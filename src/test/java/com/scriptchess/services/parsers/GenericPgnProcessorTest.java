package com.scriptchess.services.parsers;

import com.scriptchess.models.Game;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 23/09/22
 */

public class GenericPgnProcessorTest {
    private GenericPgnProcessor pgnProcessor = null;
    @Test
    public void parsePgn() {
        pgnProcessor = new GenericPgnProcessor();
        String pgn = "[Event \"GMA, Wijk aan Zee NED\"]\n" +
            "[Site \"?\"]\n" +
            "[Date \"2003.??.??\"]\n" +
            "[Round \"1\"]\n" +
            "[White \"Anand,V\"]\n" +
            "[Black \"Radjabov,T\"]\n" +
            "[Result \"1/2\"]\n" +
            "[WhiteElo \"2750\"]\n" +
            "[BlackElo \"2620\"]\n" +
            "[ECO \"C12\"]\n" +
            "[PlyCount \"55\"]\n" +
            "[Annotator \"Hathaway\"]\n" +
            "\n" +
            "1. e4 e6\n" +
            "{ I'm not terribly familiar with the style of Radjabov, so I don't know if this is his usual opening. }\n" +
            "2. d4 d5 3. Nc3 Nf6 (3...Bb4 \n" +
            "{ The Winawer Variation is probably best, though not as easy to play. }) 4. Bg5\n" +
            "{ threatens e4-e5xf6 }\n" +
            " (4. e5 \n" +
            "{ keeps pieces on the board and avoids ...dxe4 }) 4...Bb4 (4...Be7 \n" +
            "{ is more common and aims to trade dark-square bishops to ease Black's cramp }) (4...dxe4 \n" +
            "{ aims to avoid any cramp by bringing pieces into alignment for trading, though White does get at least one very good piece (Ne4 or Bg5) and an easier time castling queen-side, to stir up king-side threats }\n" +
            " 5. Nxe4 Be7  (\n" +
            "{ or Rubinstein's }\n" +
            " 5...Nbd7) ) 5. e5 h6 6. Bd2 (6. Bh4 g5 7. exf6 gxh4 \n" +
            "{ Black seems to equalize a little easier after this as he can win Pf6 in exchange for Ph4. }) 6...Bxc3 (6...Nfd7 7. Qg4 \n" +
            "{ and White isn't incurring any weaknesses, but is either gaining Bb4 for Nc3 or after ...Bb4-f8 Black is cramped again }\n" +
            "  (7. Nb5 $5 Bxd2+ 8. Qxd2 a6 9. Na3) ) 7. bxc3 Ne4 8. Qg4\n" +
            "{ White immediately takes aim at the backward Pg7 & Rh8 and usually Pf7 & Ke8. For the moment Bd2 serves to defend Pc3 and to prevent ...Qd8-g5 (offering a queen trade to end the pressure) . }\n" +
            " (\n" +
            "{ While }\n" +
            " 8. h4 \n" +
            "{ is often useful in the French Defense with this pawn structure, I don't know that it's been tried in this opening on this move. }) 8...g6 9. Bd3 (9. h4 \n" +
            "{ could take over for Bd2 in guarding g5 and preparing a later attack by f2-f4, h4-h5 or vice versa. It also would allow Rh1 to develop to build the direct frontal threats to Pf7 & Pg6. }\n" +
            " 9...c5 10. Bd3 Nxd2 11. Kxd2 Qa5 12. dxc5 Qxc5 13. Ne2 Qxf2 $4 14. Raf1 Qc5 15. Bxg6 fxg6 16. Qxg6+)  (9. Qd1 \n" +
            "{ Fritz7; Odd! }) 9...Nxd2 10. Kxd2 c5 11. Nf3\n" +
            "{ This has been considered the main line for many years, but I wonder if White can allow ...c5-c4 and not use more pawns to fight through Black's pawns. }\n" +
            " (11. dxc5 \n" +
            "{ is probably still wrong because of ...Qg5+ }) (11. h4 \n" +
            "{ still makes sense }) 11...Bd7 (11...c4 $6 \n" +
            "{ The problem with this is that however much it slows White, it also limits Black's queen-side offensive possibilities. }) (\n" +
            "{ Prematurely playing }\n" +
            " 11...cxd4 \n" +
            "{ lets White straighten-out his pawns and Black has made no real progress. }\n" +
            " 12. cxd4)  (11...Qa5 $5 \n" +
            "{ Fritz7: with the idea of ...cxd4 }) 12. dxc5 Qe7 13. Rab1 Bc6 14. Nd4 Nd7\n" +
            "{ These last few moves have been quite unusual for a French Defense, but they make sense; Qe7 defends Pf7 while Bc6 defends Pb7 and Nd7 threatens Pc5 & Pe5. }\n" +
            "15. Rhe1 (15. Nxc6 bxc6 16. Rb7 Qxc5 17. Qf4 g5 18. Qd4 Qa5 19. Rb2 c5 $11 \n" +
            "{ Fritz7 }) 15...Nxc5 16. Re3\n" +
            "{ another way of getting the rook into position, in front of the king-side pawns, to threaten Black's king-side pawns }\n" +
            "16...h5 17. Qg3 O-O-O\n" +
            "{ After this it would seem Black's pieces can handle any threats White can generate. However, black might also have ideas of winning. How might he do that? Well, ...Be8, ...Kc8-b8-a8, ...Rd8-c8, ...Nc5-a4 and Pc3 is a target (slow I know) . Another idea is to keep Kd2 from ever escaping to safety by advancing ...h5-h4-h3 to break open the king-side and open the h-file for Black's rooks. }\n" +
            " (17...h4 $15 \n" +
            "{ Fritz7 }) (17...Nxd3 $15 \n" +
            "{ Fritz7 }) 18. Ke1 Qc7 (18...h4 19. Qg4 Rh5) 19. h4\n" +
            "{ Anand aims to keep the king-side perfectly safe to ensure a draw. }\n" +
            " (19. Qh4 \n" +
            "{ Fritz7 }) 19...Qa5 20. Kf1 (20. Nxc6 bxc6 21. Kf1 Kd7 20. Qf4 Ke8 $11 \n" +
            "{ Fritz7 }) 20...Rd7 (\n" +
            "{ Premature is }\n" +
            " 20...Qxa2 21. Ree1 Qa5  (21...Ba4 $11 \n" +
            "{ Fritz7 })  22. Ra1 Qxc3 23. Nxc6 bxc6 24. Ba6+ $18) 21. Qf4\n" +
            "{ This general activity is perfect. It threatens Pf7, defends Nd4 and in some cases prepares for Qf4-b4 to attack Kc8. }\n" +
            " (21. Ree1 \n" +
            "{ Fritz7 }) (21. Nxc6 bxc6 22. Ree1 \n" +
            "{ Fritz7 }) 21...Rhd8\n" +
            "{ Black is probably wondering why he organized his pieces to only defend light squares. Only Qa5 and Nc5 can get to dark squares and that makes White's task of coordinating much easier. }\n" +
            " (21...Qxa2 \n" +
            "{ still premature }\n" +
            " 22. Nxc6 bxc6 23. Qb4 Nb7 24. Ree1)  (21...Qxc3 $4 22. Nxc6 bxc6 23. Ba6+)  (21...Rc7 $14 \n" +
            "{ Fritz7 }) (21...Na4 $14 \n" +
            "{ Fritz7 }) 22. Kg1 (22. Nxc6 bxc6 23. Qb4 Qxb4 24. cxb4 d4 25. Ree1 Na4 $11 \n" +
            "{ Fritz7 }) 22...Nxd3 23. Rxd3 (23. cxd3 Qxc3 24. Rg3 Rc7 $14 \n" +
            "{ Fritz7 }) 23...Qc5 (23...Qxa2 24. Rdd1 Qc4 $11 \n" +
            "{ Fritz7 }) 24. Rb4 a5 $2 (24...Rc7 \n" +
            "{ Mark and Fritz7 agree! }) 25. Rb1 Rc7 26. Qc1 Be8 27. Nb3 (27. Qb2 \n" +
            "{ If White commits too quickly to the b-file then Black might actually create some play against Ph4 and on the c-file. }\n" +
            " 27...Qe7  (27...a4 $11 \n" +
            "{ Fritz7 })  28. Nf3 Rc4 \n" +
            "{ possibly preparing ...b5 }) 27...Qb6 (27...Qc4 28. Nxa5 Qxh4 $14 \n" +
            "{ Fritz7 }) 28. Nd4\n" +
            "{ Black created the weakness (Pa5) and can't quite defend it, so Anand forces a draw. }\n" +
            "1/2-1/2";

        Game game = pgnProcessor.parsePgn(pgn);
        assertNotNull(game);
        assertNull(game.getDate());
        //assertNull(game.getTournament());
        assertEquals("Anand,V", game.getWhitePlayer().getName());
        assertEquals(55, game.getMoves().size());
    }

    @Test
    public void supports() {
        assertTrue(new GenericPgnProcessor().supports("any pgn"));
    }
}