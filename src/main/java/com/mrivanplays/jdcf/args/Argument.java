package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.CheckReturnValue;

public final class Argument<T, D> {

    private final CommandExecutionContext commandContext;
    private final ArgumentResolver<T, D> argumentResolver;
    private final String rawArgument;
    private Consumer<T> onSuccess;
    private Consumer<ArgumentResolveFailContext<D>> onFail;
    private final ArgumentResolveGlobalFailHandler globalFailHandler;
    private final int argumentPosition;

    public Argument(@NotNull CommandExecutionContext commandContext,
                    @NotNull ArgumentResolver<T, D> resolver,
                    @Nullable String rawArgument,
                    @Nullable ArgumentResolveGlobalFailHandler globalFailHandler,
                    int argumentPosition) {
        this.commandContext = commandContext;
        this.argumentResolver = resolver;
        this.rawArgument = rawArgument;
        this.globalFailHandler = globalFailHandler;
        this.argumentPosition = argumentPosition;
    }

    @CheckReturnValue
    public Argument<T, D> ifPresent(@NotNull Consumer<T> onSuccess) {
        this.onSuccess = Objects.requireNonNull(onSuccess, "onSuccess");
        return this;
    }

    @CheckReturnValue
    public Argument<T, D> ifNotPresent(@Nullable Consumer<ArgumentResolveFailContext<D>> onFail) {
        this.onFail = onFail;
        return this;
    }

    public ArgumentExecutionResult<T, D> execute() {
        Objects.requireNonNull(onSuccess, "onSuccess");
        String argument = rawArgument == null ? "" : rawArgument;
        ArgumentResolveContext resolveContext = new ArgumentResolveContext(
                argument,
                commandContext.getJda(),
                commandContext.getGuild(),
                argumentPosition
        );
        ArgumentResolverResult<T, D> resolverResult = argumentResolver.parse(resolveContext);
        if (resolverResult.getValue().isPresent()) {
            onSuccess.accept(resolverResult.getValue().get());
        } else {
            Objects.requireNonNull(resolverResult.getFailReason(), "Parsed value is null but there's no failure reason.");
            ArgumentResolveFailContext<D> failContext = new ArgumentResolveFailContext<>(
                    resolverResult.getFailReason(), rawArgument, commandContext.getCommandData(), argumentPosition
            );
            if (onFail != null) {
                onFail.accept(failContext);
            } else {
                if (globalFailHandler != null) {
                    globalFailHandler.handleGlobalFail(commandContext, failContext);
                }
            }
        }
        return new ArgumentExecutionResult<>(resolverResult, rawArgument);
    }
}
