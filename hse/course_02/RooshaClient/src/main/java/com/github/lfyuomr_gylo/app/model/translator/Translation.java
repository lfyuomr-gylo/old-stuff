package com.github.lfyuomr_gylo.app.model.translator;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class Translation {
    private final @NotNull String text;
    private final @NotNull Collection<Case> cases;

    public Translation(@NotNull String text, @NotNull Collection<Case> cases) {
        this.text = text;
        this.cases = cases;
    }

    public @NotNull String getText() {
        return text;
    }

    public @NotNull Collection<Case> getCases() {
        return Collections.unmodifiableCollection(cases);
    }
}
