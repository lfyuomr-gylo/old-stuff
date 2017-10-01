package com.github.lfyuomr.gylo.bostongene.task3;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class YandexTranslationService {
    private static final String COPYRIGHT_STRING =
            "\n--\nПереведено сервисом \"Яндекс.Переводчик -- http://translate.yandex.ru/\"";

    private final String apiKey;

    public YandexTranslationService(@NotNull String key) {
        try {
            this.apiKey = URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Absolutely unexpected exception. UTF-8 is supported for sure", e);
        }
    }

    /**
     * Translate text from English to Russian.
     *
     * @param text text to be translated
     * @return translated text
     * @throws IOException if any I/O exception occurs
     * @throws APIKeyException if server returns error status caused by API key
     * @throws TranslationException if server returns error status due to inability to translate the text.
     */
    public @NotNull String translate(@NotNull String text) throws IOException, APIKeyException, TranslationException {
        val connection = (HttpURLConnection) createTranslateRequest(text).openConnection();
        handleResponseCode(connection.getResponseCode());
        try (val inputStream = connection.getInputStream();
                val inputWriter = new ByteArrayOutputStream()) {
            val buff = new byte[1024];
            for (int bytesRead; (bytesRead = inputStream.read(buff)) != -1;) {
                inputWriter.write(buff, 0, bytesRead);
            }

            val responseJson = inputWriter.toString("UTF-8");
            return getTranslationFromJson(responseJson);
        }
    }

    private @NotNull URL createTranslateRequest(String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
            val requestString =
                    "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + apiKey +
                            "&text=" + text +
                            "&lang=en-ru";
            return new URL(requestString);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Absolutely unexpected exception. UTF-8 is supported for sure", e);
        } catch (MalformedURLException e) { // nothing to do with that.
            throw new RuntimeException(e);
        }
    }

    private void handleResponseCode(int code) throws APIKeyException, TranslationException {
        switch (code) {
            case 200: return;
            case 401: throw new APIKeyException("Неправильный API-ключ.");
            case 402: throw new APIKeyException("API-ключ заблокирован.");
            case 404: throw new APIKeyException("Превышено суточное ограничение на объем переведенного текста.");
            case 413: throw new TranslationException("Превышен максимально допустимый размер текста.");
            case 422: throw new TranslationException("Текст не может быть переведен.");
            case 501: throw new TranslationException("Данное направление перевода не поддерживается.");
            default:
                System.err.println("Unexpected response code: " + code + ". Ignore it");
        }
    }

    private @NotNull String getTranslationFromJson(String json) {
        val response = new Gson().fromJson(json, YandexTranslatorResponse.class);
        if (response.getText() == null || response.getText().size() == 0) {
            System.err.println("Response unexpectedly doesn't contain text.");
            return "";
        } else if (response.getText().size() > 1) {
            System.err.println("Response unexpectedly contains more than one text. Use only the first one.");
        }

        return response.getText().get(0);
    }

    /** Exception thrown when some error about the API key occurs.  */
    public static class APIKeyException extends Exception {
        public APIKeyException(String message) {
            super(message);
        }
    }


    /** Exception thrown when some error in translation occurs.  */
    public static class TranslationException extends Exception {
        public TranslationException(String message) {
            super(message);
        }
    }


    private static final class YandexTranslatorResponse {
        @Getter private String lang;
        @Getter private List<String> text;
    }
}
