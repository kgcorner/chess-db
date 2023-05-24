package com.scriptchess.data;


import com.scriptchess.annotations.ChessDbService;
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
import java.nio.file.Path;
import java.util.*;

/**
 * Description : Data Access Object class for Games data
 * Author: kumar
 * Created on : 16/09/22
 */

@Component
public class GamesDao {
    private static final Logger LOGGER = LogManager.getLogger(GamesDao.class);

    @Value("${games.path}")
    private String gamesPath;

    @Value("${games.per.file}")
    private int gamesPerFile;

    /**
     * Saves the given game and return the zero based index on which it is saved
     * @param game
     * @return
     */
    public int saveGame(Game game) throws DAOException {
        int savedIndex = 0;
        if(game == null || game.getMoves() == null || game.getMoves().size() == 0)
            return -1;
        //Save game
        try {
            List<String> gameFiles = DirectoryUtil.getFiles(gamesPath);
            String latestGameFileName = "1";
            int count = 0;
            for(String name : gameFiles) {
                int num = Integer.valueOf(name);
                count = count > num ? count : num;
            }
            latestGameFileName = count + "";
            List<Game> games = (List<Game>) SerializerDeserializerUtil
                .deSerialize(gamesPath+ File.separator + latestGameFileName);
            if(games.size() >= gamesPerFile) {
                count++;
                latestGameFileName = count + "";
                List<Game> newGame = new ArrayList<>();
                newGame.add(game);
                SerializerDeserializerUtil.serialize(newGame, gamesPath+ File.separator + latestGameFileName);
                savedIndex = ((count - 1) * gamesPerFile);
            } else {
                games.add(game);
                SerializerDeserializerUtil.serialize(games, gamesPath+ File.separator + latestGameFileName);
                savedIndex = ((count - 1) * gamesPerFile) + games.size() - 1;
            }

        } catch (DirectoryNotFound | NotADirectoryException | IOException | ClassNotFoundException e) {
            LOGGER.error(e);
            throw new DAOException(e);
        }
        return savedIndex;
    }

    /**
     * Returns game saved on given index
     * @param gameIndex
     * @return
     */
    public Game getGame(int gameIndex) throws DAOException {
        String path = gamesPath + File.separator;
        String fileName = "";
        int indexInFile = gameIndex + 1;
        if(indexInFile % gamesPerFile == 0) {
            fileName = (indexInFile / gamesPerFile) + "";
        } else {
            fileName = ((indexInFile / gamesPerFile) + 1) + "";
        }
        path = path + fileName;
        try {
            List<Game> games = (List<Game>)SerializerDeserializerUtil.deSerialize(path);
            if(indexInFile % gamesPerFile == 0) {
                return games.get(gamesPerFile - 1);
            } else {
                return games.get((gameIndex % gamesPerFile));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Returns list of games from given list of indexes
     * @param indexes
     * @return
     * @throws DAOException
     */
    public List<Game> getGames(List<Integer> indexes) throws DAOException {
        //Collections.sort(indexes);
        List<Game> games = new ArrayList<>();
        Map<String, List<Integer>> fileMap = new HashMap<>();

        for(int index : indexes) {
            int indexInFile = index + 1;
            int mod = indexInFile / gamesPerFile;
            if(mod < 1) {
                mod++;
            } else {
                if((indexInFile % gamesPerFile) > 0) {
                    mod++;
                }
            }
            String fileName = mod + "";
            if(!fileMap.containsKey(fileName)) {
                fileMap.put(fileName, new ArrayList<>());
            }
            fileMap.get(fileName).add(index % gamesPerFile);
        }

        for(Map.Entry<String,List<Integer>> entry : fileMap.entrySet()) {
            try {
                List<Game> gamesInFile = (List<Game>)SerializerDeserializerUtil
                    .deSerialize(gamesPath + File.separator + entry.getKey());
                for(int index : entry.getValue()) {
                    games.add(gamesInFile.get(index));
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
        return games;
    }
}