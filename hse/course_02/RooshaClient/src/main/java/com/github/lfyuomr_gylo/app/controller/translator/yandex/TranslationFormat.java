package com.github.lfyuomr_gylo.app.controller.translator.yandex;

import com.github.lfyuomr_gylo.app.model.translator.Translation;
import org.jetbrains.annotations.NotNull;

public interface TranslationFormat {
    @NotNull Translation toTranslation();
}
