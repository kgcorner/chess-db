package com.scriptchess.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 13/05/23
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(RuntimeWrapper.class)
public class ChessJsUtilTest {

    private static final String NODE_JS_PATH = "nodeJsPath";
    private static Runtime mockedRuntime;
    private static Process mockedProcess;
    private static final String TEMPLATE_PATH = "/tmp/template.js";
    private static final String ROOK_SAC_TEMPLATE_PATH = "/tmp/rook_sac.js";
    private static final String QUEEN_SAC_TEMPLATE_PATH = "/tmp/queen_sac.js";

    @Before
    public void setup() throws IOException, InterruptedException {
        mockedProcess = PowerMockito.mock(Process.class);
        mockedRuntime = PowerMockito.mock(Runtime.class);
        PowerMockito.mockStatic(RuntimeWrapper.class);
        when(RuntimeWrapper.getRuntime()).thenReturn(mockedRuntime);
        when(mockedRuntime.exec(NODE_JS_PATH + " " + TEMPLATE_PATH)).thenReturn(mockedProcess);
        when(mockedRuntime.exec(NODE_JS_PATH + " " + ROOK_SAC_TEMPLATE_PATH)).thenReturn(mockedProcess);
        when(mockedRuntime.exec(NODE_JS_PATH + " " + QUEEN_SAC_TEMPLATE_PATH)).thenReturn(mockedProcess);
        when(mockedProcess.waitFor()).thenReturn(0);
    }

    @Test
    public void getFens() throws UnsupportedEncodingException {
        String[] fens = {"f1","f2","f3"};
        String allFens = "";
        for(String fen : fens) {
            allFens += fen +"\n";
        }
        allFens = allFens.substring(0, allFens.length() -1);
        InputStream stream = getStreamForString(allFens);
        when(mockedProcess.getInputStream()).thenReturn(stream);
        String pgn = "pgn";
        List<String> fens1 = ChessJsUtil.getFens(pgn, NODE_JS_PATH);
        assertEquals(fens.length, fens1.size());
        for (int i = 0; i < fens.length; i++) {
            assertEquals(fens[i], fens1.get(i));
        }
    }

    @Test
    public void getFenWithRookSac() throws IOException {
        String fens = "6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49 <<turn>> w";
        InputStream stream = getStreamForString(fens);
        when(mockedProcess.getInputStream()).thenReturn(stream);
        String fenWithRookSac = ChessJsUtil.getFenWithRookSac("pgn", "1-0", NODE_JS_PATH);
        assertEquals("6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49", fenWithRookSac.trim());
    }

    @Test
    public void getFenWithRookSacFailed() throws IOException {
        String fens = "6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49 <<turn>> b";
        InputStream stream = getStreamForString(fens);
        when(mockedProcess.getInputStream()).thenReturn(stream);
        String fenWithRookSac = ChessJsUtil.getFenWithRookSac("pgn", "1-0", NODE_JS_PATH);
        assertNull(fenWithRookSac);
    }

    @Test
    public void getFenWithQueenSac() throws IOException {
        String fens = "6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49 <<turn>> w";
        InputStream stream = getStreamForString(fens);
        when(mockedProcess.getInputStream()).thenReturn(stream);
        String fenWithRookSac = ChessJsUtil.getFenWithQueenSac("pgn", "1-0", NODE_JS_PATH);
        assertEquals("6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49", fenWithRookSac.trim());
    }

    @Test
    public void getFenWithQueenSacFailed() throws IOException {
        String fens = "6k1/pp3p2/4p3/3pN1p1/3P1n2/5N1P/6rK/8 w - - 0 49 <<turn>> b";
        InputStream stream = getStreamForString(fens);
        when(mockedProcess.getInputStream()).thenReturn(stream);
        String fenWithRookSac = ChessJsUtil.getFenWithQueenSac("pgn", "1-0", NODE_JS_PATH);
        assertNull(fenWithRookSac);
    }

    private InputStream getStreamForString(String s) throws UnsupportedEncodingException {
        return new ByteArrayInputStream( s.getBytes( "UTF-8" ) );
    }
}