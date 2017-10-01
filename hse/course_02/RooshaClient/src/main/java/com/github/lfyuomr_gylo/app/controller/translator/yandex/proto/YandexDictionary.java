package com.github.lfyuomr_gylo.app.controller.translator.yandex.proto;

import com.github.lfyuomr_gylo.app.controller.translator.TranslationService;
import com.github.lfyuomr_gylo.app.controller.translator.yandex.TranslationFormat;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Scanner;

import static java.lang.String.format;

@SuppressWarnings({"WeakerAccess", "unused", "SpellCheckingInspection"})
public class YandexDictionary implements TranslationService {
    private static final String API_KEY =
            "dict.1.1.20160905T084639Z.02f5a4d75c0b1167.84b38402a3426374a791c5df3e5416e0ef7d9b5c";
    private static final String LOOKUP_FORMAT_STRING =
            "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=%s&text=%s&lang=%s";

    public static final int FAMILY = 0x0001;
    /**
     * Shorten names of parts of speech
     */
    public static final int SHORT_POS = 0x0002;
    public static final int MORPHO = 0x0004;
    public static final int POS_FILTER = 0x0008;

    static @NotNull Single<DicResult> lookup(
            @NotNull String text,
            @NotNull String lang) {
        return lookup(text, lang, null, (Flag[]) null);
    }

    static @NotNull Single<DicResult> lookup(
            @NotNull String text,
            @NotNull String lang,
            @Nullable UILang ui,
            @Nullable Flag... flags) {
        final StringBuilder requestBuilder = new StringBuilder(format(LOOKUP_FORMAT_STRING, API_KEY, text, lang));
        if (ui != null) {
            requestBuilder.append("&ui=").append(ui.key);
        }
        if (flags != null && flags.length > 0) {
            final int flag = Observable.fromArray(flags).map(Flag::getBitmask).reduce((x, y) -> x | y).blockingFirst(0);
            requestBuilder.append("flags=").append(flag);
        }

        return Single.just(requestBuilder)
                     .map(StringBuilder::toString)
                     .map(URL::new)
                     .map(URL::openConnection)
                     .map(URLConnection::getInputStream)
                     .map(Scanner::new)
                     .map(scanner -> scanner.useDelimiter("\\A"))
                     .map(scanner -> scanner.hasNext() ? scanner.next() : "")
                     .map(s -> new Gson().fromJson(s, DicResult.class));
    }

    @Override
    public @NotNull Single<? extends TranslationFormat> translate(
            @NotNull String text, @NotNull Locale source, @NotNull Locale target) {
        return lookup(text, source.getLanguage() + "-" + target.getLanguage(), UILang.fromLocale(target));
    }

    public enum UILang {
        ENGLISH("en"),
        RUSSIAN("ru"),
        UKRAINIAN("uk"),
        TURKISH("tr"),;

        final String key;

        UILang(String key) {
            this.key = key;
        }

        public static UILang fromLocale(Locale locale) {
            if (locale.equals(new Locale(RUSSIAN.key))) {
                return RUSSIAN;
            }
            if (locale.equals(new Locale(UKRAINIAN.key))) {
                return UKRAINIAN;
            }
            if (locale.equals(new Locale(TURKISH.key))) {
                return TURKISH;
            }

            return ENGLISH;
        }
    }

    public enum Flag {
        /**
         * Apply family filter
         */
        FAMILY(0x0001),
        /**
         * Shorten names of parts of speech
         */
        SHORT_POS(0x0002),
        /**
         * Enable search by word form.
         */
        MORPHO(0x0004),
        /**
         * Require matching of text's and translation's parts of speech.
         */
        POS_FILTER(0x0008),;

        final int bitmask;

        Flag(int bitmask) {
            this.bitmask = bitmask;
        }

        public int getBitmask() {
            return bitmask;
        }
    }
}
