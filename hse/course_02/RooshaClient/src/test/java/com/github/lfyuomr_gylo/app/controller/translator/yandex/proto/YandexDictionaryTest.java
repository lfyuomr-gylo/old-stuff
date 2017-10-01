package com.github.lfyuomr_gylo.app.controller.translator.yandex.proto;

import com.github.lfyuomr_gylo.app.controller.translator.yandex.TranslationFormat;
import com.github.lfyuomr_gylo.app.model.translator.Translation;
import io.reactivex.Single;
import org.junit.Test;

import java.util.Locale;

public class YandexDictionaryTest {
    @Test
    public void lookup() throws Exception {
        final Single<? extends TranslationFormat> translation =
                new YandexDictionary().translate("time", Locale.ENGLISH, new Locale("ru"));
        translation.map(TranslationFormat::toTranslation).subscribe(
                this::onSuccess,
                this::onError
        );
    }

    private void onSuccess(Translation result) {
        System.out.println("Done");
    }

    private void onError(Throwable error) {
        error.printStackTrace();
    }

}