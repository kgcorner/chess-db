package com.scriptchess.util;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Description : A utility class which gives functionality of chessjs by executing chessjs
 * Author: kumar
 * Created on : 09/10/22
 */

public class ChessJsUtil {
    private static final String CHESS_JS;
    private static final String ROOK_SAC_JS;
    private static final String QUEEN_SAC_JS;
    static {
        String CHESS_JS1;
        String TMP_ROOK_SAC_JS;
        String TMP_QUEEN_SAC_JS;
        try {
            CHESS_JS1 = FileUtil.readFile(ChessJsUtil.class.getResourceAsStream("/chess.js"));
            TMP_ROOK_SAC_JS = FileUtil.readFile(ChessJsUtil.class.getResourceAsStream("/check-rook-sacrifice.js"));
            TMP_QUEEN_SAC_JS = FileUtil.readFile(ChessJsUtil.class.getResourceAsStream("/check-queen-sacrifice.js"));
        } catch (IOException e) {
            CHESS_JS1 = null;
            TMP_ROOK_SAC_JS = null;
            TMP_QUEEN_SAC_JS = null;
        }
        CHESS_JS = CHESS_JS1;
        ROOK_SAC_JS = TMP_ROOK_SAC_JS;
        QUEEN_SAC_JS = TMP_QUEEN_SAC_JS;
    }

    public static synchronized List<String> getFens(String pgn, String nodeJsPath) {
        String completeJs = CHESS_JS.replace("<<pgn>>", pgn);
        String path = "/tmp/template.js";
        FileUtil.writeData(completeJs.getBytes(), true, path);
        List<String> fens = new ArrayList<>();
        Runtime runtime = RuntimeWrapper.getRuntime();

        try {
            Process process = runtime.exec(nodeJsPath+" " + path);
            if(process.waitFor()== 0) {
                InputStream inputStream = process.getInputStream();
                String allFens = IOUtils.toString(inputStream, "UTF-8");
                String[] fenArray = allFens.split("\n");
                fens = Arrays.asList(fenArray);
            }
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return fens;
    }

    /**
     * Checks if there was a rook sac done in the game.
     * A sac is assumed when a piece was given but was not recovered in next 2 moves
     * This is not an absolute definition but I am considering this as good parameter for example
     * check below game https://www.chessgames.com/nodejs/game/viewGamePGN?text=1&gid=1295215
     * @param pgn
     */
    public static synchronized String getFenWithRookSac(String pgn, String result, String nodeJsPath) throws IOException {
        return getFenWIthSac(pgn, result, nodeJsPath, ROOK_SAC_JS, "/tmp/rook_sac.js");
    }

    /**
     * Checks if there was a queen sac done in the game.
     * A sac is assumed when a piece was given but was not recovered in next 2 moves
     * This is not an absolute definition but I am considering this as good parameter for example
     * check below game https://www.chessgames.com/nodejs/game/viewGamePGN?text=1&gid=1295215
     * @param pgn
     */
    public static synchronized String getFenWithQueenSac(String pgn, String result, String nodeJsPath) throws IOException {
        return getFenWIthSac(pgn, result, nodeJsPath, QUEEN_SAC_JS, "/tmp/queen_sac.js");
    }

    private static String getFenWIthSac(String pgn, String result, String nodeJsPath, String rookSacJs, String s) throws IOException {
        String completeJs = rookSacJs.replace("<<pgn>>", pgn);
        String path = s;
        FileUtil.writeData(completeJs.getBytes(), true, path);
        try {
            Process process = RuntimeWrapper.getRuntime().exec(nodeJsPath + " " + path);
            if (process.waitFor() == 0) {
                InputStream inputStream = process.getInputStream();
                String fenData = IOUtils.toString(inputStream, "UTF-8");
                if (!Strings.isNullOrEmpty(fenData) && fenData.contains("<<turn>>")) {
                    String[] fenParts = fenData.split(Pattern.quote("<<turn>>"));
                    String fen = fenParts[0];
                    String doneBy = fenParts[1];
                    return doneBy.trim().equalsIgnoreCase("w") ?
                        (result.equalsIgnoreCase("1-0") ? fen : null)
                        : (result.equalsIgnoreCase("0-1") ? fen : null);
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }


}