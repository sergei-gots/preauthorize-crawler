package org.gots.apputil;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by goc on 30/10/2024
 */
public interface CommandLineArguments {
    @NotNull
    String getArgumentValue(@NotNull String argumentName);

    /** @return true if the argumentName is in use,
     * false - if there is no such a argumentName and therefore an argumentValue cannot be put into command line argument values set **/
    boolean setArgumentValue(@NotNull String argumentName, @NotNull String argumentValue);

    @NotNull
    Stream<Map.Entry<String,String>> getArguments();
}
