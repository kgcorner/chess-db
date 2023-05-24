package com.scriptchess.util;


import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.models.Fen;
import com.scriptchess.models.Game;
import com.scriptchess.models.MoveDetails;
import com.scriptchess.services.FenCompressorDecompressor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import static com.scriptchess.util.FileUtil.readFileLines;

/**
 * Description : Handler for individual fen bucket
 * Author: kumar
 * Created on : 16/11/22
 */

public class FenBucketHandler {
    private final int bucket;
    private final ReadWriteLock lock;
    private final String fenBucketPath;
    private static final String MOVE_DETAIL_SEPARATOR = FenCompressorDecompressor.FEN_MOVE_DETAIL_SEPARATOR;
    private static final String MOVE_DETAIL_GAME_ID_SEPARATOR = "^";
    private static final String ELEMENT_SEPARATOR = ",";
    private static final String FEN_SEPARATOR = new String(FenCompressorDecompressor.FEN_MOVE_SEPARATOR);
    private static final int FENS_PER_FILE = 20000;
    private static final String FENS_EXTENSION = ".fens";
    private static final int MAX_GAMES_ID = 1000;
    private static final Logger LOGGER = LogManager.getLogger(FenBucketHandler.class);


    public FenBucketHandler(int bucket, String fenPath) {
        this.bucket = bucket;
        lock = new ReentrantReadWriteLock();
        fenBucketPath = fenPath + File.separator + bucket + FENS_EXTENSION;
    }

    public void writeFen(Fen fen) throws ChessDbException {
        List<Fen> fens = new ArrayList<>();
        fens.add(fen);
        writeFens(fens, null);
    }

    public void writeFens(List<Fen> fens, String session) throws ChessDbException {
        //Take the write lock on the bucket
        long startFenWrite = System.currentTimeMillis();
        long startNewFenMerge = System.currentTimeMillis();
        Lock lock = this.lock.writeLock();
        lock.lock();
        Map<String, Fen> fenMap = new HashMap<>();
        //Prepare fenList by combining details of unique fens
        for(Fen fen : fens) {
            String fenToCheck = fen.getFenString();
            int bucket = findFenBucket(fenToCheck);
            if(bucket != this.bucket)
                throw new IllegalArgumentException("Incorrect fen given");
            String tmpFenToCheck = fenToCheck.substring(0, fenToCheck.lastIndexOf(" "));
            //Check if the fen is prepared earlier
            if(fenMap.containsKey(tmpFenToCheck)) {
                //Fen is prepared earlier.
                Fen uFen = fenMap.get(tmpFenToCheck);
                //check if move also appeared
                for(MoveDetails md: fen.getMoveDetails()) {
                    boolean moveDetailFound = false;
                    for(MoveDetails umd: uFen.getMoveDetails()) {
                        if(md.getMove().equals(umd.getMove())) {
                            //move matched. add up the corresponding results
                            umd.setDraws(umd.getDraws() + md.getDraws());
                            umd.setBlackWins(umd.getBlackWins() + md.getBlackWins());
                            umd.setWhiteWins(umd.getWhiteWins() + md.getWhiteWins());
                            umd.setCount(umd.getWhiteWins() + umd.getBlackWins() + umd.getDraws());
                            moveDetailFound = true;
                        }
                    }
                    //if move details did not matched then add this to mapped fen's move details
                    if(!moveDetailFound) {
                        md.setCount(md.getWhiteWins() + md.getBlackWins() + md.getDraws());
                        uFen.getMoveDetails().add(md);
                    }
                }
                //add the game ids
                uFen.getGameIds().addAll(fen.getGameIds());
            } else {
                //this fen is the first one of it's kind
                fenMap.put(tmpFenToCheck, fen);
            }
        }
        long endNewFenMerge = System.currentTimeMillis();
        //LOGGER.debug("New fen merge took " + (endNewFenMerge - startNewFenMerge) +"ms for bucket " + bucket);
        //Combine the prepared fens with fens existing in the fen's bucket
        try {
            //System.out.println("Entered the Critial Zone");
            //reads the fens in the bucket
            long startFinalFenMerge = System.currentTimeMillis();
            List<String> fenLines = null;
            if(FileUtil.fileExists(fenBucketPath)) {
                fenLines = readFileLines(fenBucketPath);
            } else {
                fenLines = new ArrayList<>();
            }
            long readFenFileEnds = System.currentTimeMillis();
            //LOGGER.debug("read fen file " + bucket+".fens in " + (readFenFileEnds-startFinalFenMerge) + "ms");
            //LOGGER.debug("merging " + fenMap.size()+" fens with " + fenLines.size() +" existing fens");
            List<String> copyOfExistingFens = Collections.unmodifiableList(fenLines);
            int existingFenLines = fenLines.size();

            //Combine details of given fens with existing fens
            if(fenLines.size() > 0) {
                for(Map.Entry<String, Fen> fenEntry : fenMap.entrySet()) {
                    String tmpFenToCheck = fenEntry.getKey();
                    Fen fen = fenEntry.getValue();
                    for(String fenLine : fenLines) {
                        if(fenLine.substring(0, fenLine.indexOf(FEN_SEPARATOR)).contains(tmpFenToCheck)) {
                            //fen already exists in the bucket. Let's add the move details
                            String[] fenParts = fenLine.split(Pattern.quote(FEN_SEPARATOR));
                            Fen existingFen = readFenString(fenParts[1]);
                            existingFen.setFenString(fenParts[0]);
                            for(MoveDetails md: existingFen.getMoveDetails()) {
                                boolean moveDetailFound = false;
                                for(MoveDetails umd: fen.getMoveDetails()) {
                                    if(md.getMove().equals(umd.getMove())) {
                                        //move details matched. Add up the corresponding result
                                        umd.setDraws(umd.getDraws() + md.getDraws());
                                        umd.setBlackWins(umd.getBlackWins() + md.getBlackWins());
                                        umd.setWhiteWins(umd.getWhiteWins() + md.getWhiteWins());
                                        moveDetailFound = true;
                                    }
                                }
                                if(!moveDetailFound) {
                                    //move details of existing fen in bucket didn't match with new fen. add this move
                                    // detail to new fen as we will be writing the new fen

                                    fen.getMoveDetails().add(md);
                                }
                            }
                            //if the game ids count is equal to {@code MAX_GAMES_ID} then leave it otherwise,
                            // add existing game ids and strip down to count {@code MAX_GAMES_ID}
                            if(fen.getGameIds().size() != MAX_GAMES_ID) {
                                //add game ids
                                fen.getGameIds().addAll(existingFen.getGameIds());
                                //if game ids count exceeds max count then remove the older entries
                                if (fen.getGameIds().size() > MAX_GAMES_ID) {
                                    for (int i = fen.getGameIds().size() - 1; i > MAX_GAMES_ID - 1; i--) {
                                        fen.getGameIds().remove(i);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                //add the non-matching fens to the fenMap
                for(String fenLine : fenLines) {
                    String tmpFenToCheck = fenLine.substring(0, fenLine.indexOf(FEN_SEPARATOR))
                        .substring(0, fenLine.lastIndexOf(" "));
                    if(!fenMap.containsKey(tmpFenToCheck)) {
                        String[] fenParts = fenLine.split(Pattern.quote(FEN_SEPARATOR));
                        Fen existingFen = readFenString(fenParts[1]);
                        existingFen.setFenString(fenParts[0]);
                        fenMap.put(tmpFenToCheck, existingFen);
                    }
                }
            }
            for(String existingFen : copyOfExistingFens) {
                String tmpFenToCheck = existingFen.substring(0, existingFen.indexOf(FEN_SEPARATOR))
                    .substring(0, existingFen.lastIndexOf(" "));
                if(!fenMap.containsKey(tmpFenToCheck)) {
                    LOGGER.error("Fen entry " + existingFen + " deleted in " + session);
                }
            }
            long endFinalFenMerge = System.currentTimeMillis();
            //LOGGER.debug("final fen merge took " + (endFinalFenMerge - startFinalFenMerge) +"ms for bucket " + bucket);
            writeFensToBucket(fenMap, session);
            long endFenWrite = System.currentTimeMillis();
            LOGGER.info("For bucket " + bucket +" read took " + DateUtil.getTimeDiff(readFenFileEnds, startFinalFenMerge)
                + " merge took " + DateUtil.getTimeDiff(endFinalFenMerge, readFenFileEnds) +
                " write took " + DateUtil.getTimeDiff(endFenWrite, startFenWrite) +" new fens count: " + fens.size()+" existing fens " + fenLines.size());
            //LOGGER.debug("Fen write for bucket " + bucket +" took " + (endFenWrite - startFenWrite) + "ms");
            FenDbHandler.bucketTimeMap.put(bucket, (endFenWrite - startFenWrite));
        } catch (IOException e) {
            throw new ChessDbException(e);
        }
        finally {
            //Release the write lock
            //System.out.println("Leaving the Critial Zone");
            lock.unlock();
        }
    }

    public void writeFensToBucket(Map<String, Fen> fenMap, String session) throws IOException {
        //List<String> finalFenStringList = new ArrayList<>();
        List<byte[]> fenByteArrList = new ArrayList<>();
        for(Map.Entry<String, Fen> entries : fenMap.entrySet())  {
            Fen fen = entries.getValue();
            String fenString = fen.getFenString() + FEN_SEPARATOR + getFenString(fen);
            if(fenString.contains("null")) {
                //LOGGER.error("Found null entry for fen in session : " + session);
            }
            byte[] fenStringByteArr = FenCompressorDecompressor.getFenMoveInByteArray(fenString);
            fenByteArrList.add(fenStringByteArr);
        }
        //Write the final Fen Strings to the bucket
        FileUtil.writeByteArrayListData(fenByteArrList, true, fenBucketPath);
    }

    public static Fen readFenString(String fenString) {
        String[] fenStringParts = fenString.split(Pattern.quote(MOVE_DETAIL_GAME_ID_SEPARATOR));
        if(fenStringParts.length != 2)
            return null;
        Fen fen = new Fen();
        fen.setMoveDetails(new ArrayList<>());
        String[] moveDetails = fenStringParts[0].split(Pattern.quote(MOVE_DETAIL_SEPARATOR));
        for(String moveDetailStr : moveDetails) {
            MoveDetails md = new MoveDetails();
            String[] parts= moveDetailStr.split(Pattern.quote(ELEMENT_SEPARATOR));
            if(parts.length == 4) {
                md.setMove(parts[0]);
                md.setWhiteWins(Integer.parseInt(parts[1]));
                md.setBlackWins(Integer.parseInt(parts[2]));
                md.setDraws(Integer.parseInt(parts[3]));
                fen.getMoveDetails().add(md);
            }
        }

        String[] gameIds = fenStringParts[1].split(Pattern.quote(ELEMENT_SEPARATOR));
        fen.setGameIds(new ArrayList<>());
        fen.getGameIds().addAll(Arrays.asList(gameIds));
        return fen;
    }

    public static String getFenString(Fen fen) {
        StringBuilder sb = new StringBuilder();
        for(MoveDetails md : fen.getMoveDetails()) {
            sb.append(md.getMove()).append(ELEMENT_SEPARATOR)
                .append(md.getWhiteWins()).append(ELEMENT_SEPARATOR)
                .append(md.getBlackWins()).append(ELEMENT_SEPARATOR)
                .append(md.getDraws()).append(MOVE_DETAIL_SEPARATOR);
        }
        sb.append(MOVE_DETAIL_GAME_ID_SEPARATOR);
        for(String gameId : fen.getGameIds()) {
            sb.append(gameId).append(ELEMENT_SEPARATOR);
        }
        String fenString = sb.toString();
        fenString = fenString.replace(MOVE_DETAIL_SEPARATOR + MOVE_DETAIL_GAME_ID_SEPARATOR , MOVE_DETAIL_GAME_ID_SEPARATOR);
        fenString = fenString.substring(0, fenString.length() -1);
        return fenString;
    }

    public static int findFenBucket(String fen) {
        BigInteger bi = new BigInteger(fen.getBytes());
        BigInteger threshold = new BigInteger("1000000000");
        BigInteger mod = bi.mod(threshold);
        int bucket = mod.intValue();
        bucket /= FENS_PER_FILE;
        return bucket;
    }

    public static void main(String[] args) {
        System.out.println(findFenBucket("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }
}