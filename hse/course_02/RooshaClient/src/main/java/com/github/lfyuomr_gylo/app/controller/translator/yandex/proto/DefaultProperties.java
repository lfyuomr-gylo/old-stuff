package com.github.lfyuomr_gylo.app.controller.translator.yandex.proto;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("WeakerAccess")
class DefaultProperties {
    @SerializedName("text")
    protected @NotNull String text = "";

    @SerializedName("number")
    protected @Nullable String number;

    @SerializedName("partOfSpeech")
    protected @Nullable String partOfSpeech;

    @SerializedName("gender")
    protected @Nullable String gender;

    DefaultProperties() {
    }

    DefaultProperties(
            @NotNull String text,
            @Nullable String number,
            @Nullable String partOfSpeech,
            @Nullable String gender) {
        this.text = text;
        this.number = number;
        this.partOfSpeech = partOfSpeech;
        this.gender = gender;
    }

    DefaultProperties(@NotNull DefaultProperties other) {
        this.text = other.text;
        this.number = other.number;
        this.partOfSpeech = other.partOfSpeech;
        this.gender = other.gender;
    }

    public @NotNull String getText() {
        return text;
    }

    public @Nullable String getNumber() {
        return number;
    }

    public @Nullable String getPartOfSpeech() {
        return partOfSpeech;
    }

    public @Nullable String getGender() {
        return gender;
    }
}
