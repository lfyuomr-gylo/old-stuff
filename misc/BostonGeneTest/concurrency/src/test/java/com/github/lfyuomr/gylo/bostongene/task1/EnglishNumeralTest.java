package com.github.lfyuomr.gylo.bostongene.task1;

import lombok.val;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EnglishNumeralTest {
    @Test
    public void singleDigit() throws Exception {
        val cases = new String[] {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        for (int i = 0; i < cases.length; i++) {
            val parsed = new EnglishNumeral(cases[i]);
            assertEquals(cases[i], i + 1, parsed.getIntegerValue());
        }
    }

    @Test
    public void twoDigits() throws Exception {
        val cases = new HashMap<String, Integer>();
        cases.put("thirty one", 31);
        cases.put("sixty six", 66);
        cases.put("twelve", 12);
        cases.put("thirty eight", 38);

        runCases(cases);
    }

    @Test
    public void threeDigits() throws Exception {
        val cases = new HashMap<String, Integer>();
        cases.put("one hundred seventy nine", 179);
        cases.put("three hundred twelve", 312);
        cases.put("seven hundred eighty five", 785);
        cases.put("nine hundred", 900);
        cases.put("two hundred", 200);
        cases.put("three hundred four", 304);
        cases.put("four hundred nine", 409);
        cases.put("five hundred nine", 509);

        runCases(cases);
    }

    @Test
    public void fourDigits() throws Exception {
        val cases = new HashMap<String, Integer>();
        cases.put("three thousand", 3000);

        cases.put("three thousand nine", 3009);
        cases.put("three thousand ninety", 3090);
        cases.put("three thousand twelve", 3012);
        cases.put("three thousand thirty four", 3034);

        cases.put("three thousand four hundred nine", 3409);
        cases.put("three thousand four hundred ninety", 3490);
        cases.put("three thousand four hundred twelve", 3412);
        cases.put("three thousand four hundred thirty four", 3434);

        runCases(cases);
    }

    @Test
    public void upToSixDigits() throws Exception {
        val cases = new HashMap<String, Integer>();
        cases.put("ten thousand fifty nine", 10059);
        cases.put("thirty one thousand five hundred twelve", 31512);
        cases.put("one hundred twelve thousand seven hundred thirty", 112730);

        runCases(cases);
    }

    private void runCases(Map<String, Integer> cases) throws Exception {
        for (val entry : cases.entrySet()) {
            assertEquals(entry.getKey(), (int) entry.getValue(), new EnglishNumeral(entry.getKey()).getIntegerValue());
        }
    }
}
