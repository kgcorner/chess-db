package com.scriptchess.data;


import com.scriptchess.exceptions.DAOException;
import com.scriptchess.exceptions.DirectoryNotFound;
import com.scriptchess.exceptions.FileExists;
import com.scriptchess.exceptions.NotADirectoryException;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.util.DirectoryUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Description : Data access Object for Moves
 * Author: kumar
 * Created on : 16/09/22
 */
@Component
public class MovesDao {
    private static final Logger LOGGER = LogManager.getLogger(GamesDao.class);
    private static final String COUNT_FILE_NAME = ".count";
    private static final String GAME_INDEX_FILE_NAME = ".games";

    @Value("${moves.path}")
    private String movesPath;

    /**
     * Saves moves of given game
     * @param game
     * @param gameIndex
     * @return
     * @throws DAOException
     */
    public boolean saveMoves(Game game, int gameIndex) throws DAOException {
        if(game == null || game.getMoves() == null || game.getMoves().size() == 0)
            return false;
        StringBuilder sbPath = new StringBuilder();
        sbPath.append(movesPath);
        for(Move move : game.getMoves()) {
            String moveNotation = move.getMove();
            sbPath.append(File.separator + moveNotation);
            DirectoryUtil.createDirectory(sbPath.toString());
            String countFilePath = sbPath.toString()+ File.separator + COUNT_FILE_NAME;
            try {
                FileUtil.createFile(countFilePath);
            } catch (FileExists fileExists) {
                //LOGGER.error(fileExists);
            } catch (IOException e) {
                //LOGGER.error(e);
            }

            try {
                //Write Move count
                String data = FileUtil.readFile(countFilePath);
                if(Strings.isNullOrEmpty(data)) data = "0";

                int moveCount = Integer.parseInt(data);
                moveCount++;
                FileUtil.writeData(Integer.toString(moveCount).getBytes(), true, countFilePath);
            } catch (IOException | FileExists e) {
                LOGGER.error(e);
                throw new DAOException(e);
            }
        }
        return true;
    }

    public Map<String, Integer> getMovesAndGameCountAfter(List<Move> moves) throws DAOException {
        Map<String, Integer> movesAndGameCountMap = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append(movesPath);
        for(Move move : moves) {
            String moveNotation = move.getMove();
            sb.append(File.separator + moveNotation);
        }
        String moveDirPath = sb.toString();
        try {
            List<String> directories = DirectoryUtil.getDirectories(moveDirPath);
            for(String dir : directories) {
                String cnt = FileUtil.readFile(moveDirPath + File.separator + dir +
                    File.separator + COUNT_FILE_NAME);
                movesAndGameCountMap.put(dir, Integer.parseInt(cnt));
            }
        } catch (DirectoryNotFound directoryNotFound) {
            Collections.emptyMap();
        } catch (NotADirectoryException | IOException e) {
            throw new DAOException(e);
        }
        return movesAndGameCountMap;
    }
}