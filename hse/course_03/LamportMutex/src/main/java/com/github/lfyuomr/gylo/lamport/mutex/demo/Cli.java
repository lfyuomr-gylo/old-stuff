package com.github.lfyuomr.gylo.lamport.mutex.demo;

import com.github.lfyuomr.gylo.lamport.mutex.DistributedMutex;
import com.github.lfyuomr.gylo.lamport.mutex.MutexDestroyedException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.StringsCompleter;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.stream;

class Cli {
    private boolean continueRunning = true;

    private boolean locked = false;

    private final DistributedMutex mutex;
    private final File fileName;
    private final LineReader lineReader;
    private final String prompt;

    Cli(DistributedMutex mutex, File fileName, int processNumber) {
        this.fileName = fileName;
        this.mutex = mutex;

        val completer = new StringsCompleter(
                stream(Command.values()).map(Command::toString).map(String::toLowerCase)::iterator);
        lineReader = LineReaderBuilder.builder().completer(completer).build();
        prompt = "process " + processNumber + "> ";
    }

    void run() {
        while (continueRunning) {
            try {
                val line = lineReader.readLine(prompt).trim();
                val command = parseCommand(line);
                if (command == null) {
                    System.out.println("Unknown command: " + line + ". Type 'help' to get list of available commands.");
                    continue;
                }

                switch (command) {
                    case HELP:
                        printHelp();
                        break;
                    case LOCK:
                        System.out.println("Lock mutex.");
                        mutex.lock();
                        locked = true;
                        System.out.println("Lock acquired. You're now mutex lock owner. Do not forget to release it!");
                        break;
                    case PRINT:
                        val split = line.split(" ", 2);
                        if (split.length <= 2) {
                            System.out.println("Nothing to print. Try again.");
                            continue;
                        }

                        if (!locked) {
                            System.out.println("Can not print to file while mutex is not locked.");
                        } else {
                            printToFile(split[1]);
                            System.out.println("Successfully printed to file.");
                        }
                        break;
                    case RELEASE:
                        if (!locked) {
                            System.out.println("Mutex is not locked. You need to lock mutex first.");
                        } else {
                            mutex.unlock();
                            locked = false;
                            System.out.println("Lock successfully released.");
                        }
                        break;
                    case EXIT:
                        throw new UserInterruptException("");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
                stop();
            } catch (UserInterruptException | EndOfFileException e) {
                System.out.println("Interrupted by user. Destroy mutex and stop.");
                stop();
            } catch (MutexDestroyedException e) {
                System.out.println("Mutex destroyed.");
                stop();
            }
        }
    }

    void stop() {
        mutex.close();
        continueRunning = false;
    }

    private @Nullable Command parseCommand(String line) {
        for (Command command : Command.values()) {
            if (command == Command.PRINT) {
                val split = line.split(" ");
                if (split.length >= 1 && split[0].equalsIgnoreCase(Command.PRINT.toString())) {
                    return Command.PRINT;
                }
            } else if (line.equalsIgnoreCase(command.toString())) {
                return command;
            }
        }

        return null;
    }

    private void printHelp() {
        System.out.println("Welcome to Lamport Mutex implementation command line interface. Type one of appropriate " +
                "commands(all commands are case insensitive):");
        for (Command command : Command.values()) {
            System.out.println(command);
        }
    }

    private void printToFile(String s) {
        try (val writer = new UniqueFileWriter(fileName)) {
            writer.append(s);
        } catch (IllegalMonitorStateException e) {
            System.err.println(e.getMessage());
            System.err.println("Exit.");
            stop();
        } catch (IOException e) {
            System.err.println("IOException:");
            e.printStackTrace();
            stop();
        }
    }

    @RequiredArgsConstructor
    private enum Command {
        HELP("print help message."),
        LOCK("lock mutex(blocking command."),
        PRINT("print line specified after the command(separated by one space) to the file(fails if not locked mutex)"),
        RELEASE("release lock(fails if not locked)."),
        EXIT("destroy mutex and stop the system."),;

        private final String description;

        @Override
        public String toString() {
            return super.toString().toLowerCase() + " -- " + description;
        }
    }
}
