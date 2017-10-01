package com.github.lfyuomr.gylo.bostongene.task3;

import lombok.Getter;
import lombok.val;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;

public class CLIArgs {
    private static final TranslationMode DEFAULT_MODE = TranslationMode.LINES;

    private final Options options;

    @Getter private @Nullable String apiKey = null;
    @Getter private @Nullable String inputFileName = null;
    @Getter private @Nullable String outputFileName = null;
    @Getter private @NotNull TranslationMode mode = DEFAULT_MODE;
    @Getter private boolean printHelp = false;

    public CLIArgs(String... args) {
        val keyOption = Option.builder("k").longOpt("key").numberOfArgs(1).argName("KEY").required(false)
                              .desc("API-ключ Яндекс переводчика").build();
        val inputOption = Option.builder("f").longOpt("file").numberOfArgs(1).argName("FILE").required(false)
                                .desc("Имя входного файла. По умолчанию используется стандартный поток ввода").build();
        val outputOption = Option.builder("o").longOpt("out").numberOfArgs(1).argName("FILE").required(false)
                                 .desc("Имя выходного файла. По умолчанию используется стандартный поток вывода").build();
        val modeOption = Option.builder("m").longOpt("mode").numberOfArgs(1).argName("MODE").required(false)
                               .desc("Режим перевода. По умолчанию: " + DEFAULT_MODE + ". " +
                                       "Возможные значения:\n" + describeModes())
                               .build();
        val helpOption = Option.builder("h").longOpt("help").required(false)
                               .desc("Вывести справочное сообщение").build();

        options = new Options().addOption(keyOption).addOption(inputOption).addOption(outputOption)
                               .addOption(modeOption).addOption(helpOption);


        val parser = new DefaultParser();
        try {
            val cmd = parser.parse(options, args);
            printHelp = cmd.hasOption(helpOption.getOpt());
            apiKey = cmd.getOptionValue(keyOption.getOpt());
            inputFileName = cmd.getOptionValue(inputOption.getOpt());
            outputFileName = cmd.getOptionValue(outputOption.getOpt());
            mode = modeFromString(cmd.getOptionValue(modeOption.getOpt()));
        } catch (ParseException ignored) {
            System.out.println("Ошибка при обработке аргументов командной строки. " +
                    "Будут использованы настройки по умолчанию.");
        }
    }

    public void printHelp(PrintWriter writer) {
        val formatter = new HelpFormatter();
        formatter.printHelp(writer, 81, "program",
                "Перевести текст с помощью Яндекс Переводчика", options, 0, 10, "\n"
        );
        writer.flush();
    }

    private TranslationMode modeFromString(@Nullable String modeString) {
        if (modeString != null) {
            for (val mode : TranslationMode.values()) {
                if (mode.toString().equalsIgnoreCase(modeString)) {
                    return mode;
                }
            }
        }
        return DEFAULT_MODE;
    }

    private String describeModes() {
        val result = new StringBuilder();
        for (val mode : TranslationMode.values()) {
            result.append(mode.toString()).append(" -- ").append(mode.getDescription()).append("\n");
        }
        return result.toString();
    }

}
