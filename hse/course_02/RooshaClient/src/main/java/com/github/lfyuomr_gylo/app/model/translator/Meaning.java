package com.github.lfyuomr_gylo.app.model.translator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Meaning {
    /**
     * In target language.
     */
    private final String meaning;

    /**
     * In source language.
     */
    private final String description;

    /**
     * In source language.
     */
    private final String usageExample;

    public Meaning(@NotNull String meaning, @Nullable String description, @Nullable String usageExample) {
        this.meaning = meaning;
        this.description = description;
        this.usageExample = usageExample;
    }

    public String getMeaning() {
        return meaning;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable String getUsageExample() {
        return usageExample;
    }
}
