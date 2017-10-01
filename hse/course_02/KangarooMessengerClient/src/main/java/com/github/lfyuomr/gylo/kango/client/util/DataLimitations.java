package com.github.lfyuomr.gylo.kango.client.util;

import java.util.regex.Pattern;

public enum DataLimitations {
    LOGIN(4, 24, "[[A-z][0-9]_]*", "only Latin letters, figures and underscores"),
    PASSWORD(6, 30, "[[A-z][0-9]_]*", "only Latin letters, figures and underscores"),
    NAME(1, 50, "[A-Z][a-z]*", "only beginning with Capital letter followed by only lowercase letters"),
    CONVERSATION_TITLE(3, 15, "[[A-z][0-9]_]*", "only Latin letters, figures and underscores");

    private int minLen, maxLen;
    private Pattern pattern;
    private String description;

    DataLimitations(int minLen, int maxLen, String template, String description) {
        this.minLen = minLen;
        this.maxLen = maxLen;
        pattern = Pattern.compile(template);
        this.description = "Required: length: " + minLen + "-" + maxLen + ", " + description;
    }

    public boolean isAppropriate(String str) {
        return checkLen(str.length()) && checkPattern(str);
    }

    public boolean checkLen(int len) {
        return minLen <= len && len <= maxLen;
    }

    public boolean checkPattern(String str) {
        return pattern.matcher(str).matches();
    }

    public String getDescription() {
        return description;
    }
}
