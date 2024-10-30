package org.gots.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import org.gots.apputil.CommandLineArguments;

@Getter
public class ApiPreauthorizeCrawlerCommandLineArguments implements CommandLineArguments {

    public static final String PROJECT_DIR_ARGUMENT_KEY = "project-dir";
    public static final String REST_CONTROLLER_MASK_KEY = "rest-controller-mask";
    public static final String OUTPUT_DIR_ARGUMENT_KEY = "output-dir";
    public static final String SWAGGER_HOST_ARGUMENT_KEY = "swagger-host";

    private static final String PROJECT_DIR_DEFAULT_VALUE = ".";
    private static final String REST_CONTROLLER_MASK_DEFAULT_VALUE = "RestController";
    private static final String OUTPUT_DIR_DEFAULT_VALUE = "scratches";
    private static final String SWAGGER_HOST_DEFAULT_VALUE = "http://localhost:8080/webapp/swagger-ui/index.html?configUrl=/webapp/v3/api-docs/swagger-config#/";


    private final List<String> argumentNames = List.of(PROJECT_DIR_ARGUMENT_KEY, OUTPUT_DIR_ARGUMENT_KEY, SWAGGER_HOST_ARGUMENT_KEY);

    private final Map<String, String> arguments = new HashMap<>();
    {
        arguments.put(PROJECT_DIR_ARGUMENT_KEY, PROJECT_DIR_DEFAULT_VALUE);
        arguments.put(REST_CONTROLLER_MASK_KEY, REST_CONTROLLER_MASK_DEFAULT_VALUE);
        arguments.put(OUTPUT_DIR_ARGUMENT_KEY, OUTPUT_DIR_DEFAULT_VALUE);
        arguments.put(SWAGGER_HOST_ARGUMENT_KEY, SWAGGER_HOST_DEFAULT_VALUE);
    }

    @Override
    public @NotNull String getArgumentValue(@NotNull String key) {
        String argumentValue = arguments.get(key);
        if (argumentValue != null) {
            return argumentValue;
        }
        throw new IllegalArgumentException("Argument with the name '" + key + "' is not listed in the argument names list");
    }

    @Override
    public boolean setArgumentValue(@NotNull String key, @NotNull String value) {
        if (arguments.containsKey(key)) {
            arguments.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public Stream<Map.Entry<String, String>> getArguments() {
        return this.arguments.entrySet().stream();
    }
}