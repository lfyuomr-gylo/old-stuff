package com.github.lfyuomr.gylo.bostongene.task3;

import lombok.val;
import org.apache.commons.cli.Option;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class APIKeyUtil {
    static final String ENVIRONMENT_VARIABLE_NAME = "YANDEX_TRANSLATOR_API_KEY";
    static final File FILE_LOCATION =
            new File(System.getProperty("user.home"), ".yandex_translator_api.key");

    private static final Option CMD_ARGUMENT =
            Option.builder("k").longOpt("key").desc("Yandex API key").numberOfArgs(1).build();

    public static Option getCmdArgument() {
        return Option.builder(CMD_ARGUMENT.getOpt())
                .longOpt(CMD_ARGUMENT.getLongOpt())
                .desc(CMD_ARGUMENT.getDescription())
                .numberOfArgs(CMD_ARGUMENT.getArgs())
                .required(CMD_ARGUMENT.isRequired())
                .build();
    }

    private APIKeyUtil() {}

    /**
     * Find Yandex Translator API key. The key will be searched in following locations in specified order:
     * <ol>
     *     <li>Environment variable which name is set in {@link APIKeyUtil#ENVIRONMENT_VARIABLE_NAME}.</li>
     *     <li>File, specified in {@link APIKeyUtil#FILE_LOCATION}.</li>
     * </ol>
     *
     * @return key value if key was found, {@code null} otherwise.
     */
    public static @Nullable String findKey() {
        String key;
        if ((key = getFromEnvironment()) == null &&
                (key = getFromFile()) == null) {
            key = null;
        }

        return key;
    }

    private static @Nullable String getFromEnvironment() {
        try {
            return System.getenv(ENVIRONMENT_VARIABLE_NAME);
        } catch (SecurityException e) {
            return null;
        }
    }

    private static @Nullable String getFromFile() {
        if (FILE_LOCATION.isFile() && FILE_LOCATION.canRead()) {
            try (val file = new RandomAccessFile(FILE_LOCATION, "r")) {
                return file.readLine();
            } catch (IOException ignored) {}
        }

        return null;
    }
}
