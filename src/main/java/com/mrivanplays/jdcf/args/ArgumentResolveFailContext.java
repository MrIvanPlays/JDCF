package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentResolveFailContext<D> {

    private final ArgumentResolveFailure<D> failureReason;
    private final String rawArgument;
    private final CommandData baseCommandData;
    private final int argumentPosition;

    public ArgumentResolveFailContext(@NotNull ArgumentResolveFailure<D> failureReason,
                                      @Nullable String rawArgument,
                                      @NotNull CommandData baseCommandData,
                                      int argumentPosition) {
        this.failureReason = failureReason;
        this.rawArgument = rawArgument;
        this.baseCommandData = baseCommandData;
        this.argumentPosition = argumentPosition;
    }

    @NotNull
    public ArgumentResolveFailure<D> getFailureReason() {
        return failureReason;
    }

    @Nullable
    public String getRawArgument() {
        return rawArgument;
    }

    @NotNull
    public CommandData getBaseCommandData() {
        return baseCommandData;
    }

    public int getArgumentPosition() {
        return argumentPosition;
    }
}
