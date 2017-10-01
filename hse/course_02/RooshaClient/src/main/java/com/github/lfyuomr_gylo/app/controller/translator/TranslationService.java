package com.github.lfyuomr_gylo.app.controller.translator;

import com.github.lfyuomr_gylo.app.controller.translator.yandex.TranslationFormat;
import io.reactivex.Single;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface TranslationService {
    Single<? extends TranslationFormat> translate(@NotNull String text, @NotNull Locale source, @NotNull Locale target);
}
