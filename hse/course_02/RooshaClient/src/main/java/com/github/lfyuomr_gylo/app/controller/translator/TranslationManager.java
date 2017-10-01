package com.github.lfyuomr_gylo.app.controller.translator;

import com.github.lfyuomr_gylo.app.controller.translator.yandex.TranslationFormat;
import com.github.lfyuomr_gylo.app.controller.translator.yandex.proto.YandexDictionary;
import com.github.lfyuomr_gylo.app.model.translator.Translation;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public final class TranslationManager {
    private TranslationManager() {
    }

    /**
     * Depending of preferences chooses one or more {@link TranslationService translation sevices} and
     * combines results of their work to a {@link Single}.
     * @param text text to translate
     * @return translations from different resources combined and wrapped to {@link Single}
     */
    public static @NotNull Single<Translation> translate (@NotNull String text) {
        return new YandexDictionary().translate(text, Locale.ENGLISH, new Locale("ru"))
                                     .map(TranslationFormat::toTranslation);
    }
}