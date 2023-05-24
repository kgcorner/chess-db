package com.scriptchess.util;


import java.security.SecureRandom;
import java.util.Random;

/**
 * Description : <Write class Description>
 * Author: kumar
 * Created on : 17/09/22
 */

public class TestUtils {
    public static final Random random = new SecureRandom();

    public static String generateString(int len) {
        int leftLimit = 65; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = len;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit +
                (Math.abs(random.nextInt() * (rightLimit - leftLimit + 1)) % 25);
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static int generateInt(int upperLimit) {
        return Math.abs(random.nextInt()) % upperLimit;
    }
}