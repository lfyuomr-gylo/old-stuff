package com.github.lfyuomr_gylo.app.config;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DefaultConstants {
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static final long INPUT_DEBOUNCE_TIMEOUT = 300;

    public static final TimeUnit INPUT_DEBOUNCE_TIME_UNIT = MILLISECONDS;
    public static final long MAX_TRANSLATION_TIME = 300;

    public static final TimeUnit MAX_TRANSLATION_TIME_UNIT = MILLISECONDS;

    static final boolean LAUNCHED_MINIMIZED = true;
    static final int CASES_SHOWN_WITHOUT_SCROLLING = 3;
}
