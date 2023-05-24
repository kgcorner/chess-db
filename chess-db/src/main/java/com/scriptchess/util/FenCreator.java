package com.scriptchess.util;


import com.scriptchess.models.Fen;
import com.scriptchess.models.Game;
import com.scriptchess.models.MoveDetails;
import com.scriptchess.services.FenCompressorDecompressor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 29/10/22
 */

public class FenCreator {
    private static final String MOVE_DETAIL_SEPARATOR = "|";
    private static final String MOVE_DETAIL_GAME_ID_SEPARATOR = "^";
    private static final String ELEMENT_SEPARATOR = ",";
    private static final String FEN_SEPARATOR = new String(FenCompressorDecompressor.FEN_MOVE_SEPARATOR);
    private static final int FENS_PER_FILE = 20000;
    private static final String FENS_EXTENSION = ".fens";
    private static final int MAX_GAMES_ID = 1000;
    public static long byteWritten = 0;
    private static final Logger LOGGER = LogManager.getLogger(FenCreator.class);

    private static Fen readFenString(String fenString) {
        String[] fenStringParts = fenString.split(Pattern.quote(MOVE_DETAIL_GAME_ID_SEPARATOR));
        Fen fen = new Fen();
        fen.setMoveDetails(new ArrayList<>());
        String[] moveDetails = fenStringParts[0].split(Pattern.quote(MOVE_DETAIL_SEPARATOR));
        for(String moveDetailStr : moveDetails) {
            MoveDetails md = new MoveDetails();
            String[] parts= moveDetailStr.split(Pattern.quote(ELEMENT_SEPARATOR));
            md.setMove(parts[0]);
            md.setWhiteWins(Integer.parseInt(parts[1]));
            md.setBlackWins(Integer.parseInt(parts[2]));
            md.setDraws(Integer.parseInt(parts[3]));
            fen.getMoveDetails().add(md);
        }
        String[] gameIds = fenStringParts[1].split(Pattern.quote(ELEMENT_SEPARATOR));
        fen.setGameIds(new ArrayList<>());
        fen.getGameIds().addAll(Arrays.asList(gameIds));

        return fen;
    }

    private static int findFenBucket(String fen) {
        BigInteger bi = new BigInteger(fen.getBytes());
        BigInteger threshold = new BigInteger("1000000000");
        BigInteger mod = bi.mod(threshold);
        int bucket = mod.intValue();
        bucket /= FENS_PER_FILE;
        return bucket;
    }



    public static Fen findFen(String fen, String fensPath) {
        int bucket = findFenBucket(fen);
        try {
            byte[] allBytes = FileUtil.readBytes(fensPath + File.separator + bucket + ".fens");
            List<byte[]> fenByteArrayList = splitByteArray(allBytes, FenCompressorDecompressor.FEN_STRING_SEPARATOR);
            String matchingFenString = FenCompressorDecompressor.getMatchingFenString(fenByteArrayList, fen);
            if(!Strings.isNullOrEmpty(matchingFenString)) {
                String[] split = matchingFenString.split(Pattern.quote(FEN_SEPARATOR));
                if(split.length == 2) {
                    Fen fenObj =  readFenString(split[1]);
                    fenObj.setFenString(split[0]);
                    return fenObj;
                }
            }
            return null;
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public static List<byte[]> splitByteArray(byte[] array, byte[] delimiter) {
        List<byte[]> list = new ArrayList<>();
        Byte[] tmp = new Byte[0];
        List<Byte> tmpList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            boolean found = true;
            boolean end = false;
            for (int j = 0; j < delimiter.length; j++) {
                if(array.length > (i+j)) {
                    if(array[i+j] == delimiter[j]) {
                        continue;
                    } else {
                        found = false;
                        break;
                    }
                } else {
                    end = true;
                }

            }
            if(found || end) {
                if(tmpList.size() > 0) {
                    tmp = tmpList.toArray(tmp);
                    byte[] tmpArry = new byte[tmp.length];
                    for (int j = 0; j < tmp.length; j++) {
                        tmpArry[j] = tmp[j];
                    }
                    list.add(tmpArry);
                    tmp = new Byte[0];
                    tmpList = new ArrayList<>();
                    i+=(delimiter.length -1);
                }

            } else {
                tmpList.add(array[i]);
            }
        }
        return list;
    }
}