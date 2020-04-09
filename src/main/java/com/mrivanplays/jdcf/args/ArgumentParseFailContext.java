package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentParseFailContext<P> {

    private final ArgumentParsingState<P> parsingState;
    private final String rawArgument;
    private final CommandData baseCommandData;
    private final int argumentPosition;

    public ArgumentParseFailContext(@NotNull ArgumentParsingState<P> parsingState,
                                    @Nullable String rawArgument,
                                    @NotNull CommandData baseCommandData,
                                    int argumentPosition) {
        this.parsingState = parsingState;
        this.rawArgument = rawArgument;
        this.baseCommandData = baseCommandData;
        this.argumentPosition = argumentPosition;
    }

    @NotNull
    public ArgumentParsingState<P> getParsingState() {
        return parsingState;
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
