package com.scriptchess;

import com.google.gson.Gson;
import com.scriptchess.models.Game;
import com.scriptchess.services.parsers.PGNProcessorFactory;
import com.scriptchess.services.parsers.PgnProcessor;
import com.scriptchess.util.ChessJsUtil;
import com.scriptchess.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 24/11/22
 */

public class GameAndFenPreparer {
    private static final String tmpGameFileNamePrefix = "tmpGameFile";
    /**
     * Prepares list of games with fen from PGN file and writes that to a file
     * @param pgnFilePath
     * @param nodeJsPath
     * @return
     * @throws IOException
     */
    public String getGames(String pgnFilePath, String nodeJsPath) throws IOException {

        File file = new File(pgnFilePath);
        String pgnFileName = file.getName();
        String path = "/tmp/" + pgnFileName;
        if(FileUtil.fileExists(path)) {
            return path;
        }
        byte[] bytes = FileUtil.readBytes(pgnFilePath);
        PgnProcessor defaultProcessor = PGNProcessorFactory.getDefaultProcessor();
        List<Game> games = defaultProcessor.parseMultiGamePgn(bytes);
        for(Game game : games) {
            if(game.getResult() == null) {
                continue;
            }
            String pgn = game.getPgn();
            List<String> fens = ChessJsUtil.getFens(pgn, nodeJsPath);
            game.setFens(fens);
        }
        Gson gson = new Gson();
        String s = gson.toJson(games);

        FileUtil.writeData(s.getBytes(), true, path);
        return path;
    }
}