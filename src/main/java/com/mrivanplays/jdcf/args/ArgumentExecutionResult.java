package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentExecutionResult {

    private final ArgumentParsingState<?> argumentParsingState;
    private final String rawArgument;

    public ArgumentExecutionResult(@NotNull ArgumentParsingState<?> parsingState, @Nullable String rawArgument) {
        this.argumentParsingState = parsingState;
        this.rawArgument = rawArgument;
    }

    @NotNull
    public ArgumentParsingState<?> getParsingState() {
        return argumentParsingState;
    }

    public boolean isSuccessful() {
        return argumentParsingState.isSuccess();
    }

    @Nullable
    public String getArgument() {
        return rawArgument;
    }
}
