package com.scriptchess.models;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 12/05/23
 */

public class TinyMoveTest {

    @Test
    public void testTinyMove() {

        String[] moves = {"e4","exd6","Ngxf5","N1f5","e8=Q","fxe8=Q","Rhg6","Rhxg6","R7g6","R8xg6","f5+","Bcf5+","O-O",
            "O-O-O", "O-O+", "O-O-O+","O-O#", "O-O-O#","axb1=Q","N2g2", "N3d3","N4d4","N5d5","N6d6","Ndd6"};
        for(String move : moves) {
            TinyMove tinyMove = TinyMove.getTinyMove(move);
            byte[] nativeMove = TinyMove.getNativeMove(tinyMove);
            TinyMove tinyMoveFromNativeMove = TinyMove.getTinyMoveFromNativeMove(nativeMove);
            String recoveredMove = TinyMove.getMove(tinyMoveFromNativeMove);
            assertEquals(move, recoveredMove);
        }
    }

}