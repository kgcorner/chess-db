package com.scriptchess.services.parsers;

import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import com.scriptchess.models.Tournament;
import com.scriptchess.util.PGNDateParser;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 09/05/23
 */

public class ByteWisePGNProcessorTest {

    private static final String PGN = "[Event \"Live Chess - chess\"]\n" +
        "    [Site \"Chess.com\"]\n" +
        "    [Date \"2023.05.11\"]\n" +
        "    [Round \"?\"]\n" +
        "    [White \"muzzleplayer\"]\n" +
        "    [Black \"Jordy-tk\"]\n" +
        "    [Result \"1-0\"]\n" +
        "    [TimeControl \"600\"]\n" +
        "    [WhiteElo \"1577\"]\n" +
        "    [BlackElo \"1583\"]\n" +
        "    [Termination \"muzzleplayer won by resignation\"]\n" +
        "    \n" +
        "    1. e4 e5 2. Nf3 d6 3. Bc4 Bg4 4. d3 Nf6 5. h3 Bh5 6. Nc3 c6 7. g4 Bg6 8. Ng5 h6\n" +
        "    9. Nf3 Nbd7 10. Be3 b5 1-0";

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
        "[Date \"2023.05.01\"]\n" +
        "[WhiteClock \"0:09:34\"]\n" +
        "[BlackClock \"0:09:28\"]\n" +
        "\n" +
        "1. e4 {[%clk 0:10:00]}  c5 {[%clk 0:10:01]} 2. Nc3 {[%clk 0:10:00]}  Nc6 {[%clk 0:10:00]} 3. Bb5 {[%clk 0:10:00]}  e5 {[%clk 0:09:40]} 4. d3 {[%clk 0:09:51]}  Nd4 {[%clk 0:09:36]} 5. Bc4 {[%clk 0:09:47]}  d6 {[%clk 0:09:28]} 6. Nge2 {[%clk 0:09:34]} 1/2-1/2";

    private static final Game expectedGame = new Game();
    private ByteWisePGNProcessor pgnProcessor = new ByteWisePGNProcessor();
    @Before
    public void setup() {
        expectedGame.setRound("?");
        expectedGame.setPgn("[Event \"Live Chess - chess\"]\n" +
            "[Site \"Chess.com\"]\n" +
            "[Date \"2023.05.11\"]\n" +
            "[Round \"?\"]\n" +
            "[White \"muzzleplayer\"]\n" +
            "[Black \"Jordy-tk\"]\n" +
            "[Result \"1-0\"]\n" +
            "[TimeControl \"600\"]\n" +
            "[WhiteElo \"1577\"]\n" +
            "[BlackElo \"1583\"]\n" +
            "[Termination \"muzzleplayer won by resignation\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 d6 3. Bc4 Bg4 4. d3 Nf6 5. h3 Bh5 6. Nc3 c6 7. g4 Bg6 8. Ng5 h6\n" +
            "9. Nf3 Nbd7 10. Be3 b5 1-0");
        expectedGame.setWhiteWinner(true);
        Player whitePlayer = new Player();
        whitePlayer.setElo(1577);
        whitePlayer.setName("muzzleplayer");
        Player blackPlayer = new Player();
        blackPlayer.setName("Jordy-tk");
        blackPlayer.setElo(1583);
        expectedGame.setBlackPlayer(blackPlayer);
        expectedGame.setWhitePlayer(whitePlayer);
        expectedGame.setSite("Chess.com");
        expectedGame.setDate(PGNDateParser.parseDate("2023.05.11"));
        expectedGame.setResult("1-0");
        expectedGame.setEvent("Live Chess - chess");
        Tournament tournament = new Tournament();
        tournament.setName(expectedGame.getEvent());
        expectedGame.setTournament(tournament);
        Map<String, String> otherDetails = new HashMap<>();
        otherDetails.put("TimeControl", "600");
        otherDetails.put("Termination", "muzzleplayer won by resignation");

        List<Move> moves = new ArrayList<>();
        moves.add(new Move(1, "e4", 0));
        moves.add(new Move(1, "e5", 0));
        moves.add(new Move(2, "Nf3", 0));
        moves.add(new Move(2, "d6", 0));
        moves.add(new Move(3, "Bc4", 0));
        moves.add(new Move(3, "Bg4", 0));
        moves.add(new Move(4, "d3", 0));
        moves.add(new Move(4, "Nf6", 0));
        moves.add(new Move(5, "h3", 0));
        moves.add(new Move(5, "Bh5", 0));
        moves.add(new Move(6, "Nc3", 0));
        moves.add(new Move(6, "c6", 0));
        moves.add(new Move(7, "g4", 0));
        moves.add(new Move(7, "Bg6", 0));
        moves.add(new Move(8, "Ng5", 0));
        moves.add(new Move(8, "h6", 0));
        moves.add(new Move(9, "Nf3", 0));
        moves.add(new Move(9, "Nbd7", 0));
        moves.add(new Move(10, "Be3", 0));
        moves.add(new Move(10, "b5", 0));
        expectedGame.setMoves(moves);
        expectedGame.setOtherDetails(otherDetails);
    }

    @Test
    public void parsePgn() {
        Game game = pgnProcessor.parsePgn(PGN);
        assertNotNull(game);
        assertEquals(expectedGame.getDate().getDate(), game.getDate().getDate());
        assertEquals(expectedGame.getDate().getMonth(), game.getDate().getMonth());
        assertEquals(expectedGame.getDate().getYear(), game.getDate().getYear());
        assertEquals(expectedGame.getSite(), game.getSite());
        assertEquals(expectedGame.isWhiteWinner(), game.isWhiteWinner());
        assertEquals(expectedGame.getWhitePlayer().getName(), game.getWhitePlayer().getName());
        assertEquals(expectedGame.getBlackPlayer().getName(), game.getBlackPlayer().getName());
        assertEquals(expectedGame.getResult(), game.getResult());
        assertEquals(expectedGame.getEvent(), game.getEvent());
        assertEquals(expectedGame.exportInPgn(), game.exportInPgn());
        assertEquals(expectedGame.getOtherDetails(), game.getOtherDetails());
    }

    @Test
    public void parseMultiGamePgn() {
        List<Game> games = pgnProcessor.parseMultiGamePgn(MULTI_GAME_PGN.getBytes());
        assertEquals(2, games.size());
        Game firstGame = games.get(0);
        Game secondGame = games.get(1);
        assertEquals("Aronian, Levon", firstGame.getWhitePlayer().getName());
        assertEquals("Kollars, Dmitrij", firstGame.getBlackPlayer().getName());
        assertEquals("Shevchenko, Kirill", secondGame.getWhitePlayer().getName());
        assertEquals("Andreikin, Dmitry", secondGame.getBlackPlayer().getName());
    }

    @Test
    public void supports() {
        assertTrue(pgnProcessor.supports(PGN));
    }
}