package com.github.lfyuomr.gylo.lamport.mutex.demo;

import com.github.lfyuomr.gylo.lamport.mutex.Configuration;
import lombok.Getter;
import lombok.val;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static java.util.Arrays.stream;

class CliArgs {
    private static final String HELP_OPTION = "h";
    private static final String AUTO_OPTION = "a";
    private static final String FILE_OPTION = "f";
    private static final String MODE_OPTION = "m";
    private static final String STRESS_ITERS_OPTION = "n";
    private static final String MY_ID_OPTION = "id";
    private static final String MY_PORT_OPTION = "p";
    private static final String OTHER_IDS_OPTION = "ids";
    private static final String OTHER_PORTS_OPTION = "ps";

    private static final String DEFAULT_FILE = System.getProperty("user.home") + "/.lmutex.txt";
    private static final int DEFAULT_NODES_NUM = 10; // for 'auto' mode.
    private static final int DEFAULT_STRESS_ITERATIONS = 10_000; // for STRESS mode.
    private static final Mode DEFAULT_MODE = Mode.STRESS;

    private static final Options options = defineOptions();


    public static void printHelp() {
        new HelpFormatter().printHelp("/path/to/program", options);
    }

    private static Options defineOptions() {
        val options = new Options();

        val help = Option.builder("h").longOpt("help").desc("print help").build();
        options.addOption(help);

        val file = Option.builder(FILE_OPTION)
                         .longOpt("file")
                         .hasArg()
                         .numberOfArgs(1)
                         .required(false)
                         .desc("Path to file, which will be used to store lock/release log. " +
                                 "By default, it's '" + DEFAULT_FILE + "'.")
                         .build();
        options.addOption(file);

        val auto = Option.builder(AUTO_OPTION)
                         .longOpt("auto")
                         .optionalArg(true)
                         .numberOfArgs(1)
                         .required(false)
                         .desc("If this option is set, configuration will be created automatically with specified " +
                                 "number of arguments(by default, " + DEFAULT_NODES_NUM + ") " +
                                 "and stress test will be run. " +
                                 "The other configuration options will be ignored if this option is set. ")
                         .build();
        options.addOption(auto);

        val mode = Option.builder(MODE_OPTION)
                         .longOpt("mode")
                         .required(false)
                         .hasArg()
                         .numberOfArgs(1)
                         .desc("Specifies, whether CLI or stress test mode will be used. By default, " +
                                 "'" + DEFAULT_MODE.toString().toLowerCase() + "'. " +
                                 "Available case insensitive values: " +
                                 join(", ", (Iterable<String>) stream(Mode.values()).map(m -> "'" + m + "'")::iterator) +
                                 ".")
                         .build();
        options.addOption(mode);

        val stressIters = Option.builder(STRESS_ITERS_OPTION)
                                .longOpt("stress-iters")
                                .required(false)
                                .hasArg()
                                .numberOfArgs(1)
                                .desc("Specifies number of iterations for stress test mode. " + "By default, " +
                                        DEFAULT_STRESS_ITERATIONS + "." +
                                        "Ignored in CLI mode.")
                                .build();
        options.addOption(stressIters);

        val myId = Option.builder(MY_ID_OPTION)
                         .longOpt("my-id")
                         .hasArg()
                         .numberOfArgs(1)
                         .required(false)
                         .desc("Id of this process.")
                         .build();
        options.addOption(myId);

        val myPort = Option.builder(MY_PORT_OPTION)
                           .longOpt("my-port")
                           .hasArg()
                           .numberOfArgs(1)
                           .required(false)
                           .desc("Port of this process.")
                           .build();
        options.addOption(myPort);

        val otherIds = Option.builder(OTHER_IDS_OPTION)
                             .longOpt("other-id")
                             .hasArgs()
                             .valueSeparator(',')
                             .required(false)
                             .desc("Ids of the other processes.")
                             .build();
        options.addOption(otherIds);

        val otherPorts = Option.builder(OTHER_PORTS_OPTION)
                               .longOpt("other-ports")
                               .hasArgs()
                               .valueSeparator(',')
                               .required(false)
                               .desc("Ports of the other processes in the same order as ids.")
                               .build();
        options.addOption(otherPorts);


        return options;
    }

    // ----------------------------------------------------------
    private final CommandLine cmd;
    @Getter private final boolean help;
    @Getter private final File file;
    @Getter private final Mode mode;
    @Getter private final int stressModeIterations;
    @Getter private final boolean singleProcess; // auto option
    @Getter private final int nodesNum; // for auto option only

    @Getter private final Configuration configuration; // null if auto option is set

    CliArgs(String[] args) throws ParseException, IOException {
        val parser = new DefaultParser();
        this.cmd = parser.parse(options, args);

        this.help = cmd.hasOption(HELP_OPTION);
        this.file = extractFile();
        this.mode = extractMode();
        if (mode == Mode.STRESS) {
            this.stressModeIterations = extractStressModeIterations();
        }
        else {
            this.stressModeIterations = -1;
        }
        singleProcess = cmd.hasOption(AUTO_OPTION);
        if (singleProcess) {
            val nodesNumValue = cmd.getOptionValue(AUTO_OPTION);
            this.nodesNum = nodesNumValue == null ? DEFAULT_NODES_NUM : parseInt(nodesNumValue);
            this.configuration = null;
        }
        else {
            this.nodesNum = 1;
            this.configuration = extractConfiguration();
        }
    }

    private File extractFile() throws IOException {
        final File file;
        if (cmd.hasOption(FILE_OPTION)) {
            file = new File(cmd.getOptionValue(FILE_OPTION));
        }
        else {
            file = new File(DEFAULT_FILE);
        }
        file.delete();
        file.createNewFile();
        return file;
    }

    private Mode extractMode() {
        if (cmd.hasOption(MODE_OPTION)) {
            val mode = cmd.getOptionValue(MODE_OPTION);
            if (mode.equalsIgnoreCase(Mode.STRESS.toString())) {
                return Mode.STRESS;
            }
            else if (mode.equalsIgnoreCase(Mode.CLI.toString())) {
                return Mode.CLI;
            }
            else {
                throw new IllegalArgumentException("Unexpected mode: '" + mode + "'");
            }
        }
        return DEFAULT_MODE;
    }

    private int extractStressModeIterations() {
        if (cmd.hasOption(STRESS_ITERS_OPTION)) {
            val iters = cmd.getOptionValue(STRESS_ITERS_OPTION);
            return parseInt(iters);
        }
        return DEFAULT_STRESS_ITERATIONS;
    }

    private Configuration extractConfiguration() {
        if (help) {
            return null;
        }
        val myIdValue = cmd.getOptionValue(MY_ID_OPTION);
        assertOptionNotNull(MY_ID_OPTION, myIdValue);
        val myPortValue = cmd.getOptionValue(MY_PORT_OPTION);
        assertOptionNotNull(MY_PORT_OPTION, myPortValue);
        val otherIdsValue = cmd.getOptionValues(OTHER_IDS_OPTION);
        assertOptionNotNull(OTHER_IDS_OPTION, otherIdsValue);
        val otherPortsValue = cmd.getOptionValues(OTHER_PORTS_OPTION);
        assertOptionNotNull(OTHER_PORTS_OPTION, otherPortsValue);
        if (otherIdsValue.length != otherPortsValue.length) {
            throw new IllegalArgumentException("other ids number doesn't match other ports number");
        }

        val config = Configuration.builder().myId(parseInt(myIdValue)).myPort(parseInt(myPortValue));

        for (int i = 0; i < otherIdsValue.length; i++) {
            val id = parseInt(otherIdsValue[i]);
            val port = parseInt(otherPortsValue[i]);
            val address = new InetSocketAddress("localhost", port);
            config.otherNode(id, address);
        }

        return config.build();
    }

    private void assertOptionNotNull(String option, @Nullable Object optionValue) {
        if (optionValue == null && !help) {
            throw new IllegalArgumentException("Missing option: '" + option + "'");
        }
    }

    enum Mode {
        STRESS,
        CLI,;
    }
}
