package com.scriptchess.util;

import org.junit.Before;
import org.junit.Test;

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


}