package com.github.lfyuomr.gylo.bostongene.task3;

import com.github.lfyuomr.gylo.bostongene.task3.YandexTranslationService.APIKeyException;
import com.github.lfyuomr.gylo.bostongene.task3.YandexTranslationService.TranslationException;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Scanner;

public class TranslationApp implements Closeable {
    private boolean continueListening;

    private final BufferedReader in;
    private final Scanner inputScanner;
    private final PrintStream out;
    private final TranslationMode mode;
    private final YandexTranslationService translationService;

    public TranslationApp(
            @NotNull InputStream in,
            @NotNull PrintStream out,
            @NotNull TranslationMode mode,
            @NotNull YandexTranslationService service) {
        this.continueListening = true;
        this.in = new BufferedReader(new InputStreamReader(in));
        this.inputScanner = new Scanner(in);
        this.out = out;
        this.mode = mode;
        this.translationService = service;
    }

    public void run() {
        for (String text; continueListening && (text = next()) != null; ) {
            if (!translateAndPrint(text)) {
                return;
            }
        }
        continueListening = false;
    }

    private boolean translateAndPrint(@NotNull String text) {
        try {
            val translation = translationService.translate(text);
            out.println(translation);
            return true;
        } catch (IOException e) {
            System.out.println("Не удалось подключиться к серверу Яндекс переводчика.");
        } catch (APIKeyException e) {
            System.out.println("Ошибка API-ключа: " + e.getMessage());
        } catch (TranslationException e) {
            System.out.println("Невозможно перевести введенный текст: " + e.getMessage());
        }

        return false;
    }

    private @Nullable String next() {
        switch (mode) {
            case WHOLE_TEXT:
                continueListening = false; // since only one string should be read.
                val buff = new char[1024];
                try (val inputWriter = new CharArrayWriter()) {
                    for (int charsRead; (charsRead = in.read(buff)) != -1; ) {
                        inputWriter.write(buff, 0, charsRead);
                    }

                    val input = inputWriter.toString();
                    return input.length() > 0 ? input : null;
                } catch (IOException ignored) {
                    return null;
                }
            case LINES:
                try {
                    return in.readLine();
                } catch (IOException e) {
                    return null;
                }
            default:
                throw new IllegalStateException("unexpected mode: " + mode);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
        try {
            in.close();
        } catch (IOException ignored) {}
    }
}
