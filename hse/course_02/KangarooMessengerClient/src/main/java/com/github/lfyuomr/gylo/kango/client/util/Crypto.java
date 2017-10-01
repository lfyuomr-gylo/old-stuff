package com.github.lfyuomr.gylo.kango.client.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Random;

public final class Crypto {
    private static final int KEY_LENGTH = 128;
    private static final Random randomGenerator = new Random();

    public static byte[]
    encode(String text, @NotNull BigInteger key) {
        return (new BigInteger(text.getBytes())).multiply(key).toByteArray();
    }

    public static String
    decode(byte[] data, @NotNull BigInteger key) {
        return new String((new BigInteger(data)).divide(key).toByteArray());
    }

    public static BigInteger
    generateBigInteger() {
        byte[] result = new byte[KEY_LENGTH];
        randomGenerator.nextBytes(result);
        return new BigInteger(result);
    }

}
