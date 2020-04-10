package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentExecutionResult<T, D> {

    private final ArgumentResolverResult<T, D> resolverResult;
    private final String rawArgument;

    public ArgumentExecutionResult(@NotNull ArgumentResolverResult<T, D> resolverResult, @Nullable String rawArgument) {
        this.resolverResult = resolverResult;
        this.rawArgument = rawArgument;
    }

    @NotNull
    public ArgumentResolverResult<T, D> getResolverResult() {
        return resolverResult;
    }

    public boolean isSuccessful() {
        return resolverResult.getValue().isPresent();
    }

    @Nullable
    public String getArgument() {
        return rawArgument;
    }
}
