package com.github.lfyuomr.gylo.kango.client.util;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
@FunctionalInterface
public interface TriConsumer<T1, T2, T3> {
    void accept(T1 arg1, T2 arg2, T3 arg3);
    default TriConsumer<T1, T2, T3> andThen(TriConsumer<? super T1, ? super T2, ? super T3> after) {
        Objects.requireNonNull(after);

        return (a1, a2, a3) -> {
            accept(a1, a2, a3);
            after.accept(a1, a2, a3);
        };
    }
}
