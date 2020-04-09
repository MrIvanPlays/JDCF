package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// work in progress
public class ArgumentHolder {

    private List<String> args;
    private final CommandExecutionContext commandContext;
    private final ArgumentParseGlobalFailHandler globalFailHandler;
    private int argumentsTookCount = 0;

    public ArgumentHolder(CommandExecutionContext commandContext, String[] args) {
        this.args = new ArrayList<>(Arrays.asList(args));
        this.commandContext = commandContext;
        this.globalFailHandler = commandContext.getCommandManagerCreator().getSettings().getGlobalFailHandler();
    }

    @NotNull
    public <T, P> Argument<T, P> nextArgument(@NotNull ArgumentResolver<T, P> argumentResolver) {
        Objects.requireNonNull(argumentResolver, "argumentResolver");
        argumentsTookCount++;
        try {
            String argument = args.remove(0);
            return new Argument<>(
                    commandContext,
                    argumentResolver,
                    argument,
                    globalFailHandler,
                    argumentsTookCount - 1
            );
        } catch (IndexOutOfBoundsException e) { // I hate lists and java's exceptions
            return new Argument<>(
                    commandContext,
                    argumentResolver,
                    null,
                    globalFailHandler,
                    argumentsTookCount - 1
            );
        }
    }
}
