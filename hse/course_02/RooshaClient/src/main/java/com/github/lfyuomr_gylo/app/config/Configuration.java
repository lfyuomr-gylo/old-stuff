package com.github.lfyuomr_gylo.app.config;

import io.reactivex.subjects.BehaviorSubject;

import javax.swing.*;
import java.util.Locale;

import static com.github.lfyuomr_gylo.app.config.DefaultConstants.CASES_SHOWN_WITHOUT_SCROLLING;
import static com.github.lfyuomr_gylo.app.config.DefaultConstants.LAUNCHED_MINIMIZED;

public final class Configuration {
    private static final Object lock = new Object();
    private static Configuration INSTANCE = null;

    public static Configuration CONFIG() {
        Configuration instance = INSTANCE;
        if (instance != null) {
            return instance;
        }

        synchronized (lock) {
            instance = INSTANCE;
            if (instance != null) {
                return instance;
            }

            INSTANCE = new Configuration();
            return INSTANCE;
        }
    }

    //-----------------------------------------------------
    private boolean launchedMinimized;
    private int casesShownWithoutScrollingNum;

    private final BehaviorSubject<Locale> localeSubject;
    private final BehaviorSubject<KeyStroke> globalHotKeyActivatorSubject;

    private Configuration() {
        this.launchedMinimized = LAUNCHED_MINIMIZED;
        this.casesShownWithoutScrollingNum = CASES_SHOWN_WITHOUT_SCROLLING;

        this.localeSubject = BehaviorSubject.create();
        this.localeSubject.onNext(Locale.ENGLISH);

        this.globalHotKeyActivatorSubject = BehaviorSubject.create();
        this.globalHotKeyActivatorSubject.onNext(KeyStroke.getKeyStroke("control alt R"));

    }

    public boolean isLaunchedMinimized() {
        return launchedMinimized;
    }

    public int getCasesShownWithoutScrollingNum() {
        return casesShownWithoutScrollingNum;
    }

    BehaviorSubject<Locale> getLocaleSubject() {
        return localeSubject;
    }

    public void setLocale(Locale locale) {
        localeSubject.onNext(locale);
    }

    public BehaviorSubject<KeyStroke> getGlobalHotKeyActivatorSubject() {
        return globalHotKeyActivatorSubject;
    }

    public void setGlobalHotKeyActivator(KeyStroke globalHotKeyActivator) {
        this.globalHotKeyActivatorSubject.onNext(globalHotKeyActivator);
    }
}