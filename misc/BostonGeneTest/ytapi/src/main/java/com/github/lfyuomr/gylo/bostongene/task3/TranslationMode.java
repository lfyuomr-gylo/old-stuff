package com.github.lfyuomr.gylo.bostongene.task3;

import lombok.Getter;

public enum TranslationMode {
    WHOLE_TEXT("Текст будет считан и переведен целиком."),
    LINES("Текст будет считываться и переводиться построчно.");

    @Getter private final String description;

    TranslationMode(String description) {
        this.description = description;
    }
}
