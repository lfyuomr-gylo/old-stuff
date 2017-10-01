package com.github.lfyuomr.gylo.kango.server.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PublicKeys {
    private static SecureRandom generator = new SecureRandom();

    public static BigInteger generateModulo(int bitNum) {
        return BigInteger.probablePrime(bitNum, generator);
    }
}
