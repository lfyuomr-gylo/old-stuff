package com.github.lfyuomr.gylo.bostongene.task1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class EnglishNumeral {
    private static final Map<String, Integer> units;
    private static final Map<String, Integer> teens;
    private static final Map<String, Integer> decades;
    private static final Map<String, Integer> magnitudes;

    static {
        val unitsArray = new String[]{"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        units = new HashMap<>(unitsArray.length);
        for (int i = 0; i < unitsArray.length; i++) {
            units.put(unitsArray[i], i + 1);
        }

        val teensArray = new String[]{"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
                "sixteen", "seventeen", "eighteen", "nineteen"
        };
        teens = new HashMap<>(teensArray.length);
        for (int i = 0; i < teensArray.length; i++) {
            teens.put(teensArray[i], i + 10);
        }

        val decadesArray = new String[]{"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};
        decades = new HashMap<>(decadesArray.length);
        for (int i = 0; i < decadesArray.length; i++) {
            decades.put(decadesArray[i], (i + 2) * 10);
        }

        magnitudes = new HashMap<>();
        magnitudes.put("thousand", tenPow(3));
    }

    private static int tenPow(int power) {
        int result = 1;
        for (int i = 0; i < power; i++) {
            result *= 10;
        }
        return result;
    }

    @Getter private final String englishRepresentation;
    @Getter private final int integerValue;

    public EnglishNumeral(@NotNull String numeral) throws NumeralParserException {
        val words = Arrays.stream(numeral.split("[\\s]")).filter(x -> x.length() > 0).toArray(String[]::new);
        integerValue = parseEnglishNumeral(words);
        englishRepresentation = numeral;
    }

    private int parseEnglishNumeral(String[] words) throws NumeralParserException {
        if (words.length == 0) { throw new NumeralParserException("В числительном нет слов."); }

        int offset = 0;
        String lastMagnitudeString = "";
        int lastMagnitude = Integer.MAX_VALUE;
        int result = 0;
        while (offset < words.length) {
            val nextThousand = parseUpToThousand(words, offset);
            if (nextThousand == null) {
                throw new NumeralParserException();
            }
            offset += nextThousand.numberOfWords;

            if (offset < words.length) {
                val nextMagnitude = magnitudes.get(words[offset]);
                if (nextMagnitude == null) {
                    throw new NumeralParserException("Неожиданное слово: " + words[offset]);
                } else if (nextMagnitude >= lastMagnitude) {
                    throw new NumeralParserException("Неожиданный модификатор степени " + words[offset] +
                            " стоящий справа от " + lastMagnitudeString
                    );
                } else {
                    lastMagnitudeString = words[offset];
                    lastMagnitude = magnitudes.get(lastMagnitudeString);
                    offset++;
                    result += nextThousand.value * lastMagnitude;
                }
            } else {
                result += nextThousand.value;
            }
        }

        return result;
    }

    private @Nullable ParsedSubNumber parseUpToThousand(String[] words, int start) {
        if (units.containsKey(words[start]) && start + 1 < words.length && words[start + 1].equals("hundred")) {
            val hundreds = 100 * units.get(words[start]);
            if (start + 2 < words.length) {
                val decs = parseUpToHundred(words, start + 2);
                return decs == null ? null :
                        new ParsedSubNumber(decs.numberOfWords + 2, hundreds + decs.value);
            } else {
                return new ParsedSubNumber(2, hundreds);
            }
        } else {
            return parseUpToHundred(words, start);
        }
    }

    private @Nullable ParsedSubNumber parseUpToHundred(String[] words, int start) {
        if (units.containsKey(words[start])) { // num < 10
            return new ParsedSubNumber(1, units.get(words[start]));
        } else if (teens.containsKey(words[start])) { // 10 <= num < 20
            return new ParsedSubNumber(1, teens.get(words[start]));
        } else if (decades.containsKey(words[start])) { // 20 <= num < 100
            final int decs = decades.get(words[start++]);
            if (start < words.length && units.containsKey(words[start])) {
                return new ParsedSubNumber(2, decs + units.get(words[start]));
            } else {
                return new ParsedSubNumber(1, decs);
            }
        }

        return null;
    }


    @AllArgsConstructor
    private static class ParsedSubNumber {
        final int numberOfWords;
        final int value;
    }

    static class NumeralParserException extends RuntimeException {
        public NumeralParserException() {
        }

        public NumeralParserException(String message) {
            super(message);
        }
    }
}
