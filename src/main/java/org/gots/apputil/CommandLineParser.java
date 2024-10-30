package org.gots.apputil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by goc on 30/10/2024
 */
@RequiredArgsConstructor
public class CommandLineParser {

    @Getter
    private boolean helpRequested = false;

    private static final String UNRECOGNIZED_ARGUMENT = "Unrecognized command line argument '%s'. Launch 'java -jar preauthorize-crawler-1.0.jar --help' for detailed info about options\n";

    private final CommandLineArguments commandLineArguments;
    private final String[] args;
    private final AppAbleToShowHelp appAbleToShowHelp;

    public static void parseArgs(CommandLineArguments commandLineArguments, String[] args, AppAbleToShowHelp appAbleToShowHelp) {
        CommandLineParser parser = new CommandLineParser(commandLineArguments, args, appAbleToShowHelp);
        parser.process();
    }

    private void process() {

        parseArgs();

        if (isHelpRequested()) {
            appAbleToShowHelp.printHelp();
            return;
        }

        printResult();

    }

    private void printResult() {
        System.out.println("Application settings according to default values and values got from command line:");
        commandLineArguments.getArguments().forEach(arg -> System.out.printf("\t%s=%s\n", arg.getKey(), arg.getValue()));
        System.out.println("If you have any question try launch just with --help parameter");
    }

    private void parseArgs() {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                if (arg.equals("--help")) {
                    helpRequested = true;
                    break;
                }

                String[] splitArg = arg.substring(2).split("=", 2);
                if (splitArg.length != 2 ||
                    !commandLineArguments.setArgumentValue(splitArg[0], splitArg[1])
                ) {
                        System.err.printf(UNRECOGNIZED_ARGUMENT, splitArg[0]);
                }
            }
        }
    }
}
