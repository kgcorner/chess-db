package com.scriptchess.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class StringsTest {

    @Test
    public void isNullOrEmpty() {
        assertNotNull(new Strings());
        assertTrue(Strings.isNullOrEmpty(""));
        assertFalse(Strings.isNullOrEmpty("some val"));
    }

    @Test
    public void getMd5() {
        String MD5 = "9885d8a989852cc8aeb67ddbefb84c1e";
        String data = "this is some very secret string";
        assertEquals(MD5, Strings.getMd5(data));
    }


    @Test
    public void testIsNullOrEmpty() {
        assertTrue(Strings.isNullOrEmpty(null));
        assertTrue(!Strings.isNullOrEmpty("a"));
    }

    @Test
    public void createSlug() {
        assertEquals("a-b", Strings.createSlug("a b"));
    }

    @Test
    public void generateId() {
        assertNotNull(Strings.generateId());
    }

    @Test
    public void getUniqueId() {
        assertNotNull(Strings.getUniqueId());
    }

    @Test
    public void generateUniqueSessionId() {
        assertNotNull(Strings.generateUniqueSessionId());
    }


    @Test
    public void createPathFromId() {
        assertEquals("a/b/", Strings.createPathFromId("a-b"));
    }

    @Test
    public void toHex() {
        assertEquals("A", Strings.toHex(10));
    }

    @Test
    public void getString() {
        String test = "test";
        byte[] a = test.getBytes();
        Byte[] b = new Byte[a.length];
        for (int i = 0; i < a.length; i++) {
            b[i] = a[i];
        }
        assertEquals(test, Strings.getString(b));
    }

    @Test
    public void getByteArr() {
        String test = "test";
        byte[] a = test.getBytes();
        List<Byte> b = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            b.add(a[i]);
        }
        byte[] c = Strings.getByteArr(b);
        for (int i = 0; i < a.length; i++) {
            assertEquals(a[i], c[i]);
        }
    }
}