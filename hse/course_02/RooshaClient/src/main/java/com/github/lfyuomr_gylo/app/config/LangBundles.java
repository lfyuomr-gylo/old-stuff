package com.github.lfyuomr_gylo.app.config;

import io.reactivex.Observable;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.github.lfyuomr_gylo.app.config.Configuration.CONFIG;
import static com.github.lfyuomr_gylo.app.config.DefaultConstants.DEFAULT_LOCALE;
import static com.github.lfyuomr_gylo.app.config.LangBundles.Bundles.SYSTRAY;
import static com.github.lfyuomr_gylo.app.config.LangBundles.Bundles.TRANSLATION_DIALOG;

public final class LangBundles {
    private static final Observable<LanguageBundle> systemTrayBundleObservable;
    private static final Observable<LanguageBundle> translationDialogBundleObservable;

    static {
        systemTrayBundleObservable = CONFIG().getLocaleSubject().map(SYSTRAY::load);
        translationDialogBundleObservable = CONFIG().getLocaleSubject().map(TRANSLATION_DIALOG::load);
    }

    public static Observable<LanguageBundle> getSystemTrayBundleObservable() {
        return systemTrayBundleObservable;
    }

    public static Observable<LanguageBundle> getTranslationDialogBundleObservable() {
        return translationDialogBundleObservable;
    }

    enum Bundles {
        SYSTRAY("lang.systray"),
        TRANSLATION_DIALOG("lang.translation-dialog"),
        ;

        private final String name;

        Bundles(String name) {
            this.name = name;
        }

        LanguageBundle load(Locale locale) {
            LanguageBundle bundle;
            try {
                bundle = new LanguageBundle(ResourceBundle.getBundle(name, locale));
            } catch (MissingResourceException e) {
                // TODO: show error alert
                bundle = new LanguageBundle(ResourceBundle.getBundle(name, DEFAULT_LOCALE));
            }

            return bundle;
        }
    }

    public static class LanguageBundle {
        final ResourceBundle bundle;

        public LanguageBundle(ResourceBundle bundle) {
            this.bundle = bundle;
        }

        public String getString(String key) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                // TODO: show alert
            } catch (ClassCastException e) {
                // TODO: show alert
            }
            return "";
        }
    }
}
