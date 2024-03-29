package com.scriptchess.models;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 16/09/22
 */

public class GameTest {

    private Game game;
    private String expectedPgn = "[Event \"Live Chess\"]\n" +
        "[Site \"Chess.com\"]\n" +
        "[Date \"2022.09.15\"]\n" +
        "[Round \"?\"]\n" +
        "[White \"JonnyJamesf\"]\n" +
        "[Black \"muzzleplayer\"]\n" +
        "[Result \"0-1\"]\n" +
        "[ECO \"B10\"]\n" +
        "[WhiteElo \"1434\"]\n" +
        "[BlackElo \"1496\"]\n" +
        "[TimeControl \"600\"]\n" +
        "[EndTime \"7:17:00 PDT\"]\n" +
        "[Termination \"muzzleplayer won by checkmate\"]\n" +
        "\n" +
        "1. e4 c6 2. Nf3 d5 3. e5 Bg4 4. d4 e6 5. h3 Bh5 6. g4 Bg6 7. Bg5 f6 8. exf6 Nxf6 9. Ne5 Bd6 10. Nxg6 hxg6 11. Bd3 Qa5+ 12. c3 Ne4 13. Bxe4 dxe4 14. Be3 g5 15. f3 exf3 16. Qxf3 Nd7 17. Qe4 Bg3+ 18. Kd1 O-O-O 19. Bxg5 Qxg5 20. Rf1 Rxh3 21. Nd2 e5 22. dxe5 Nxe5 23. Kc2 Rxd2+ 24. Kb3 Nd7 25. Qe8+ Kc7 26. Rad1 Qb5+ 27. Ka3 Bd6+ 28. b4 Bxb4+ 29. Kb3 Rxc3# 0-1";

    @Before
    public void setUp() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        game = new Game();
        game.setEvent("Live Chess");
        game.setSite("Chess.com");
        try {
            game.setDate(sdf.parse("2022.09.15"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Player white = new Player();
        Player black = new Player();
        white.setName("JonnyJamesf");
        black.setName("muzzleplayer");
        white.setElo(1434);
        black.setElo(1496);
        game.setWhitePlayer(white);
        game.setBlackPlayer(black);
        game.setResult("0-1");
        List<Move> moves = new ArrayList<>();
        Map<String, String> otherDetails = new HashMap<>();
        otherDetails.put("ECO", "B10");
        otherDetails.put("TimeControl", "600");
        otherDetails.put("EndTime", "7:17:00 PDT");
        otherDetails.put("Termination", "muzzleplayer won by checkmate");
        game.setOtherDetails(otherDetails);

        moves.add(new Move(1, "e4", 0));
        moves.add(new Move(1, "c6", 0));
        moves.add(new Move(2, "Nf3", 0));
        moves.add(new Move(2, "d5", 0));
        moves.add(new Move(3, "e5", 0));
        moves.add(new Move(3, "Bg4", 0));
        moves.add(new Move(4, "d4", 0));
        moves.add(new Move(4, "e6", 0));
        moves.add(new Move(5, "h3", 0));
        moves.add(new Move(5, "Bh5", 0));
        moves.add(new Move(6, "g4", 0));
        moves.add(new Move(6, "Bg6", 0));
        moves.add(new Move(7, "Bg5", 0));
        moves.add(new Move(7, "f6", 0));
        moves.add(new Move(8, "exf6", 0));
        moves.add(new Move(8, "Nxf6", 0));
        moves.add(new Move(9, "Ne5", 0));
        moves.add(new Move(9, "Bd6", 0));
        moves.add(new Move(10, "Nxg6", 0));
        moves.add(new Move(10, "hxg6", 0));
        moves.add(new Move(11, "Bd3", 0));
        moves.add(new Move(11, "Qa5+", 0));
        moves.add(new Move(12, "c3", 0));
        moves.add(new Move(12, "Ne4", 0));
        moves.add(new Move(13, "Bxe4", 0));
        moves.add(new Move(13, "dxe4", 0));
        moves.add(new Move(14, "Be3", 0));
        moves.add(new Move(14, "g5", 0));
        moves.add(new Move(15, "f3", 0));
        moves.add(new Move(15, "exf3", 0));
        moves.add(new Move(16, "Qxf3", 0));
        moves.add(new Move(16, "Nd7", 0));
        moves.add(new Move(17, "Qe4", 0));
        moves.add(new Move(17, "Bg3+", 0));
        moves.add(new Move(18, "Kd1", 0));
        moves.add(new Move(18, "O-O-O", 0));
        moves.add(new Move(19, "Bxg5", 0));
        moves.add(new Move(19, "Qxg5", 0));
        moves.add(new Move(20, "Rf1", 0));
        moves.add(new Move(20, "Rxh3", 0));
        moves.add(new Move(21, "Nd2", 0));
        moves.add(new Move(21, "e5", 0));
        moves.add(new Move(22, "dxe5", 0));
        moves.add(new Move(22, "Nxe5", 0));
        moves.add(new Move(23, "Kc2", 0));
        moves.add(new Move(23, "Rxd2+", 0));
        moves.add(new Move(24, "Kb3", 0));
        moves.add(new Move(24, "Nd7", 0));
        moves.add(new Move(25, "Qe8+", 0));
        moves.add(new Move(25, "Kc7", 0));
        moves.add(new Move(26, "Rad1", 0));
        moves.add(new Move(26, "Qb5+", 0));
        moves.add(new Move(27, "Ka3", 0));
        moves.add(new Move(27, "Bd6+", 0));
        moves.add(new Move(28, "b4", 0));
        moves.add(new Move(28, "Bxb4+", 0));
        moves.add(new Move(29, "Kb3", 0));
        moves.add(new Move(29, "Rxc3#", 0));
        game.setMoves(moves);
    }

    @Test
    public void exportInPgn() {
        String exportedPgn = game.exportInPgn();
        String[] expectedPgnLines = expectedPgn.split("\n");
        String[] exportedPgnLines = exportedPgn.split("\n");
        boolean found = false;
        for(String expectedPgnLine : expectedPgnLines) {
            for (String exportedPgnLine : exportedPgnLines) {
                if(expectedPgnLine.equalsIgnoreCase(exportedPgnLine)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Could not found " + expectedPgnLine+" in exported pgn\n" + exportedPgn, found);
            found = false;
        }
        System.out.println(exportedPgn);
        System.out.println("*********************************");
        System.out.println(expectedPgn);
    }
}