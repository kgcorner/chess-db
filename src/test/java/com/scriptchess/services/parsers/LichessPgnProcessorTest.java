package com.scriptchess.services.parsers;

import com.scriptchess.models.Game;
import com.scriptchess.models.Move;
import com.scriptchess.models.Player;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class LichessPgnProcessorTest {

    private LichessPgnProcessor parser;
    private String pgn = null;
    @Before
    public void setUp() throws Exception {
        pgn = "[Event \"Rated Rapid game\"]\n" +
            "[Site \"https://lichess.org/gGu1F7Cc\"]\n" +
            "[Date \"2022.09.06\"]\n" +
            "[White \"CarlosBOliveira\"]\n" +
            "[Black \"muzzleplayer\"]\n" +
            "[Result \"0-1\"]\n" +
            "[WhiteElo \"1454\"]\n" +
            "[BlackElo \"1505\"]\n" +
            "[TimeControl \"600+0\"]\n" +
            "[Termination \"Normal\"]\n" +
            "[UTCDate \"2022.09.16\"]\n" +
            "[UTCTime \"03:09:20\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C45\"]\n" +
            "[Opening \"Scotch Game: Schmidt Variation\"]\n" +
            "[Annotator \"https://lichess.org/@/muzzleplayer\"]\n" +
            "\n" +
            "1. e4 { [%clk 0:10:00] } 1... e5 { [%clk 0:10:00] } 2. Nf3 { [%clk 0:09:58] } 2...Nc6 { [%clk 0:09:59] } 3. d4 { [%clk 0:09:57] } 3... exd4 { [%clk 0:09:58] } 4. Nxd4 { [%clk 0:09:56] } 4... Nf6 { [%clk 0:09:56] } 5. Nxc6 { [%clk 0:09:54] } 5... bxc6 { [%clk 0:09:56] } 6. Bd3 { [%clk 0:09:53] } 6... Bc5 { [%clk 0:09:54] } 7. b3 { [%clk 0:09:52] } 7... O-O { [%clk 0:09:52] } 8. Bg5 { [%clk 0:09:50] } 8... d6 { [%clk 0:09:49] } 9. Qf3 { [%clk 0:09:49] } 9... h6 { [%clk 0:09:44] } 10. Bxf6 { [%clk 0:09:48] } 10... gxf6 { [%clk 0:09:44] } 11. Qh5 { [%clk 0:09:37] } 11... f5 { [%clk 0:09:17] } 12. exf5 { [%clk 0:09:33] } 12... Qg5 { [%clk 0:09:16] } 13. Qxg5+ { [%clk 0:09:31] } 13... hxg5 { [%clk 0:09:16] } 14. Nd2 { [%clk 0:09:28] } 14... Bb4 { [%clk 0:09:13] } 15. Rd1 { [%clk 0:09:24] } 15... Re8+ { [%clk 0:09:07] } 16. Kf1 { [%clk 0:09:22] } 16... Re5 { [%clk 0:08:57] } 17. Rg1 { [%clk 0:09:19] } 17... Bxf5 { [%clk 0:08:55] } 18. Bxf5 { [%clk 0:09:17] } 18... Rxf5 { [%clk 0:08:55] } 19. g3 { [%clk 0:09:08] } 19... Rd5 { [%clk 0:08:52] } 20. Nf3 { [%clk 0:09:00] } 20... Rxd1+ { [%clk 0:08:50] } 21. Kg2 { [%clk 0:09:00] } 21... Rxg1+ { [%clk 0:08:48] } 22. Kxg1 { [%clk 0:08:59] } 22... Re8 { [%clk 0:08:43] } 23. Nxg5 { [%clk 0:08:57] } 23... Re1+ { [%clk 0:08:41] } 24. Kg2 { [%clk 0:08:56] } 24... Re2 { [%clk 0:08:38] } 25. h4 { [%clk 0:08:54] } 25... Rxc2 { [%clk 0:08:36] } 26. h5 { [%clk 0:08:53] } 26... Bc5 { [%clk 0:08:29] } 27. h6 { [%clk 0:08:52] } 27... f6 { [%clk 0:08:19] } 28. h7+ { [%clk 0:08:50] } 28... Kh8 { [%clk 0:08:17] } 29. Ne6 { [%clk 0:08:49] } 29... Bxf2 { [%clk 0:08:13] } 30. Nxc7 { [%clk 0:08:46] } 30... Bb6+ { 0-1 White resigns. } { [%clk 0:08:11] } 0-1\n" +
            "\n" +
            "\n";
        parser = new LichessPgnProcessor();
    }

    @Test
    public void parsePgn() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd");
        Game expectedGame = new Game();
        expectedGame.setEvent("Rated Rapid game");
        expectedGame.setSite("https://lichess.org/gGu1F7Cc");
        try {
            expectedGame.setDate(sdf.parse("2022.09.06"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Player white = new Player();
        Player black = new Player();
        white.setName("CarlosBOliveira");
        black.setName("muzzleplayer");
        expectedGame.setWhitePlayer(white);
        expectedGame.setBlackPlayer(black);
        expectedGame.setResult("0-1");
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(1, "e4", 0));
        moves.add(new Move(1, "e5", 0));
        moves.add(new Move(2, "Nf3", 0));
        moves.add(new Move(2, "Nc6", 0));
        moves.add(new Move(3, "d4", 0));
        moves.add(new Move(3, "exd4", 0));
        moves.add(new Move(4, "Nxd4", 0));
        moves.add(new Move(4, "Nf6", 0));
        moves.add(new Move(5, "Nxc6", 0));
        moves.add(new Move(5, "bxc6", 0));
        moves.add(new Move(6, "Bd3", 0));
        moves.add(new Move(6, "Bc5", 0));
        moves.add(new Move(7, "b3", 0));
        moves.add(new Move(7, "O-O", 0));
        moves.add(new Move(8, "Bg5", 0));
        moves.add(new Move(8, "d6", 0));
        moves.add(new Move(9, "Qf3", 0));
        moves.add(new Move(9, "h6", 0));
        moves.add(new Move(10, "Bxf6", 0));
        moves.add(new Move(10, "gxf6", 0));
        moves.add(new Move(11, "Qh5", 0));
        moves.add(new Move(11, "f5", 0));
        moves.add(new Move(12, "exf5", 0));
        moves.add(new Move(12, "Qg5", 0));
        moves.add(new Move(13, "Qxg5+", 0));
        moves.add(new Move(13, "hxg5", 0));
        moves.add(new Move(14, "Nd2", 0));
        moves.add(new Move(14, "Bb4", 0));
        moves.add(new Move(15, "Rd1", 0));
        moves.add(new Move(15, "Re8+", 0));
        moves.add(new Move(16, "Kf1", 0));
        moves.add(new Move(16, "Re5", 0));
        moves.add(new Move(17, "Rg1", 0));
        moves.add(new Move(17, "Bxf5", 0));
        moves.add(new Move(18, "Bxf5", 0));
        moves.add(new Move(18, "Rxf5", 0));
        moves.add(new Move(19, "g3", 0));
        moves.add(new Move(19, "Rd5", 0));
        moves.add(new Move(20, "Nf3", 0));
        moves.add(new Move(20, "Rxd1+", 0));
        moves.add(new Move(21, "Kg2", 0));
        moves.add(new Move(21, "Rxg1+", 0));
        moves.add(new Move(22, "Kxg1", 0));
        moves.add(new Move(22, "Re8", 0));
        moves.add(new Move(23, "Nxg5", 0));
        moves.add(new Move(23, "Re1+", 0));
        moves.add(new Move(24, "Kg2", 0));
        moves.add(new Move(24, "Re2", 0));
        moves.add(new Move(25, "h4", 0));
        moves.add(new Move(25, "Rxc2", 0));
        moves.add(new Move(26, "h5", 0));
        moves.add(new Move(26, "Bc5", 0));
        moves.add(new Move(27, "h6", 0));
        moves.add(new Move(27, "f6", 0));
        moves.add(new Move(28, "h7+", 0));
        moves.add(new Move(28, "Kh8", 0));
        moves.add(new Move(29, "Ne6", 0));
        moves.add(new Move(29, "Bxf2", 0));
        moves.add(new Move(30, "Nxc7", 0));
        moves.add(new Move(30, "Bb6+", 0));
        expectedGame.setMoves(moves);
        Game result = parser.parsePgn(pgn);
        assertEquals(expectedGame.getEvent(), result.getEvent());
        assertEquals(expectedGame.getSite(), result.getSite());
        assertEquals(expectedGame.getRound(), result.getRound());
        assertEquals(expectedGame.getWhitePlayer().getName(), result.getWhitePlayer().getName());
        assertEquals(expectedGame.getBlackPlayer().getName(), result.getBlackPlayer().getName());
        assertEquals(expectedGame.getMoves().size(), result.getMoves().size());
        for (int i = 0; i < expectedGame.getMoves().size(); i++) {
            Move expectedMove = expectedGame.getMoves().get(i);
            Move resultMove = result.getMoves().get(i);
            assertEquals(expectedMove.getMoveNumber(), resultMove.getMoveNumber());
            assertEquals(expectedMove.getMove(), resultMove.getMove());
        }
    }

    @Test
    public void supports() {
        assertTrue(parser.supports(pgn));
    }
}