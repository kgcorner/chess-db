package com.scriptchess.data;


import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
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
 * Description : Data access Object for tournaments
 * Author: kumar
 * Created on : 16/09/22
 */

public class TournamentsDao {

    @Value("${tournament.path}")
    private String tournamentFilePath;

    private List<Tournament> tournaments = null;

    /**
     * Saves the given tournament
     * @param tournament
     * @return
     * @throws DAOException
     */
    public Tournament saveTournament(Tournament tournament) throws DAOException {
        if(tournament == null || Strings.isNullOrEmpty(tournament.getName()) || tournament.getYear() == 0)
            return null;
        extractTournaments();
        if(tournaments.contains(tournament))
            return tournament;
        tournaments.add(tournament);
        try {
            SerializerDeserializerUtil.serialize(tournaments, tournamentFilePath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
        return tournament;
    }

    /**
     * removes the given tournament
     * @param tournament
     * @throws DAOException
     */
    public void deleteTournament(Tournament tournament) throws DAOException {
        if(tournament == null || Strings.isNullOrEmpty(tournament.getName()) || tournament.getYear() == 0)
            return;
        extractTournaments();
        tournaments.remove(tournament);
        try {
            SerializerDeserializerUtil.serialize(tournaments, tournamentFilePath);
        } catch (IOException e) {
            throw new DAOException(e);
        }
    }

    /**
     * returns all the tournaments
     * @return
     */
    public List<Tournament> getTournaments() throws DAOException {
        extractTournaments();
        return tournaments;
    }

    /**
     * returns list of tournaments taken place in given year
     * @param year
     * @return
     */
    public List<Tournament> getTournamentsInYear(int year) throws DAOException {
        extractTournaments();
        List<Tournament> required = new ArrayList<>();
        for(Tournament t : tournaments) {
            if(t.getYear() == year)
                required.add(t);
        }
        return required;
    }

    private void extractTournaments() throws DAOException {
        if(!FileUtil.fileExists(tournamentFilePath)) {
            try {
                SerializerDeserializerUtil.serialize(new ArrayList<>(), tournamentFilePath);
            } catch (IOException e) {
                throw new DAOException(e);
            }
        }
        if (tournaments == null) {
            try {
                tournaments = (List) SerializerDeserializerUtil.deSerialize(tournamentFilePath);
            } catch (IOException | ClassNotFoundException e) {
                throw new DAOException(e);
            }
        }
    }
}