package com.scriptchess.util;

import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.models.Fen;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.models.MoveDetails;
import com.scriptchess.services.ChessService;
import com.scriptchess.services.UpdateCallBack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 19/05/23
 */

public class FenDbHandlerTest {
    private static final Logger LOGGER = LogManager.getLogger(FenDbHandlerTest.class);
    private FenDbHandler fenDbHandler = null;
    private String fensPath = "/tmp/scriptchess/fens";
    private String gamesPath = "/tmp/scriptchess/games";
    private ChessService chessService = null;
    private List<Game> games = null;
    private int fenBuilderThreadCount = 10;
    private String nodeJsPath = "nodeJsPath";
    private Map<String, List<MoveDetails>> fenMap = new HashMap<>();
    private static final Object LOCK = new Object();
    @Before
    public void setUp() throws Exception {
        fenDbHandler = new FenDbHandler();
        Whitebox.setInternalState(fenDbHandler, "fenBuilderThreadCount", fenBuilderThreadCount);
        Whitebox.setInternalState(fenDbHandler, "nodeJsPath", nodeJsPath);
        Whitebox.setInternalState(fenDbHandler, "fensPath", fensPath);
        String game1File = FenDbHandlerTest.class.getResource("/game1.pgn").getPath();
        String game2File = FenDbHandlerTest.class.getResource("/game2.pgn").getPath();
        String data = FileUtil.readFile(game1File);
        games = JsonUtil.getList(data, Game.class);
        data = FileUtil.readFile(game2File);
        games.addAll(JsonUtil.getList(data, Game.class));
        fenMap = prepareFenMap(games);
        for(Map.Entry<String,List<MoveDetails>> mds : fenMap.entrySet()) {
            LOGGER.info(mds.getKey());
            for (MoveDetails md : mds.getValue()) {
                LOGGER.info("    " + md.getCount() +" " + md.getMove() +"-" + md.getWhiteWins() +"-" + md.getBlackWins() +"-" + md.getDraws());
            }
        }
        File file = new File(fensPath);
        file.mkdirs();
    }

    private Map<String, List<MoveDetails>> prepareFenMap(List<Game> games) {
        Map<String, List<MoveDetails>> fenMap = new HashMap<>();
        Set<String> uniqueFens = new HashSet<>();
        for (int i = 0; i < games.size(); i++) {
            List<String> fens = games.get(i).getFens();
            List<Move> moves = games.get(i).getMoves();
            uniqueFens.addAll(fens);
            for (int j = 0; j < moves.size(); j++) {
                String fen = fens.get(j);
                if(!fenMap.containsKey(fen)) {
                    fenMap.put(fen, new ArrayList<>());
                }
                List<MoveDetails> moveDetailsList = fenMap.get(fen);
                boolean found = false;
                for(MoveDetails md : moveDetailsList) {
                    if(md.getMove().equals(moves.get(j).getMove())) {
                        found = true;
                        md.setCount(md.getCount() + 1);
                        String result = games.get(i).getResult();
                        if(result.equals("1-0")) {
                            md.setWhiteWins(md.getWhiteWins() +1);
                        } else {
                            if(result.equals("0-1")) {
                                md.setBlackWins(md.getBlackWins() +1);
                            } else {
                                md.setDraws(md.getDraws() +1);
                            }
                        }
                        break;
                    }
                }
                if(!found) {
                    MoveDetails md = new MoveDetails();
                    String result = games.get(i).getResult();
                    if(result.equals("1-0")) {
                        md.setWhiteWins(md.getWhiteWins() +1);
                    } else {
                        if(result.equals("0-1")) {
                            md.setBlackWins(md.getBlackWins() +1);
                        } else {
                            md.setDraws(md.getDraws() +1);
                        }
                    }
                    md.setMove(moves.get(j).getMove());
                    md.setCount(1);
                    moveDetailsList.add(md);
                }
            }
        }
        LOGGER.info("Created " + uniqueFens.size() + " fens");
        return fenMap;
    }

    @After
    public void tearDown() throws Exception {
        List<String> files = FileUtil.getFiles(fensPath);
        for(String path : files) {
            if(!path.startsWith("/tmp")) {
                path = fensPath + File.separator + path;
            }
            FileUtil.deleteFile(path);
        }
        FileUtil.deleteFile(fensPath);
        FileUtil.deleteFile("/tmp/scriptchess");
    }

    @Test
    public void testReadAndWriteFens() throws IOException {
        Set<String> createdFens = new HashSet<>();
        String game1File = FenDbHandlerTest.class.getResource("/game1.pgn").getPath();
        String game2File = FenDbHandlerTest.class.getResource("/game2.pgn").getPath();
        String data = FileUtil.readFile(game1File);
        List<Game> tmpGames = JsonUtil.getList(data, Game.class);
        try {
            fenDbHandler.createFens(tmpGames, "session", new UpdateCallBack() {
                @Override
                public void updated(Object object) {
                    synchronized (LOCK) {
                        List<Fen> fens = (List) object;
                        for(Fen fen  : fens) {
                            createdFens.add(fen.getFenString());
                        }
                    }

                }
            });
        } catch (ChessDbException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        data = FileUtil.readFile(game2File);
        tmpGames = JsonUtil.getList(data, Game.class);
        try {
            fenDbHandler.createFens(tmpGames, "session", new UpdateCallBack() {
                @Override
                public void updated(Object object) {
                    synchronized (LOCK) {
                        List<Fen> fens = (List) object;
                        for(Fen fen  : fens) {
                            createdFens.add(fen.getFenString());
                        }
                    }

                }
            });
        } catch (ChessDbException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        LOGGER.info("Write completed");
        List<String> missingFens = new ArrayList<>();
        if(fenMap.size() != createdFens.size()) {

            for(String fen : fenMap.keySet()) {
                if(!createdFens.contains(fen)) {
                    missingFens.add(fen);
                }
            }
            LOGGER.info("Missed: ");
            for(String fen : missingFens) {
                LOGGER.info(fen);
            }
        }
        //assertEquals(fenMap.size(), createdFens.size());
        int matching = 0;
        for(Map.Entry<String, List<MoveDetails>> entry : fenMap.entrySet()) {
            try {
                Fen fen = FenCreator.findFen(entry.getKey(), fensPath);
                if (fen == null) {
                    LOGGER.info("Matching: " + matching);
                    LOGGER.info("Failed for: " + entry.getKey());
                    LOGGER.info(createdFens.contains(entry.getKey()));
                    LOGGER.info("Fen missed:"+ missingFens.contains(entry.getKey()));
                }
                assertNotNull(fen);
                List<MoveDetails> moveDetailsList = entry.getValue();
                Assert.assertEquals(moveDetailsList.size(), fen.getMoveDetails().size());
                int matchingMoves = 0;
                for (MoveDetails md : moveDetailsList) {
                    for (MoveDetails recoveredMd : fen.getMoveDetails()) {
                        if (md.getMove().equals(recoveredMd.getMove())) {
                            assertEquals(md.getBlackWins(), recoveredMd.getBlackWins());
                            assertEquals(md.getWhiteWins(), recoveredMd.getWhiteWins());
                            assertEquals(md.getDraws(), recoveredMd.getDraws());
                            assertEquals(md.getCount(), recoveredMd.getWhiteWins() + recoveredMd.getBlackWins() + recoveredMd.getDraws());
                            matchingMoves++;
                        }
                    }
                }
                assertEquals(moveDetailsList.size(), matchingMoves);
                matching++;
            } catch (Exception x) {
                LOGGER.info("Failed for : " + entry.getKey());
                x.printStackTrace();
            }
        }
    }

}