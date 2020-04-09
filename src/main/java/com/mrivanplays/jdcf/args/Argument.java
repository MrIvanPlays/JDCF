package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public final class Argument<T, P> {

    private final CommandExecutionContext commandContext;
    private final ArgumentResolver<T, P> argumentResolver;
    private final String rawArgument;
    private Consumer<T> onSuccess;
    private Consumer<ArgumentParseFailContext<P>> onFail;
    private final ArgumentParseGlobalFailHandler globalFailHandler;
    private final int argumentPosition;

    public Argument(@NotNull CommandExecutionContext commandContext,
                    @NotNull ArgumentResolver<T, P> resolver,
                    @Nullable String rawArgument,
                    @Nullable ArgumentParseGlobalFailHandler globalFailHandler,
                    int argumentPosition) {
        this.commandContext = commandContext;
        this.argumentResolver = resolver;
        this.rawArgument = rawArgument;
        this.globalFailHandler = globalFailHandler;
        this.argumentPosition = argumentPosition;
    }

    public Argument<T, P> ifPresent(@NotNull Consumer<T> onSuccess) {
        this.onSuccess = Objects.requireNonNull(onSuccess, "onSuccess");
        return this;
    }

    public Argument<T, P> ifNotPresent(@Nullable Consumer<ArgumentParseFailContext<P>> onFail) {
        this.onFail = onFail;
        return this;
    }

    public ArgumentExecutionResult execute() {
        Objects.requireNonNull(onSuccess, "onSuccess");
        String argument = rawArgument == null ? "" : rawArgument;
        ArgumentResolverContext resolverContext = new ArgumentResolverContext(
                argument,
                commandContext.getGuild(),
                commandContext.getJda()
        );
        ArgumentParsingState<P> parsingState = argumentResolver.tryParse(resolverContext);
        if (parsingState.isSuccess()) {
            onSuccess.accept(argumentResolver.parseNoTests(resolverContext));
        } else {
            ArgumentParseFailContext<P> failContext = new ArgumentParseFailContext<>(
                    parsingState, rawArgument, commandContext.getCommandData(), argumentPosition
            );
            if (onFail != null) {
                onFail.accept(failContext);
            } else {
                if (globalFailHandler != null) {
                    globalFailHandler.handleGlobalFail(commandContext, parsingState, rawArgument, argumentPosition);
                }
            }
        }
        return new ArgumentExecutionResult(parsingState, rawArgument);
    }
}
