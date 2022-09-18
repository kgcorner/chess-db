package com.scriptchess.data;

import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.DirectoryUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SerializerDeserializerUtil.class, DirectoryUtil.class, FileUtil.class})
public class TournamentsDaoTest {

    @Value("${tournament.path}")
    private String tournamentFilePath = "tournaments";

    private TournamentsDao tournamentsDao;

    @Before
    public void setUp() {
        tournamentsDao = new TournamentsDao();
        Whitebox.setInternalState(tournamentsDao, "tournamentFilePath", tournamentFilePath);
        mockStatic(SerializerDeserializerUtil.class);
        mockStatic(DirectoryUtil.class);
        mockStatic(FileUtil.class);
    }

    @Test
    public void saveTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sample tournament");
        tournament.setYear(2022);
        List<Tournament> existingTournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            existingTournaments.add(t);
        }
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        tournamentsDao.saveTournament(tournament);
        assertEquals(11, existingTournaments.size());
        assertTrue(existingTournaments.contains(tournament));
    }

    @Test
    public void saveFirstTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sample tournament");
        tournament.setYear(2022);
        List<Tournament> existingTournaments = new ArrayList<>();
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(false);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        tournamentsDao.saveTournament(tournament);
        assertEquals(1, existingTournaments.size());
        assertTrue(existingTournaments.contains(tournament));
    }

    @Test
    public void deleteTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sample tournament");
        tournament.setYear(2022);
        List<Tournament> existingTournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            existingTournaments.add(t);
        }
        existingTournaments.add(tournament);
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        tournamentsDao.deleteTournament(tournament);
        assertEquals(10, existingTournaments.size());
        assertFalse(existingTournaments.contains(tournament));
    }

    @Test
    public void deleteNonExistingTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sample tournament");
        tournament.setYear(2022);
        List<Tournament> existingTournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            existingTournaments.add(t);
        }
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        tournamentsDao.deleteTournament(tournament);
        assertEquals(10, existingTournaments.size());
        assertFalse(existingTournaments.contains(tournament));
    }

    @Test
    public void getTournaments() throws IOException, ClassNotFoundException, DAOException {
        List<Tournament> existingTournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            existingTournaments.add(t);
        }
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        List<Tournament> result = tournamentsDao.getTournaments();
        assertEquals(existingTournaments, result);
    }

    @Test
    public void getTournamentsInYear()  throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sample tournament");
        tournament.setYear(2020);
        List<Tournament> existingTournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(2022);
            existingTournaments.add(t);
        }
        existingTournaments.add(tournament);
        when(FileUtil.fileExists(tournamentFilePath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(tournamentFilePath)).thenReturn(existingTournaments);
        List<Tournament> result = tournamentsDao.getTournamentsInYear(2022);
        assertEquals(10, result.size());
        for(Tournament t : result) {
            assertEquals(2022, t.getYear());
        }
    }
}