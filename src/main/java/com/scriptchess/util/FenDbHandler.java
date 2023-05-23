package com.scriptchess.util;


import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.models.Fen;
import com.scriptchess.models.Game;
import com.scriptchess.models.MoveDetails;
import com.scriptchess.services.UpdateCallBack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/11/22
 */

@Component
public class FenDbHandler {


    @Value("${fens.builder.thread.count}")
    private int fenBuilderThreadCount;

    @Value("${fens.path}")
    private String fensPath;

    @Value("${node.js.path}")
    private String nodeJsPath;
    public static Map<Integer, Long> bucketTimeMap = new HashMap<>();

    private ExecutorService dbExecutor;
    private CountDownLatch latch;

    private static final Logger LOGGER = LogManager.getLogger(FenDbHandler.class);

    private Map<Integer, FenBucketHandler> fenBucketHandlerMap = new HashMap<>();

    //Prepares fens handler map
    public void prepareFensHandlers() {
        List<String> files = FileUtil.getFiles(fensPath);
        for(String file : files) {
            if(!fenBucketHandlerMap.containsKey(file)) {
                file = file.replace(".fens","");
                Integer bucketNum = Integer.parseInt(file);
                fenBucketHandlerMap.put(bucketNum, new FenBucketHandler(bucketNum, fensPath));
            }
        }
    }

    public void createFens(List<Game> games, String session, UpdateCallBack callBack) throws ChessDbException {
        List<Game> finalGameList = new ArrayList<>();
        Map<Integer, List<Fen>> fenListBucketMap = new HashMap<>();
        int count = 0;
        int fenCountSentForCreation = 0;
        Set<String> uniqueFens = new HashSet<>();

        long startFenCollection = System.currentTimeMillis();
        for(Game game : games) {
            long start= System.currentTimeMillis();
            List<String> fenStrings = game.getFens();

            if(fenStrings == null || fenStrings.size() == 0) {
                fenStrings = ChessJsUtil.getFens(game.getPgn(), nodeJsPath);
            }
            uniqueFens.addAll(fenStrings);
            long end= System.currentTimeMillis();
            LOGGER.debug("Created fens for game " + ++count + " in " + (end - start) + "ms");
            if(game.getMoves().size() + 1  != fenStrings.size()) {
                LOGGER.error("Fen length is not matching for game :" + game.getGameId()+" below is game pgn");
                LOGGER.error(game.getPgn());
            } else {
                finalGameList.add(game);
                for (int i = 0; i < game.getMoves().size(); i++) {
                    String fenString = fenStrings.get(i);
                    int bucket = FenBucketHandler.findFenBucket(fenString);
                    MoveDetails md = new MoveDetails();
                    md.setMove(game.getMoves().get(i).getMove());
                    switch (game.getResult()) {
                        case "1-0":
                            md.setWhiteWins(1);
                            break;
                        case "0-1":
                            md.setBlackWins(1);
                            break;
                        case "1/2":
                        case "1/2-1/2":
                            md.setDraws(1);
                            break;
                    }
                    Fen fen = new Fen();
                    fen.setFenString(fenString);
                    List<MoveDetails> mds = new ArrayList<>();
                    List<String> gameIds = new ArrayList<>();
                    gameIds.add(game.getGameId());
                    mds.add(md);
                    fen.setMoveDetails(mds);
                    fen.setGameIds(gameIds);
                    if(!fenListBucketMap.containsKey(bucket)) {
                        fenListBucketMap.put(bucket, new ArrayList<>());
                    }
                    fenListBucketMap.get(bucket).add(fen);
                }
            }
        }
        long endFenCollection = System.currentTimeMillis();
        LOGGER.info("Fen collection for all games took " + DateUtil.getTimeDiff(endFenCollection , startFenCollection));
        long startFenCreate = System.currentTimeMillis();
        bucketTimeMap.clear();
        for(Map.Entry<Integer, List<Fen>> entry : fenListBucketMap.entrySet()) {
            fenCountSentForCreation+=entry.getValue().size();
        }
        executeHandlers(fenListBucketMap, session, callBack);
        long endFenCreate = System.currentTimeMillis();
        LOGGER.info("Fen creation for all games took " + DateUtil.getTimeDiff(endFenCreate , startFenCreate));
        double avg = 0;
        for(Map.Entry<Integer, Long> entry : bucketTimeMap.entrySet()) {
            avg+=entry.getValue();
        }
        avg = avg / bucketTimeMap.size();
        LOGGER.info("Created "+ fenCountSentForCreation + " fens in " + bucketTimeMap.size()+" buckets avg time taken per bucket is " + avg +"ms");
    }
    public void createFens(List<Game> games) throws ChessDbException {
        createFens(games, null, null);
    }

    private void executeHandlers(Map<Integer, List<Fen>> fenListBucketMap, String session, UpdateCallBack callBack) throws ChessDbException {
        dbExecutor = Executors.newFixedThreadPool(fenBuilderThreadCount);
        latch = new CountDownLatch(fenListBucketMap.size());
        List<Fen> failedFens = new ArrayList<>();
        int count = 0;
        for(Map.Entry<Integer, List<Fen>> fenBucketEntry : fenListBucketMap.entrySet()) {
            int bucket = fenBucketEntry.getKey();
            List<Fen> fens = fenBucketEntry.getValue();
            if(!fenBucketHandlerMap.containsKey(bucket)) {
                fenBucketHandlerMap.put(bucket, new FenBucketHandler(bucket, fensPath));
            }
            //LOGGER.info("Submitted job " + ++count);
            dbExecutor.submit(()-> {
                try {

                    fenBucketHandlerMap.get(bucket).writeFens(fens, session);
                    if(callBack != null)
                        callBack.updated(fens);
                } catch (ChessDbException e) {
                    LOGGER.error("Failed fen writing: " ,e);
                    failedFens.addAll(fens);
                    callBack.updated("Error: " );
                }
                finally {
                    latch.countDown();
                    //LOGGER.info("Latch count : " + latch.getCount());
                }
            });
        }
        try {
            latch.await();
            //LOGGER.info("All fens written");
            if(failedFens.size() > 0) {
                LOGGER.error("failed to write: " + failedFens.size()+" fens");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            dbExecutor.shutdown();
        }
    }

    private void executeHandlers(Map<Integer, List<Fen>> fenListBucketMap, UpdateCallBack callBack) throws ChessDbException {
        executeHandlers(fenListBucketMap, null, null);
    }


    public String getFensPath() {
        return fensPath;
    }

    public void setFensPath(String fensPath) {
        this.fensPath = fensPath;
    }

    public String getNodeJsPath() {
        return nodeJsPath;
    }

    public void setNodeJsPath(String nodeJsPath) {
        this.nodeJsPath = nodeJsPath;
    }
}