package com.scriptchess.services;

import com.scriptchess.models.Fen;
import com.scriptchess.util.FenBucketHandler;
import com.scriptchess.util.FenCreator;
import com.scriptchess.util.FileUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 20/05/23
 */

public class FenCompressorDecompressorTest {

    @Test
    public void getMatchingFenString() {
    }

    @Test
    public void getFenMoveStringFromByteArray() {
    }

    @Test
    public void getFenMoveInByteArray() {
        String fen = "5k2/8/4K3/4N3/4r3/8/8/R7 b - - 80 103$^Qc2-0-0-1";
        byte[] bytes = FenCompressorDecompressor.getFenMoveInByteArray(fen);
        String recovered = FenCompressorDecompressor.getFenMoveStringFromByteArray(bytes);
        System.out.println(fen);
        System.out.println(recovered);
    }

    @Test
    public void getFenList() throws IOException {
        List<byte[]> bytesList = new ArrayList<>();
        String[] fens = {"2r5/3npkbp/6p1/p2P4/P5P1/1K3P2/7R/2R5 b - - 0 39$^Rxc1,0,1,0^7E7-1E-0B-1817FC",
            "r2qk2r/1b1nbppp/3p1n2/4p3/N2P4/RP3NP1/1B3PBP/3Q1RK1 b kq - 0 15$^e4,0,1,0^7E7-1E-0B-1817187",
            "8/1pk5/1pnR2p1/1Kn1P1B1/2N1r1bP/8/8/8 b - - 9 55$^Na7+,0,1,0^7E7-1E-0A-3AB28B",
            "2krR3/1pp2q2/p4bpp/B1p2p1b/2N2P1P/3P2Q1/PPP3PK/7R b - - 0 28$^Rxe8,0,1,0^7E7-1E-0B-1E2E235",
            "r2q1rk1/2p2ppp/p2pb3/P3n1b1/1p2P3/1B2N3/1PP2PPP/R1BQR1K1 w - - 4 16$^Nd5,0,0,1^7E7-1E-0B-81E14A",
            "6k1/3q2p1/p6p/1n2Qp2/1p5P/1P1p2P1/P2N1P1K/8 w - - 0 43$^Qc5,0,0,1^7E7-1E-0B-930AF",
            "3b4/8/p2kp1K1/1P1p1p2/3P1P2/2P2N2/1P6/8 b - - 0 52$^axb5,1,0,0^7E7-1E-0B-316363",
            "r3kbnr/pp1bpppp/8/qB1QP3/8/2N5/PPP2PPP/R1B1K2R b KQkq - 2 9$^Qc7,1,0,0^7E7-1E-0B-1E2E229"
        };
        for(String fen : fens) {
            bytesList.add(FenCompressorDecompressor.getFenMoveInByteArray(fen));
        }
        FileUtil.writeByteArrayListData(bytesList, true,"/tmp/fenListtest");
        byte[] bytes = FileUtil.readBytes("/tmp/fenListtest");
        List<String> recoveredFen = FenCompressorDecompressor.getFenList(bytes);
        assertEquals(fens.length, recoveredFen.size());
        for(String fen : fens) {
            assertTrue(recoveredFen.contains(fen));
        }
    }
}