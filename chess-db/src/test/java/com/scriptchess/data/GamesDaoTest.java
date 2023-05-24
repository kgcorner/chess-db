package com.scriptchess.data;

import com.scriptchess.exceptions.DAOException;
import com.scriptchess.exceptions.DirectoryNotFound;
import com.scriptchess.exceptions.NotADirectoryException;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.DirectoryUtil;
import com.scriptchess.util.FileUtil;
import com.scriptchess.util.SerializerDeserializerUtil;
import com.scriptchess.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SerializerDeserializerUtil.class, DirectoryUtil.class, FileUtil.class})
public class GamesDaoTest {

    private String gamesPath = "/games";
    private int gamesPerFile = 10;
    private GamesDao gamesDao;
    private List<String> mockedGameFiles = new ArrayList<>();
    private List<Game> mockedGamesListUnder10 = new ArrayList<>();
    private List<Game> mockedGamesList10 = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        gamesDao = new GamesDao();
        Whitebox.setInternalState(gamesDao, "gamesPath", gamesPath);
        Whitebox.setInternalState(gamesDao, "gamesPerFile", gamesPerFile);
        mockStatic(SerializerDeserializerUtil.class);
        mockStatic(DirectoryUtil.class);
        mockStatic(FileUtil.class);
        for (int i = 1; i < 10; i++) {
            mockedGameFiles.add(i+"");
        }
        for (int i = 0; i < 5; i++) {
            mockedGamesListUnder10.add(new Game());
        }

        for (int i = 0; i < 10; i++) {
            mockedGamesList10.add(new Game());
        }
    }



    @Test
    public void testSaveNullGame() throws DAOException {
        assertEquals(-1, gamesDao.saveGame(null));
    }

    @Test(expected = DAOException.class)
    public void testNonExistingGameDirectory() throws DAOException, NotADirectoryException, DirectoryNotFound {
        when(DirectoryUtil.getFiles(gamesPath)).thenThrow(new DirectoryNotFound());
        List<Move> moves = new ArrayList<>();
        Game expectedGame = new Game();
        int moveCount = 20;
        for(int i = 1; i<=moveCount; i++) {
            Move wMove = new Move(i, TestUtils.generateString(4), 0);
            Move bMove = new Move(i, TestUtils.generateString(4), 0);
            moves.add(wMove);
            moves.add(bMove);
        }
        expectedGame.setMoves(moves);
        gamesDao.saveGame(expectedGame);
    }

    @Test
    public void saveGame() throws NotADirectoryException, DirectoryNotFound, IOException, ClassNotFoundException, DAOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd");
        Game expectedGame = new Game();
        expectedGame.setEvent("WorldChess Gaming");
        expectedGame.setSite("worldchess.com");
        try {
            expectedGame.setDate(sdf.parse("2021.09.17"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        expectedGame.setRound("1");
        Player white = new Player();
        Player black = new Player();
        white.setName("Kumar Gaurav");
        white.setFideId("366093876");
        black.setName("Rabulan, Leo");
        black.setFideId("5207517");
        expectedGame.setWhitePlayer(white);
        expectedGame.setBlackPlayer(black);
        Tournament tournament = new Tournament();
        tournament.setName("Daily Tournament");
        expectedGame.setTournament(tournament);
        expectedGame.setResult("0-1");
        List<Move> moves = new ArrayList<>();
        int moveCount = 20;
        for(int i = 1; i<=moveCount; i++) {
            Move wMove = new Move(i, TestUtils.generateString(4), 0);
            Move bMove = new Move(i, TestUtils.generateString(4), 0);
            moves.add(wMove);
            moves.add(bMove);
        }
        expectedGame.setMoves(moves);
        when(DirectoryUtil.getFiles(gamesPath)).thenReturn(mockedGameFiles);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + mockedGameFiles.size() +""))
            .thenReturn(mockedGamesListUnder10);
        int index = gamesDao.saveGame(expectedGame);
        PowerMockito.verifyStatic(SerializerDeserializerUtil.class);
        SerializerDeserializerUtil.serialize(mockedGamesListUnder10, gamesPath + File.separator + mockedGameFiles.size() +"");
        assertEquals((mockedGameFiles.size() - 1) * gamesPerFile + mockedGamesListUnder10.size() - 1, index);
    }

    @Test
    public void saveGameWithLastFileFull() throws NotADirectoryException, DirectoryNotFound, IOException, ClassNotFoundException, DAOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd");
        Game expectedGame = new Game();
        expectedGame.setEvent("WorldChess Gaming");
        expectedGame.setSite("worldchess.com");
        try {
            expectedGame.setDate(sdf.parse("2021.09.17"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        expectedGame.setRound("1");
        Player white = new Player();
        Player black = new Player();
        white.setName("Kumar Gaurav");
        white.setFideId("366093876");
        black.setName("Rabulan, Leo");
        black.setFideId("5207517");
        expectedGame.setWhitePlayer(white);
        expectedGame.setBlackPlayer(black);
        Tournament tournament = new Tournament();
        tournament.setName("Daily Tournament");
        expectedGame.setTournament(tournament);
        expectedGame.setResult("0-1");
        List<Move> moves = new ArrayList<>();
        int moveCount = 20;
        for(int i = 1; i<=moveCount; i++) {
            Move wMove = new Move(i, TestUtils.generateString(4), 0);
            Move bMove = new Move(i, TestUtils.generateString(4), 0);
            moves.add(wMove);
            moves.add(bMove);
        }
        expectedGame.setMoves(moves);
        when(DirectoryUtil.getFiles(gamesPath)).thenReturn(mockedGameFiles);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + mockedGameFiles.size() +""))
            .thenReturn(mockedGamesList10);
        int index = gamesDao.saveGame(expectedGame);
        PowerMockito.verifyStatic(SerializerDeserializerUtil.class);
        List<Game> newArr = new ArrayList<>();
        newArr.add(expectedGame);
        SerializerDeserializerUtil.serialize(newArr, gamesPath + File.separator + (mockedGameFiles.size() + 1) +"");
        assertEquals((mockedGameFiles.size()) * gamesPerFile, index);
    }

    @Test
    public void getGameAsGamesPerFile() throws IOException, ClassNotFoundException, DAOException {
        int index = 10;
        Game mockedGame = new Game();
        List<Game> newArr = new ArrayList<>();
        newArr.add(mockedGame);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "2")).thenReturn(newArr);
        Game result = gamesDao.getGame(index);
        assertEquals(mockedGame, result);
    }

    @Test
    public void getGame() throws IOException, ClassNotFoundException, DAOException {
        int index = 9;
        Game mockedGame = new Game();
        List<Game> newArr = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            newArr.add(new Game());
        }
        newArr.add(mockedGame);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "1")).thenReturn(newArr);
        Game result = gamesDao.getGame(index);
        assertEquals(mockedGame, result);
    }

    @Test(expected = DAOException.class)
    public void getGameException() throws IOException, ClassNotFoundException, DAOException {
        int index = 9;
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "1")).thenThrow(new IOException());
        Game result = gamesDao.getGame(index);
    }

    @Test
    public void getGames() throws IOException, ClassNotFoundException, DAOException {
        List<Integer> indexes = new ArrayList<>();
        indexes.add(8);
        indexes.add(18);
        indexes.add(14);
        indexes.add(24);
        indexes.add(38);
        indexes.add(39);
        indexes.add(48);
        Game mockedGame = new Game();
        List<Game> newArr = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if(i == 8 || i == 4 || i == 9)
                newArr.add(mockedGame);
            else
                newArr.add(new Game());
        }
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "1")).thenReturn(newArr);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "2")).thenReturn(newArr);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "3")).thenReturn(newArr);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "4")).thenReturn(newArr);
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "5")).thenReturn(newArr);
        List<Game> result = gamesDao.getGames(indexes);
        for(Game g : result) {
            assertEquals(mockedGame, g);
        }
    }

    @Test(expected = DAOException.class)
    public void getGamesException() throws IOException, ClassNotFoundException, DAOException {
        List<Integer> indexes = new ArrayList<>();
        indexes.add(8);
        indexes.add(18);
        indexes.add(14);
        indexes.add(24);
        indexes.add(38);
        indexes.add(39);
        indexes.add(48);
        Game mockedGame = new Game();
        List<Game> newArr = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if(i == 8 || i == 4 || i == 9)
                newArr.add(mockedGame);
            else
                newArr.add(new Game());
        }
        when(SerializerDeserializerUtil.deSerialize(gamesPath + File.separator + "1")).thenThrow(new IOException());
        gamesDao.getGames(indexes);
    }
}