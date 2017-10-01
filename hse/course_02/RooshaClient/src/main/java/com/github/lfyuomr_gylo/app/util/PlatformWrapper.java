package com.github.lfyuomr_gylo.app.util;

import org.jetbrains.annotations.NotNull;

import static javafx.application.Platform.runLater;

public interface PlatformWrapper {
    /**
     * Returns mirrored consumer, which will be invoked on JavaFX Application Thread only.
     * @param consumer consumer to be wrapped to {@link javafx.application.Platform#runLater(Runnable)}
     * @param <T> consumer argument type
     * @return specified consumer wrapped to {@link javafx.application.Platform#runLater(Runnable)}
     */
    static <T> @NotNull io.reactivex.functions.Consumer<T> onFX(@NotNull java.util.function.Consumer<T> consumer) {
        return t -> runLater(() -> consumer.accept(t));
    }
}
