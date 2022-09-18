package com.scriptchess.services.parsers;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 19/09/22
 */

public class PGNProcessorFactoryTest {

    @Test
    public void getProcessor() {
        //test constructor
        assertNotNull(new PGNProcessorFactory());
        String pgn = pgn = "[Event \"WorldChess Gaming\"]\n" +
            "[Site \"worldchess.com\"]\n" +
            "[Date \"2021.09.17\"]\n" +
            "[Round \"1\"]\n" +
            "[White \"Kumar Gaurav\"]\n" +
            "[Black \"Rabulan, Leo\"]\n" +
            "[Result \"0-1\"]\n" +
            "[BlackElo \"1052\"]\n" +
            "[BlackFideId \"5207517\"]\n" +
            "[BlackPlayerId \"442015\"]\n" +
            "[Tournament \"Daily Tournament\"]\n" +
            "[UTCDate \"2021.09.17\"]\n" +
            "[UTCTime \"18:00:08\"]\n" +
            "[WhiteElo \"843\"]\n" +
            "[WhiteFideId \"366093876\"]\n" +
            "[WhitePlayerId \"69106\"]\n" +
            "\n" +
            "1. d4 { [%clk 00:10:08] } 1... Nf6 { [%clk 00:09:58] } 2. Bf4\n" +
            "{ [%clk 00:10:15] } 2... d5 { [%clk 00:09:54] } 3. e3 { [%clk 00:10:23] } 3...\n" +
            "c5 { [%clk 00:09:59] } 4. Nf3 { [%clk 00:10:29] } 4... Qb6 { [%clk 00:10:06] }\n" +
            "5. Nc3 { [%clk 00:10:25] } 5... a6 { [%clk 00:09:21] } 6. a3\n" +
            "{ [%clk 00:10:23] } 6... e6 { [%clk 00:09:08] } 7. Be2 { [%clk 00:10:20] } 7...\n" +
            "Nc6 { [%clk 00:08:48] } 8. O-O { [%clk 00:10:25] } 8... cxd4\n" +
            "{ [%clk 00:08:33] } 9. exd4 { [%clk 00:10:19] } 9... Be7 { [%clk 00:08:35] }\n" +
            "10. Na4 { [%clk 00:09:39] } 10... Qd8 { [%clk 00:08:35] } 11. Nc5\n" +
            "{ [%clk 00:09:39] } 11... O-O { [%clk 00:08:33] } 12. a4 { [%clk 00:09:33] }\n" +
            "12... b6 { [%clk 00:08:24] } 13. Nd3 { [%clk 00:09:34] } 13... Ne4\n" +
            "{ [%clk 00:08:19] } 14. Nfe5 { [%clk 00:09:22] } 14... Nxd4 { [%clk 00:08:13] }\n" +
            "15. f3 { [%clk 00:08:54] } 15... Nd6 { [%clk 00:07:54] } 16. c3\n" +
            "{ [%clk 00:08:46] } 16... Nxe2+ { [%clk 00:07:56] } 17. Qxe2\n" +
            "{ [%clk 00:08:52] } 17... Bb7 { [%clk 00:08:04] } 18. Nb4 { [%clk 00:08:17] }\n" +
            "18... Qc8 { [%clk 00:06:57] } 19. Nc2 { [%clk 00:08:03] } 19... Nf5\n" +
            "{ [%clk 00:06:23] } 20. Rad1 { [%clk 00:07:21] } 20... Bc5+ { [%clk 00:06:05] }\n" +
            "21. Be3 { [%clk 00:07:21] } 21... Nxe3 { [%clk 00:06:09] } 22. Nxe3\n" +
            "{ [%clk 00:07:29] } 22... Qc7 { [%clk 00:05:47] } 23. b4 { [%clk 00:07:15] }\n" +
            "23... Qxe5 { [%clk 00:05:43] } 24. bxc5 { [%clk 00:07:21] } 24... bxc5\n" +
            "{ [%clk 00:05:50] } 25. Kh1 { [%clk 00:07:22] } 25... d4 { [%clk 00:05:45] }\n" +
            "26. cxd4 { [%clk 00:07:29] } 26... cxd4 { [%clk 00:05:52] } 27. Qb2\n" +
            "{ [%clk 00:07:28] } 27... Qxe3 { [%clk 00:05:37] } 28. Qxb7 { [%clk 00:07:34] }\n" +
            "28... Rfd8 { [%clk 00:04:32] } 29. Qb6 { [%clk 00:07:28] } 29... e5\n" +
            "{ [%clk 00:04:21] } 30. Rfe1 { [%clk 00:07:33] } 30... Qg5 { [%clk 00:04:13] }\n" +
            "31. Qc7 { [%clk 00:07:26] } 31... f6 { [%clk 00:04:06] } 32. Re4\n" +
            "{ [%clk 00:07:07] } 32... h5 { [%clk 00:04:08] } 33. Rb1 { [%clk 00:06:36] }\n" +
            "33... Rac8 { [%clk 00:04:03] } 34. Qb7 { [%clk 00:05:59] } 34... Rc1+\n" +
            "{ [%clk 00:02:57] } 35. Re1 { [%clk 00:06:07] } 35... Rxe1+ { [%clk 00:02:53] }\n" +
            "36. Rxe1 { [%clk 00:06:14] } 36... d3 { [%clk 00:02:45] } 37. Qxa6\n" +
            "{ [%clk 00:06:11] } 37... d2 { [%clk 00:02:44] } 38. Rd1 { [%clk 00:06:13] }\n" +
            "38... Qe3 { [%clk 00:02:43] } 39. Qf1 { [%clk 00:05:38] } 39... Rc8\n" +
            "{ [%clk 00:02:46] } 40. h3 { [%clk 00:04:47] } 40... Rc1 { [%clk 00:02:35] }\n" +
            "41. Kh2 { [%clk 00:04:54] } 41... h4 { [%clk 00:02:29] } 42. a5\n" +
            "{ [%clk 00:04:48] } 42... e4 { [%clk 00:01:52] } 43. a6 { [%clk 00:04:38] }\n" +
            "43... Kh7 { [%clk 00:01:31] } 44. a7 { [%clk 00:04:09] } 44... Qf4+\n" +
            "{ [%clk 00:01:05] } 45. Kh1 { [%clk 00:04:04] } 45... Qe3 { [%clk 00:01:09] }\n" +
            "46. a8=Q { [%clk 00:03:23] } 46... Rxd1 { [%clk 00:01:13] } 47. Qxe4+\n" +
            "{ [%clk 00:03:14] } 47... Qxe4 { [%clk 00:01:16] } 0-1";

        PgnProcessor processor = PGNProcessorFactory.getProcessor(pgn);
        assertTrue(processor instanceof ChessArenaPgnProcessor);

    }
}