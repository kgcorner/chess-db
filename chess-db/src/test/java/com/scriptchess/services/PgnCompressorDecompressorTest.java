package com.scriptchess.services;

import com.scriptchess.models.Game;
import com.scriptchess.services.parsers.ByteWisePGNProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 12/05/23
 */

public class PgnCompressorDecompressorTest {
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
    private Game game;
    private String gameFile = "test-game-file";

    @Before
    public void setUp() throws Exception {
        game = new ByteWisePGNProcessor().parsePgn(PGN);
    }

    @After
    public void tearDown() throws Exception {
        File file = new File("/tmp/" + gameFile + PgnCompressorDecompressor.GAME_EXTENSION);
        if(file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testReadAndWrite() {
        PgnCompressorDecompressor.writeGame(game, "/tmp/" + gameFile);
        File compressedFileLocation = new File("/tmp/" + gameFile + PgnCompressorDecompressor.GAME_EXTENSION);
        long length = compressedFileLocation.length();
        assertTrue(length > 0);
        List<Game> games = PgnCompressorDecompressor.readGames("/tmp/" + gameFile);
        assertTrue(games.size() == 1);
        Game recoveredGame = games.get(0);
        assertEquals("Chess.com", recoveredGame.getSite());
        assertEquals("muzzleplayer", recoveredGame.getWhitePlayer().getName());
        assertEquals("Jordy-tk", recoveredGame.getBlackPlayer().getName());
        assertEquals(20, recoveredGame.getMoves().size());
    }
}