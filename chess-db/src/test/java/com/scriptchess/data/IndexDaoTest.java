package com.scriptchess.data;

import com.scriptchess.exceptions.DAOException;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SerializerDeserializerUtil.class, DirectoryUtil.class, FileUtil.class})
public class IndexDaoTest {

    private String playerIndexPath = "players";
    private String tournamentIndexPath = "tournament";
    private String moveIndexPath = ".moves";
    private IndexDao indexDao;
    private String sampleFideId = "fideId";
    @Before
    public void setUp() throws Exception {
        indexDao = new IndexDao();
        Whitebox.setInternalState(indexDao, "playerIndexPath", playerIndexPath);
        Whitebox.setInternalState(indexDao, "tournamentIndexPath", tournamentIndexPath);
        Whitebox.setInternalState(indexDao, "moveIndexPath", moveIndexPath);
        mockStatic(SerializerDeserializerUtil.class);
        mockStatic(DirectoryUtil.class);
        mockStatic(FileUtil.class);
    }

    @Test
    public void writePlayersIndex() throws DAOException, IOException, ClassNotFoundException {
        Player player = new Player();
        player.setName("Kumar Gaurav");
        player.setFideId(sampleFideId);
        int index = 100;
        List<Integer> existingGamesIndex = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            existingGamesIndex.add(TestUtils.generateInt(500));
        }
        String path = playerIndexPath + File.separator + sampleFideId;
        when(FileUtil.fileExists(path)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(path)).thenReturn(existingGamesIndex);
        indexDao.savePlayersIndex(player, index);
        verifyStatic(SerializerDeserializerUtil.class);
        SerializerDeserializerUtil.serialize(existingGamesIndex, path);
    }

    @Test
    public void writeNewPlayersIndex() throws DAOException, IOException, ClassNotFoundException {
        Player player = new Player();
        player.setName("Kumar Gaurav");
        player.setFideId(sampleFideId);
        int index = 100;
        String path = playerIndexPath + File.separator + sampleFideId;
        when(FileUtil.fileExists(path)).thenReturn(false);
        indexDao.savePlayersIndex(player, index);
        verifyStatic(SerializerDeserializerUtil.class);
        List<Integer> indexes = new ArrayList<>();
        indexes.add(index);
        SerializerDeserializerUtil.serialize(indexes, path);
    }

    @Test
    public void writeTournamentIndex() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sinqfield cup");
        tournament.setYear(2022);
        String tournamentPath = tournamentIndexPath + File.separator + "Sinqfield cup" + "_" + "2022";
        when(FileUtil.fileExists(tournamentPath)).thenReturn(true);
        int index = 100;
        List<Integer> existingGamesIndex = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            existingGamesIndex.add(TestUtils.generateInt(500));
        }
        when(SerializerDeserializerUtil.deSerialize(tournamentPath)).thenReturn(existingGamesIndex);
        indexDao.saveTournamentIndex(tournament, index);
        verifyStatic(SerializerDeserializerUtil.class);
        SerializerDeserializerUtil.serialize(existingGamesIndex, tournamentPath);
    }

    @Test
    public void writeNewTournamentIndex() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sinqfield cup");
        tournament.setYear(2022);
        String tournamentPath = tournamentIndexPath + File.separator + "Sinqfield cup" + "_" + "2022";
        when(FileUtil.fileExists(tournamentPath)).thenReturn(false);
        int index = 100;
        indexDao.saveTournamentIndex(tournament, index);
        verifyStatic(SerializerDeserializerUtil.class);
        List<Integer> indexes = new ArrayList<>();
        indexes.add(index);
        SerializerDeserializerUtil.serialize(indexes, tournamentPath);
    }

    @Test
    public void getGamesOfTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sinqfield cup");
        tournament.setYear(2022);
        String tournamentPath = tournamentIndexPath + File.separator + "Sinqfield cup" + "_" + "2022";
        when(FileUtil.fileExists(tournamentPath)).thenReturn(true);
        List<Integer> existingGamesIndex = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            existingGamesIndex.add(TestUtils.generateInt(500));
        }
        when(SerializerDeserializerUtil.deSerialize(tournamentPath)).thenReturn(existingGamesIndex);
        List<Integer> result = indexDao.getGamesOfTournament(tournament);
        assertEquals(existingGamesIndex, result);
    }

    @Test(expected = DAOException.class)
    public void getGamesOfNewTournament() throws IOException, ClassNotFoundException, DAOException {
        Tournament tournament = new Tournament();
        tournament.setName("Sinqfield cup");
        tournament.setYear(2022);
        String tournamentPath = tournamentIndexPath + File.separator + "Sinqfield cup" + "_" + "2022";
        when(FileUtil.fileExists(tournamentPath)).thenReturn(false);
        indexDao.getGamesOfTournament(tournament);
    }

    @Test
    public void getGamesOfPlayer() throws IOException, ClassNotFoundException, DAOException {
        Player player = new Player();
        player.setName("Kumar Gaurav");
        player.setFideId(sampleFideId);
        String path = playerIndexPath + File.separator + sampleFideId;
        when(FileUtil.fileExists(path)).thenReturn(true);
        List<Integer> existingGamesIndex = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            existingGamesIndex.add(TestUtils.generateInt(500));
        }
        when(SerializerDeserializerUtil.deSerialize(path)).thenReturn(existingGamesIndex);
        List<Integer> result = indexDao.getGamesOfPlayer(player);
        assertEquals(existingGamesIndex, result);
    }

    @Test(expected = DAOException.class)
    public void getGamesOfNewPlayer() throws IOException, ClassNotFoundException, DAOException {
        Player player = new Player();
        player.setName("Kumar Gaurav");
        player.setFideId(sampleFideId);
        String path = playerIndexPath + File.separator + sampleFideId;
        when(FileUtil.fileExists(path)).thenReturn(false);
        indexDao.getGamesOfPlayer(player);
    }

    @Test
    public void getGamesOfPlayerWithoutFideId() throws IOException, ClassNotFoundException {
        Player player = new Player();
        player.setName("Kumar Gaurav");
        try {
            indexDao.getGamesOfPlayer(player);
        } catch (DAOException e) {
            assertEquals("Only games of player that have fide id is allowed", e.getMessage());
        }
    }

    @Test
    public void getGamesFromMove() throws IOException, ClassNotFoundException, DAOException {
        int moveCount = 10;
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < moveCount; i++) {
            moves.add(new Move(i+1, TestUtils.generateString(4), 0));
        }
        StringBuilder sb = new StringBuilder();
        for(Move move : moves) {
            sb.append(move.getMove());
        }
        String totalMoveStr = sb.toString();
        String md5 = DigestUtils.md5DigestAsHex(totalMoveStr.getBytes());
        List<Integer> randomGameIndex = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            randomGameIndex.add(TestUtils.generateInt(400));
        }
        Map<String, List<Integer>> mockedExistingIndex = new HashMap<>();
        mockedExistingIndex.put(md5, randomGameIndex);
        when(FileUtil.fileExists(moveIndexPath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(moveIndexPath)).thenReturn(mockedExistingIndex);
        List<Integer> result = indexDao.getGamesFromMove(moves);
        assertEquals(randomGameIndex, result);
    }

    @Test
    public void getGamesFromMoveOfNewGame() throws IOException, ClassNotFoundException, DAOException {
        int moveCount = 10;
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < moveCount; i++) {
            moves.add(new Move(i+1, TestUtils.generateString(4), 0));
        }
        StringBuilder sb = new StringBuilder();
        for(Move move : moves) {
            sb.append(move.getMove());
        }
        String totalMoveStr = sb.toString();
        Map<String, List<Integer>> mockedExistingIndex = new HashMap<>();
        when(FileUtil.fileExists(moveIndexPath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(moveIndexPath)).thenReturn(mockedExistingIndex);
        List<Integer> result = indexDao.getGamesFromMove(moves);
        assertEquals(0, result.size());
    }

    @Test
    public void saveMoveSequence() throws IOException, ClassNotFoundException, DAOException {
        int moveCount = 10;
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < moveCount; i++) {
            moves.add(new Move(i+1, TestUtils.generateString(4), 0));
        }
        Map<String, List<Integer>> mockedExistingIndex = new TreeMap<>();
        int exitingMovesCount = 7;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < exitingMovesCount; i++) {
            List<Integer> randomGameIndex = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                randomGameIndex.add(TestUtils.generateInt(400));
            }
            sb.append(moves.get(i).getMove());
            String moveStrMD5 =  DigestUtils.md5DigestAsHex(sb.toString().getBytes());
            mockedExistingIndex.put(moveStrMD5, randomGameIndex);
        }
        when(FileUtil.fileExists(moveIndexPath)).thenReturn(true);
        when(SerializerDeserializerUtil.deSerialize(moveIndexPath)).thenReturn(mockedExistingIndex);
        int index = 100;

        indexDao.saveMoveSequence(moves, index);
        Map<String, List<Integer>> expectedFinalMoveIndexMap = new HashMap<>();
        for(Map.Entry<String, List<Integer>> entry : mockedExistingIndex.entrySet()) {
            expectedFinalMoveIndexMap.put(entry.getKey(), entry.getValue());
        }
        sb = new StringBuilder();
        List<Integer> newIndex = new ArrayList<>();
        newIndex.add(index);
        for (int i = 0; i < moveCount; i++) {
            sb.append(moves.get(i).getMove());
            if(i > exitingMovesCount) {
                expectedFinalMoveIndexMap.put(DigestUtils.md5DigestAsHex(sb.toString().getBytes()), newIndex);
            }
        }
        verifyStatic(SerializerDeserializerUtil.class);
        SerializerDeserializerUtil.serialize(expectedFinalMoveIndexMap, moveIndexPath);
    }


}