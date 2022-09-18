package com.scriptchess.data;


import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.Strings;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Description : Data access Object for Accessing index of games of players or tournament
 * Author: kumar
 * Created on : 17/09/22
 */

public class IndexDao {

    private final static Logger LOGGER = Logger.getLogger(IndexDao.class);

    @Value("${players.index.path}")
    private String playerIndexPath;

    @Value("${tournament.index.path}")
    private String tournamentIndexPath;

    @Value("${move.index.path}")
    private String moveIndexPath;

    private final List<Player> players = new ArrayList<>();

    /**
     * Saves index of games of given player
     * @param player
     * @param index
     * @throws DAOException
     */
    public void savePlayersIndex(Player player, int index) throws DAOException {
        if(Strings.isNullOrEmpty(player.getFideId()))
            throw new IllegalArgumentException("Saving player's game that doesn't has " +
                "fide id is not allowed");
        String fideId = player.getFideId();
        List<Integer> indexes = null;
        final String path = playerIndexPath + File.separator + fideId;
        if(FileUtil.fileExists(path)) {
            try {
                indexes = (List<Integer>) SerializerDeserializerUtil
                    .deSerialize(path);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        } else {
            indexes = new ArrayList<>();
        }
        indexes.add(index);
        try {
            SerializerDeserializerUtil.serialize(indexes,
                path);
        } catch (IOException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Saves index of games of given tournament
     * @param tournament
     * @param index
     * @throws DAOException
     */
    public void saveTournamentIndex(Tournament tournament, int index) throws DAOException {
        String tournamentPath = tournamentIndexPath + File.separator + tournament.getName() + "_" + tournament.getYear();
        List<Integer> indexes = null;
        if(FileUtil.fileExists(tournamentPath)) {
            try {
                indexes = (List<Integer>) SerializerDeserializerUtil
                    .deSerialize(tournamentPath);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        } else {
            indexes = new ArrayList<>();
        }
        indexes.add(index);
        try {
            SerializerDeserializerUtil.serialize(indexes,tournamentPath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Returns games of given tournament
     * @param tournament
     * @return
     * @throws DAOException
     */
    public List<Integer> getGamesOfTournament(Tournament tournament) throws DAOException {
        String tournamentPath = tournamentIndexPath + File.separator + tournament.getName() + "_" + tournament.getYear();
        List<Integer> indexes = null;
        if(!FileUtil.fileExists(tournamentPath)) {
            throw new DAOException("No such tournament found");
        } else {
            try {
                indexes = (List<Integer>) SerializerDeserializerUtil
                    .deSerialize(tournamentPath);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
        return indexes;
    }

    /**
     * Returns games of
     * @param player
     * @return
     * @throws DAOException
     */
    public List<Integer> getGamesOfPlayer(Player player) throws DAOException {
        if(Strings.isNullOrEmpty(player.getFideId()))
            throw new DAOException("Only games of player that have fide id is allowed");;
        final String path = playerIndexPath + File.separator + player.getFideId();
        List<Integer> indexes = null;
        if(!FileUtil.fileExists(path)) {
            throw new DAOException("No such player found");
        } else {
            try {
                indexes = (List<Integer>) SerializerDeserializerUtil
                    .deSerialize(path);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
        return indexes;
    }

    /**
     * Returns index of games that has certain move sequence
     * @param moves
     * @return
     * @throws DAOException
     */
    public List<Integer> getGamesFromMove(List<Move>moves) throws DAOException {
        StringBuilder sb = new StringBuilder();
        for(Move move : moves) {
            sb.append(move.getMove());
        }
        String moveMD5 = Strings.getMd5(sb.toString());
        if(FileUtil.fileExists(moveIndexPath)) {
            try {
                Map<String, List<Integer>> moveIndexMap = (Map<String, List<Integer>>) SerializerDeserializerUtil
                    .deSerialize(moveIndexPath);
                if(moveIndexMap.containsKey(moveMD5)) {
                    return moveIndexMap.get(moveMD5);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Saves index on moves
     * @param moves
     * @param index
     * @throws DAOException
     */
    public void saveMoveSequence(List<Move> moves, int index) throws DAOException {
        if(moves == null || moves.size() == 0) return;
        Map<String, List<Integer>> moveIndexMap = null;
        if(FileUtil.fileExists(moveIndexPath)) {
            try {
                moveIndexMap = (Map<String, List<Integer>>) SerializerDeserializerUtil
                    .deSerialize(moveIndexPath);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        } else {
            moveIndexMap = new HashMap<>();
        }
        List<String> movesMD5List = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(Move move : moves) {
            sb.append(move.getMove());
            movesMD5List.add(Strings.getMd5(sb.toString()));
        }
        //add index on existing moves
        for(Map.Entry<String, List<Integer>> entry : moveIndexMap.entrySet()) {
            if(movesMD5List.contains(entry.getKey())) {
                entry.getValue().add(index);
                movesMD5List.remove(entry.getKey());
            }
        }
        List<Integer> newIndex = new ArrayList<>();
        newIndex.add(index);
        //add indexes on new moves
        for(String moveMd5 : movesMD5List) {
            moveIndexMap.put(moveMd5, newIndex);
        }
        try {
            SerializerDeserializerUtil.serialize(moveIndexMap, moveIndexPath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
    }
}