package com.scriptchess.services;

import com.scriptchess.data.*;
import com.scriptchess.exceptions.ChessDbException;
import com.scriptchess.exceptions.DAOException;
import com.scriptchess.exceptions.UnSupportedPgn;
import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.services.parsers.ByteWisePGNProcessor;
import com.scriptchess.services.parsers.PGNProcessorFactory;
import com.scriptchess.services.parsers.PgnProcessor;
import com.scriptchess.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 18/09/22
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(PGNProcessorFactory.class)
public class ChessServiceTest {
    private GamesDao gamesDao;
    private IndexDao indexDao;
    private PlayersDao playersDao;
    private TournamentsDao tournamentsDao;
    private MovesDao movesDao;
    private ChessService service;
    private PgnProcessor pgnProcessor;

    private static final String MULTI_GAME_PGN = "[Site \"https://www.chess.com\"]\n" +
        "[Event \"Play-In\"]\n" +
        "[White \"Aronian, Levon\"]\n" +
        "[Black \"Kollars, Dmitrij\"]\n" +
        "[WhiteFideId \"13300474\"]\n" +
        "[BlackFideId \"12909572\"]\n" +
        "[WhiteElo \"2721\"]\n" +
        "[BlackElo \"2522\"]\n" +
        "[Result \"1/2-1/2\"]\n" +
        "[Round \"09\"]\n" +
        "[TimeControl \"600+2\"]\n" +
        "[Date \"2023.05.01\"]\n" +
        "[WhiteClock \"0:03:06\"]\n" +
        "[BlackClock \"0:02:02\"]\n" +
        "\n" +
        "\n" +
        "\n" +
        "1. e4 {[%clk 0:10:01]}  e5 {[%clk 0:09:54] (e4)} 2. Nf3 {[%clk 0:10:02]}  Nc6 {[%clk 0:09:54]} 3. Bc4 {[%clk 0:10:03]}  Nf6 {[%clk 0:09:53]} 4. d3 {[%clk 0:10:04]}  Bc5 {[%clk 0:09:52]} 5. O-O {[%clk 0:10:05]}  d6 {[%clk 0:09:50]} 6. c3 {[%clk 0:10:04]}  a5 {[%clk 0:09:50]} 7. Re1 {[%clk 0:09:59]}  O-O {[%clk 0:09:48]} 8. h3 {[%clk 0:09:56]}  h6 {[%clk 0:09:33]} 9. Nbd2 {[%clk 0:09:56]}  Be6 {[%clk 0:09:26]} 10. Bb5 {[%clk 0:09:56]}  Nd7 {[%clk 0:09:22]} 11. Nf1 {[%clk 0:09:53]}  d5 {[%clk 0:09:22]} 12. Ne3 {[%clk 0:09:51]}  dxe4 {[%clk 0:08:35]} 13. dxe4 {[%clk 0:09:52]}  Bd6 {[%clk 0:08:36]} 14. Nf5 {[%clk 0:09:41]}  Nc5 {[%clk 0:08:14]} 15. Nxd6 {[%clk 0:08:22]}  cxd6 {[%clk 0:07:35]} 16. Nd2 {[%clk 0:08:09]}  d5 {[%clk 0:07:21]} 17. exd5 {[%clk 0:07:53]}  Qxd5 {[%clk 0:07:21]} 18. Nf1 {[%clk 0:07:21]}  Qxd1 {[%clk 0:05:43]} 19. Rxd1 {[%clk 0:07:21]}  Rfd8 {[%clk 0:05:44]} 20. Be3 {[%clk 0:07:16]}  Ne4 {[%clk 0:05:44]} 21. Rxd8+ {[%clk 0:06:50]}  Nxd8 {[%clk 0:05:23]} 22. Bd3 {[%clk 0:06:38]}  Nf6 {[%clk 0:04:58]} 23. a4 {[%clk 0:06:37]}  Nd5 {[%clk 0:04:51]} 24. Bd2 {[%clk 0:06:32]}  Nc6 {[%clk 0:04:51]} 25. Re1 {[%clk 0:06:24]}  Nb6 {[%clk 0:04:25]} 26. Bb5 {[%clk 0:06:10]}  Bb3 {[%clk 0:04:26]} 27. Be3 {[%clk 0:05:58]}  Nc4 {[%clk 0:03:53]} 28. Nd2 {[%clk 0:05:50]}  Nxd2 {[%clk 0:03:48]} 29. Bxd2 {[%clk 0:05:52]}  f6 {[%clk 0:03:41]} 30. Be3 {[%clk 0:05:38]}  Na7 {[%clk 0:03:20]} 31. Be2 {[%clk 0:05:32]}  Nc6 {[%clk 0:03:20]} 32. Bb6 {[%clk 0:05:26]}  Ne7 {[%clk 0:02:53]} 33. Bd1 {[%clk 0:05:11]}  Bc4 {[%clk 0:02:43]} 34. Bc5 {[%clk 0:04:30]}  Nd5 {[%clk 0:02:39]} 35. b3 {[%clk 0:04:30]}  Bd3 {[%clk 0:02:36]} 36. c4 {[%clk 0:04:08]}  Rc8 {[%clk 0:02:24]} 37. Ba3 {[%clk 0:03:51]}  Nb4 {[%clk 0:02:25]} 38. Bf3 {[%clk 0:03:32]}  Nc2 {[%clk 0:02:17]} 39. Rd1 {[%clk 0:03:22]}  e4 {[%clk 0:02:15]} 40. Bg4 $ { [%clk 0:03:22]}  Rc6 {[%clk 0:02:01]} 41. Bb2 ({[%clk 0:03:07]})  Rb6{[%clk 0:02:02]} 42. c5({[%clk 0:03:06]}) 1/2-1/2\n" +
        "\n" +
        "\n" +
        "\n" +
        "\n" +
        "[Site \"https://www.chess.com\"]\n" +
        "[Event \"Play-In\"]\n" +
        "[White \"Shevchenko, Kirill\"]\n" +
        "[Black \"Andreikin, Dmitry\"]\n" +
        "[WhiteFideId \"14129574\"]\n" +
        "[BlackFideId \"4158814\"]\n" +
        "[WhiteElo \"2598\"]\n" +
        "[BlackElo \"2628\"]\n" +
        "[Result \"1/2-1/2\"]\n" +
        "[Round \"09\"]\n" +
        "[TimeControl \"600+2\"]\n" +
        "[Tournament \"Daily\"]\n" +
        "[Date \"2023.05.01\"]\n" +
        "[WhiteClock \"0:09:34\"]\n" +
        "[BlackClock \"0:09:28\"]\n" +
        "\n" +
        "1. e4 {[%clk 0:10:00]}  c5 {[%clk 0:10:01]} 2. Nc3 {[%clk 0:10:00]}  Nc6 {[%clk 0:10:00]} 3. Bb5 {[%clk 0:10:00]}  e5 {[%clk 0:09:40]} 4. d3 {[%clk 0:09:51]}  Nd4 {[%clk 0:09:36]} 5. Bc4 {[%clk 0:09:47]}  d6 {[%clk 0:09:28]} 6. Nge2 {[%clk 0:09:34]} 1/2-1/2";
        private static final String GAME_STORAGE_PATH = "/tmp/test-games";

    @Before
    public void setUp() throws Exception {
        service = new ChessService();
        gamesDao = mock(GamesDao.class);
        indexDao = mock(IndexDao.class);
        playersDao = mock(PlayersDao.class);
        tournamentsDao = mock(TournamentsDao.class);
        movesDao = mock(MovesDao.class);
        pgnProcessor = mock(PgnProcessor.class);

        mockStatic(PGNProcessorFactory.class);
        Whitebox.setInternalState(service, "gamesDao", gamesDao);
        Whitebox.setInternalState(service, "indexDao", indexDao);
        Whitebox.setInternalState(service, "playersDao", playersDao);
        Whitebox.setInternalState(service, "tournamentsDao", tournamentsDao);
        Whitebox.setInternalState(service, "movesDao", movesDao);
        Whitebox.setInternalState(service, "gamesPath", GAME_STORAGE_PATH);
        when(PGNProcessorFactory.getProcessor(ArgumentMatchers.anyString())).thenReturn(pgnProcessor);
    }

    @Test
    public void saveGame() throws DAOException, UnSupportedPgn {
        //Here PGN doesn't has to be perfect for testing as all of our parsers are Unit tested
        String pgn = "Sample Pgn";
        Player whitePlayer = new Player();
        whitePlayer.setFideId("Some fideID");
        Player blackPlayer = new Player();
        blackPlayer.setFideId("Some other fideID");
        Tournament tournament = new Tournament();
        List<Move> moves = new ArrayList<>();
        Game game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setTournament(tournament);
        game.setMoves(moves);
        int index = 100;
        when(PGNProcessorFactory.getProcessor(pgn)).thenReturn(pgnProcessor);
        when(pgnProcessor.parsePgn(pgn)).thenReturn(game);
        when(gamesDao.saveGame(game)).thenReturn(index);

        service.saveGame(pgn);
        verify(indexDao, times(1)).saveTournamentIndex(tournament, index);
        verify(movesDao, times(1)).saveMoves(game, index);
        verify(indexDao, times(1)).saveMoveSequence(moves, index);
        verify(indexDao, times(1)).savePlayersIndex(whitePlayer, index);
        verify(indexDao, times(1)).savePlayersIndex(blackPlayer, index);
    }

    @Test(expected = RuntimeException.class)
    public void saveGameWithException() throws DAOException, UnSupportedPgn {
        //Here PGN doesn't has to be perfect for testing as all of our parsers are Unit tested
        String pgn = "Sample Pgn";
        Player whitePlayer = new Player();
        whitePlayer.setFideId("Some fideID");
        Player blackPlayer = new Player();
        blackPlayer.setFideId("Some other fideID");
        Tournament tournament = new Tournament();
        List<Move> moves = new ArrayList<>();
        Game game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setTournament(tournament);
        game.setMoves(moves);
        int index = 100;
        when(PGNProcessorFactory.getProcessor(pgn)).thenReturn(pgnProcessor);
        when(pgnProcessor.parsePgn(pgn)).thenReturn(game);
        when(gamesDao.saveGame(game)).thenReturn(index);
        doThrow(new DAOException()).when(indexDao).saveTournamentIndex(tournament, index);
        service.saveGame(pgn);
    }

    @Test
    public void getGamesOfPlayer() throws DAOException {
        String fideId = "sampleFideId";
        Player player = new Player();
        List<Game> games = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            indexes.add(TestUtils.generateInt(50));
        }

        for (int i = 0; i < 10; i++) {
            Game g = new Game();
            g.setSite(TestUtils.generateString(10));
            games.add(g);
        }

        when(playersDao.getPlayer(fideId)).thenReturn(player);
        when(indexDao.getGamesOfPlayer(player)).thenReturn(indexes);
        when(gamesDao.getGames(indexes)).thenReturn(games);
        List<Game> result = service.getGamesOfPlayer(fideId);
        assertEquals(games, result);
    }

    @Test(expected = RuntimeException.class)
    public void getGamesOfPlayerWithException() throws DAOException {
        String fideId = "sampleFideId";
        Player player = new Player();
        List<Game> games = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            indexes.add(TestUtils.generateInt(50));
        }

        for (int i = 0; i < 10; i++) {
            Game g = new Game();
            g.setSite(TestUtils.generateString(10));
            games.add(g);
        }

        when(playersDao.getPlayer(fideId)).thenThrow(new DAOException());
        service.getGamesOfPlayer(fideId);
    }

    @Test
    public void getGamesOfTournament() throws DAOException {
        String name = "Tournament";
        int year = 2000;
        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setYear(year);
        List<Game> games = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            indexes.add(TestUtils.generateInt(50));
        }

        for (int i = 0; i < 10; i++) {
            Game g = new Game();
            g.setSite(TestUtils.generateString(10));
            games.add(g);
        }

        when(indexDao.getGamesOfTournament(tournament)).thenReturn(indexes);
        when(gamesDao.getGames(indexes)).thenReturn(games);
        List<Game> result = service.getGamesOfTournament(name, year);
        assertEquals(games, result);
    }

    @Test
    public void getGamesAfterMoves() throws DAOException {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            moves.add(new Move(0, TestUtils.generateString(4), 0));
        }
        List<Game> games = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            indexes.add(TestUtils.generateInt(50));
        }

        for (int i = 0; i < 10; i++) {
            Game g = new Game();
            g.setSite(TestUtils.generateString(10));
            games.add(g);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) {
            if((i + 1) == moves.size() )
                sb.append(moves.get(i).getMove());
            else
                sb.append(moves.get(i).getMove() + ">");
        }
        when(indexDao.getGamesFromMove(moves)).thenReturn(indexes);
        when(gamesDao.getGames(indexes)).thenReturn(games);
        List<Game> result = service.getGamesAfterMoves(sb.toString());
        assertEquals(games, result);
    }

    @Test
    public void getMovesAndGameCountAfterMove() throws DAOException {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            moves.add(new Move(0, TestUtils.generateString(4), 0));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) {
            if((i + 1) == moves.size() )
                sb.append(moves.get(i).getMove());
            else
                sb.append(moves.get(i).getMove() + ">");
        }
        Map<String, Integer> movesMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            movesMap.put(TestUtils.generateString(4), TestUtils.generateInt(5));
        }
        when(movesDao.getMovesAndGameCountAfter(moves)).thenReturn(movesMap);
        Map<String, Integer> result = service.getMovesAndGameCountAfterMove(sb.toString());
        assertEquals(movesMap, result);
    }

    @Test
    public void savePlayer() throws DAOException {
        Player player = new Player();
        player.setFideId(TestUtils.generateString(5));
        when(playersDao.savePlayer(player)).thenReturn(player);
        Player result = service.savePlayer(player);
        assertEquals(player, result);
    }

    @Test
    public void getPlayers() throws DAOException {
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Player player = new Player();
            player.setFideId(TestUtils.generateString(5));
            playerList.add(player);
        }
        when(playersDao.getPlayers()).thenReturn(playerList);
        List<Player> result = service.getPlayers();
        assertEquals(playerList, result);
    }

    @Test
    public void updatePlayer() throws DAOException {
        String fideId = "fideId";
        Player player = new Player();
        player.setFideId(TestUtils.generateString(5));
        when(playersDao.updatePlayer(player, fideId)).thenReturn(player);
        Player result = service.updatePlayer(player, fideId);
        assertEquals(player, result);
    }

    @Test(expected = RuntimeException.class)
    public void deletePlayerException() throws DAOException {
        String fideId = "fideId";
        doThrow(new DAOException()).when(playersDao).deletePlayer(fideId);
        service.deletePlayer(fideId);
    }

    @Test
    public void getPlayer() throws DAOException {
        String fideId = TestUtils.generateString(5);
        Player player = new Player();
        player.setFideId(fideId);
        when(playersDao.getPlayer(fideId)).thenReturn(player);
        Player result = service.getPlayer(fideId);
        assertEquals(player, result);
    }

    @Test
    public void saveTournament() throws DAOException {
        Tournament tournament = new Tournament();
        tournament.setName(TestUtils.generateString(5));
        when(tournamentsDao.saveTournament(tournament)).thenReturn(tournament);
        Tournament result = service.saveTournament(tournament);
        assertEquals(tournament, result);
    }

    @Test(expected = RuntimeException.class)
    public void deleteTournament() throws DAOException {
        Tournament tournament = new Tournament();
        tournament.setName(TestUtils.generateString(5));
        doThrow(new DAOException()).when(tournamentsDao).deleteTournament(tournament);
        service.deleteTournament(tournament);
    }

    @Test
    public void getTournaments() throws DAOException {
        List<Tournament> tournaments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            tournaments.add(t);
        }
        when(tournamentsDao.getTournaments()).thenReturn(tournaments);
        List<Tournament> result = service.getTournaments();
        assertEquals(tournaments, result);
    }

    @Test
    public void getTournamentsInYear() throws DAOException {
        List<Tournament> tournaments = new ArrayList<>();
        List<Tournament> tournamentsIn2022 = new ArrayList<>();
        int year = 2022;
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(2022);
            tournaments.add(t);
            tournamentsIn2022.add(t);
        }
        for (int i = 0; i < 10; i++) {
            Tournament t = new Tournament();
            t.setName(TestUtils.generateString(15));
            t.setYear(TestUtils.generateInt(2022));
            tournaments.add(t);
        }
        when(tournamentsDao.getTournamentsInYear(year)).thenReturn(tournamentsIn2022);
        List<Tournament> result = service.getTournamentsInYear(year);
        assertEquals(tournamentsIn2022, result);
    }

    @Test
    public void testSaveAndReadGames() throws ChessDbException {
        String session = "session";
        List<Game> games = new ByteWisePGNProcessor().parseMultiGamePgn(MULTI_GAME_PGN.getBytes());
        List<Game> writtenGames = service.saveGames(games, session, object -> {
            assertTrue(games.contains(object));
        }, object -> {

        });
        Game game = service.getGame(writtenGames.get(0).getGameId());
        assertEquals(writtenGames.get(0).getEvent(), game.getEvent());
    }
}