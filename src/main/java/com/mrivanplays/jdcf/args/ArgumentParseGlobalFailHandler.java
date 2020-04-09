package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArgumentParseGlobalFailHandler {

    void handleGlobalFail(@NotNull CommandExecutionContext commandContext,
                          @NotNull ArgumentParsingState<?> parsingState,
                          @Nullable String argument,
                          int argumentPosition);
}
