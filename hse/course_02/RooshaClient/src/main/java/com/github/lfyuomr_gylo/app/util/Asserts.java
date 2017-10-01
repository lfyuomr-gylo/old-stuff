package com.github.lfyuomr_gylo.app.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Asserts {
    private Asserts() {}

    public static <T> @NotNull T assertNotNull(@Nullable T value) {
        if (value == null) {
            throw new IllegalStateException("Unexpected null value");
        }
        return value;
    }
}
