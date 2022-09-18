package com.scriptchess.data;


import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Player;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.Strings;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description : DAO class for Players
 * Author: kumar
 * Created on : 16/09/22
 */

public class PlayersDao {

    @Value("${players.path}")
    private String playersFilePath;


    private Map<String, Player> players = null;

    /**
     * Saves the given player
     * @param player
     * @throws DAOException
     */
    public Player savePlayer(Player player) throws DAOException {
        extractPlayers();
        if(player == null || Strings.isNullOrEmpty(player.getFideId())) {
            throw new DAOException("Player without Fide id is not allowed");
        }

        players.put(player.getFideId(), player);
        try {
            SerializerDeserializerUtil.serialize(players, playersFilePath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
        return player;
    }

    /**
     * returns list of existing players
     * @return
     * @throws DAOException
     */
    public List<Player> getPlayers() throws DAOException {
        extractPlayers();
        List<Player> playerList = new ArrayList<>();
        for(String key : players.keySet()) {
            playerList.add(players.get(key));
        }
        return playerList;
    }

    /**
     * populate the player's cache
     * @throws DAOException
     */
    private void extractPlayers() throws DAOException {
        if(!FileUtil.fileExists(playersFilePath)) {
            try {
                SerializerDeserializerUtil.serialize(new HashMap<>(), playersFilePath);
            } catch (IOException e) {
                throw new DAOException(e);
            }
        }
        if (players == null) {
            try {
                players = (Map<String, Player>) SerializerDeserializerUtil.deSerialize(playersFilePath);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
    }

    /**
     * Deletes the player from players list
     * @param fideId
     * @throws DAOException
     */
    public void deletePlayer(String fideId) throws DAOException {
        if(Strings.isNullOrEmpty(fideId)) return;
        extractPlayers();
        players.remove(fideId);
        try {
            SerializerDeserializerUtil.serialize(players, playersFilePath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Updates the given player
     * @param player
     * @return
     * @throws DAOException
     */
    public Player updatePlayer(Player player, String fideId) throws DAOException {
        extractPlayers();
        if(Strings.isNullOrEmpty(player.getFideId()) || Strings.isNullOrEmpty(fideId)) {
            throw new DAOException("Player without Fide id is not allowed");
        }
        players.remove(fideId);
        players.put(player.getFideId(), player);
        try {
            SerializerDeserializerUtil.serialize(players, playersFilePath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
        return player;
    }

    /**
     * returns the player of given fideId
     * @param fideId
     * @return
     * @throws DAOException
     */
    public Player getPlayer(String fideId) throws DAOException {
        extractPlayers();
        if(Strings.isNullOrEmpty(fideId)) {
            throw new DAOException("Player without Fide id is not allowed");
        }
        return players.get(fideId);
    }
}