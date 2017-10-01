package com.github.lfyuomr_gylo.app.controller.translator.yandex.proto;

import com.github.lfyuomr_gylo.app.controller.translator.yandex.TranslationFormat;
import com.github.lfyuomr_gylo.app.model.translator.Case;
import com.github.lfyuomr_gylo.app.model.translator.Meaning;
import com.github.lfyuomr_gylo.app.model.translator.Translation;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

class DicResult implements TranslationFormat {
    @SerializedName("def")
    private @Nullable Definition[] definitions;

    @Override
    public @NotNull Translation toTranslation() {
        // TODO: passing "" to first argument of Translation constructor may cause error in TranslationView logic. Check it!
        if (definitions == null || definitions.length == 0) {
            return new Translation("", Collections.emptyList());
        }
        final String text = definitions[0] == null ? "" : definitions[0].getText();
        Collection<Case> cases = new ArrayList<>();
        Arrays.stream(definitions)
              .filter(def -> def != null)
              .map(Definition::toCaseArray)
              .map(Arrays::asList)
              .forEach(cases::addAll);
        return new Translation(text, cases);
    }

    private static class Definition extends DefaultProperties {
        @SerializedName("ts")
        private @Nullable String transcription;

        @SerializedName("tr")
        private @Nullable Translation[] translations;

        @NotNull Case[] toCaseArray() {
            if (translations == null) {
                return new Case[]{new Case(new Meaning(text, null, null), new YandexDictionary())};
            }
            return Arrays.stream(translations)
                         .filter(x -> x != null)
                         .map(Translation::toMeaning)
                         .map(meaning -> new Case(meaning, new YandexDictionary()))
                         .toArray(Case[]::new);
        }

        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        static class Translation extends DefaultProperties {
            /**
             * Synonyms in target language.
             */
            @SerializedName("syn")
            private @Nullable DefaultProperties[] synonyms;

            /**
             * Synonyms in source language.
             */
            @SerializedName("mean")
            private @Nullable DefaultProperties[] means;

            /**
             * Examples of usage in source language.
             */
            @SerializedName("ex")
            private @Nullable DefaultProperties[] examples;

            @NotNull Meaning toMeaning() {
                final String description = (means != null && means[0] != null) ? means[0].text : null;
                final String example = (examples != null && examples[0] != null) ? examples[0].text : null;
                return new Meaning(text, description, example);
            }
        }
    }
}


