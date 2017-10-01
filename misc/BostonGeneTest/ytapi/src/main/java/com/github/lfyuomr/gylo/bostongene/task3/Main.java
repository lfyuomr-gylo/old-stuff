package com.github.lfyuomr.gylo.bostongene.task3;

import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        val arguments = new CLIArgs(args);

        val output = openOutputFileOrUseStdout(arguments.getOutputFileName());
        if (arguments.isPrintHelp()) {
            arguments.printHelp(new PrintWriter(output));
            output.close();
            return;
        }
        val input = openInputFileOrUseStdin(arguments.getInputFileName());

        String apiKey;
        if ((apiKey = arguments.getApiKey()) == null &&
                (apiKey = APIKeyUtil.findKey()) == null) {
            System.out.println("Не удалось найти API ключ Яндекс Переводчика.");
            return;
        }

        val translationService = new YandexTranslationService(apiKey);
        try (val translationApp = new TranslationApp(input, output, arguments.getMode(), translationService)) {
            translationApp.run();
        } catch (IOException ignored) {}

    }

    private static InputStream openInputFileOrUseStdin(@Nullable String fileName) {
        if (fileName != null) {
            try {
                return new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                System.out.println("Не удалось найти указанный входной файл. " +
                        "Будет использован стандартный поток ввода.");
            } catch (SecurityException e) {
                System.out.println("Не удалось открыть указанный входной файл. " +
                        "Будет использован стандартный поток ввода.");
            }
        }
        return System.in;
    }

    private static PrintStream openOutputFileOrUseStdout(@Nullable String fileName) {
        if (fileName != null) {
            try {
                return new PrintStream(new FileOutputStream(fileName, true));
            } catch (FileNotFoundException e) {
                System.out.println("Не удалось найти указанный выходной файл. " +
                        "Будет использован стандартный поток вывода.");
            } catch (SecurityException e) {
                System.out.println("Не удалось открыть указанный выходной файл. " +
                        "Будет использован стандартный поток вывода.");
            }
        }
        return System.out;
    }
}
