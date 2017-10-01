package com.github.lfyuomr.gylo.lamport.mutex.demo;

import com.github.lfyuomr.gylo.lamport.mutex.Configuration;
import com.github.lfyuomr.gylo.lamport.mutex.DistributedMutex;
import com.github.lfyuomr.gylo.lamport.mutex.MutexDestroyedException;
import lombok.val;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

/**
 * NOTE: every {@link DistributedMutex} instance created by any instance of this file will write it's log
 * to the same file, so if you run multiple instances with different system configurations, it will spoil the log.
 */
public class Demo {
    private static final String LOG_FILE_NAME = "lamport-mutex-local.log";
    private static final Logger log = LoggerFactory.getLogger("lmutex-demo");

    public static void main(String[] args) {
        try {
            new Demo(args).run();
        } catch (IllegalArgumentException e) { // exceptions thrown manually
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Can not create specified log file: " + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------
    private final CliArgs cliArgs;

    public Demo(String... args) throws IOException, ParseException, IllegalArgumentException {
        this.cliArgs = new CliArgs(args);
        DistributedMutex.setDefaultLogger(log);
    }

    public void run() {
        if (cliArgs.isHelp()) {
            CliArgs.printHelp();
            return;
        }

        printArgs();
        System.out.println();

        if (cliArgs.isSingleProcess()) {
            autoStressTest();
        }
        else {
            if (cliArgs.getMode() == CliArgs.Mode.STRESS) {
                stressTest(cliArgs.getStressModeIterations(), cliArgs.getFile(), cliArgs.getConfiguration());
            }
            else {
                cliTest(cliArgs.getFile(), cliArgs.getConfiguration());
            }
        }

        parseLog();
        removeLog();
    }

    private void autoStressTest() {
        val configs = generateConfigurations(cliArgs.getNodesNum());
        val threads = configs.stream()
                             .map(config -> new Thread(() ->
                                     stressTest(cliArgs.getStressModeIterations(), cliArgs.getFile(), config)
                             ))
                             .collect(toList());

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stressTest(int iterations, File fileName, Configuration config) {
        int i = 0;
        try (val mutex = new DistributedMutex(config)) {
            for (i = 0; i < iterations; i++) {
                try {
                    mutex.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

                try (val fileWriter = new UniqueFileWriter(fileName)) {
                    val note = "Process " + config.getMyId() + " acquired lock\n";
                    fileWriter.append(note);
                    System.out.println("-----> " + note);
                } catch (IllegalMonitorStateException e) {
                    System.out.println("======>ERROR: Failed to acquire file lock. " +
                            "It seems like another process has already locked file.");
                    throw new RuntimeException("Failed to acquire file lock. " +
                            "It seems like another process has already locked file.", e);
                }

                mutex.unlock();
                System.out.println("-----> Process" + config.getMyId() + " released mutex");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MutexDestroyedException e) {
            System.out.println(config.getMyId() + "> MUTEX DESTROYED ON ITERATION " + i);
            return;
        }

        System.out.println(config.getMyId() + "> MUTEX DESTROYED AFTER ALL ITERATIONS");
    }

    private void cliTest(File file, Configuration config) {
        try {
            System.out.println("Creating mutex....");
            val mutex = new DistributedMutex(config);
            System.out.println("Mutex successfully created.\nStarting CLI....");

            val cli = new Cli(mutex, file, config.getMyId());
            cli.run();
        } catch (IOException e) {
            System.err.println("Failed to create mutex. Exit.");
            e.printStackTrace();
        }
    }

    private void printArgs() {
        System.out.println("file: " + cliArgs.getFile());
        System.out.println("mode: " + cliArgs.getMode());
        System.out.println("nodesNum: " + cliArgs.getNodesNum());
        System.out.println("singleProcess: " + cliArgs.isSingleProcess());
        System.out.println("stressModeIterations: " + cliArgs.getStressModeIterations());
        System.out.println("configuration: " + cliArgs.getConfiguration());
    }

    private void parseLog() {
        System.out.println("Parse log.....");

        final LogParser parser;
        try {
            parser = new LogParser(LOG_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (parser.isOk()) {
            System.out.println("Log is ok.");
        } else {
            System.out.println("Error found in log: " + parser.getErrorMessage());
        }
    }

    private void removeLog() {
        System.out.println("Removing file with log....");
        if (new File(LOG_FILE_NAME).delete()) {
            System.out.println("Log file was successfully removed.");
        } else {
            System.out.println("Failed to remove log file.");
        }

    }

    private List<Configuration> generateConfigurations(int nodesNum) {
        val addrs = new Random(System.nanoTime()).ints(10_000, 60_000)
                                      .distinct()
                                      .limit(nodesNum)
                                      .mapToObj(port -> new InetSocketAddress("localhost", port))
                                      .collect(toList());

        val result = new ArrayList<Configuration>(nodesNum);
        for (int i = 0; i < nodesNum; i++) {
            val configBuilder = Configuration.builder().myId(i + 1).myPort(addrs.get(i).getPort());
            for (int j = 0; j < nodesNum; j++) {
                if (j != i) {
                    configBuilder.otherNode(j + 1, addrs.get(j));
                }
            }
            result.add(configBuilder.build());
        }

        return result;
    }
}

