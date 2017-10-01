package com.github.lfyuomr.gylo.bostongene.task1;

import lombok.val;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Option helpOption;
    private static final Option blockingOption;
    private static final Option intervalOption;

    private static final int DEFAULT_RETRIEVAL_INTERVAL = 5000;

    static {
        helpOption = Option.builder("h").longOpt("help").required(false)
                           .desc("Показать справочное сообщение").build();
        blockingOption = Option.builder("b").longOpt("blocking").required(false)
                               .desc("Всегда дожидаться ввода числа прежде, чем выводить минимальное.").build();
        intervalOption = Option.builder("t").longOpt("interval").required(false).argName("MILLIS").numberOfArgs(1)
                               .desc("Задать интервал извлечения минимального числа в миллисекундах").build();
    }

    public static void main(String[] args) throws IOException {
        val options = new Options().addOption(helpOption).addOption(blockingOption).addOption(intervalOption);
        val parser = new DefaultParser();
        try {
            val cmd = parser.parse(options, args);
            if (cmd.hasOption(helpOption.getOpt())) {
                val printer = new HelpFormatter();
                printer.printHelp("program", options);
                return;
            }

            val interval = cmd.hasOption(intervalOption.getOpt()) ?
                    Integer.parseInt(cmd.getOptionValue(intervalOption.getOpt())) :
                    DEFAULT_RETRIEVAL_INTERVAL;
            val blocking = cmd.hasOption(blockingOption.getOpt());

            runTask(interval, blocking);
        } catch (ParseException e) {
            System.out.println("Ошибка в аргументах командной строки:" + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("В качестве интервала извлечения указано не число");
        }
    }

    private static void runTask(int retrievalIntervalMillis, boolean blocking) {
        val inputQueue = new MyThreadSafePriorityQueue<Integer>();
        val retriever = new Retreiver(System.out, inputQueue, blocking, retrievalIntervalMillis);

        val executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            readNumbers(System.in, inputQueue);
            executor.shutdownNow();
        });
        executor.execute(retriever::run);
        executor.shutdown();

        try {
            while (!executor.isTerminated()) {
                executor.awaitTermination(DEFAULT_RETRIEVAL_INTERVAL, TimeUnit.MILLISECONDS);
            }
            System.out.println("Процесс успешно завершен");
        } catch (InterruptedException e) {
            System.out.println("Поток выполнения был неожиданно прерван");
            Thread.currentThread().interrupt();
        }
    }

    private static void readNumbers(InputStream in, MyThreadSafePriorityQueue<Integer> queue) {
        val reader = new BufferedReader(new InputStreamReader(in));
        try {
            for (String line; (line = reader.readLine()) != null;) {
                try {
                    val number = new EnglishNumeral(line);
                    queue.push(number.getIntegerValue());
                } catch (EnglishNumeral.NumeralParserException e) {
                    System.out.println("Не удалось распарсить число: " + line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода.");
        }
    }
}
