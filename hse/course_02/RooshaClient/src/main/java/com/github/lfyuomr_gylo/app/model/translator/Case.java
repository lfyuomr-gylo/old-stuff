package com.github.lfyuomr_gylo.app.model.translator;

import com.github.lfyuomr_gylo.app.controller.translator.TranslationService;
import org.jetbrains.annotations.NotNull;

public class Case {
    private final @NotNull Meaning meaning;
    private final @NotNull TranslationService service;

    public Case(@NotNull Meaning meaning, @NotNull TranslationService service) {
        this.meaning = meaning;
        this.service = service;
    }

    public @NotNull Meaning getMeaning() {
        return meaning;
    }

    public @NotNull TranslationService getService() {
        return service;
    }
}
