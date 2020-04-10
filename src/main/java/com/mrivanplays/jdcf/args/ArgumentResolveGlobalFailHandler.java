package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ArgumentResolveGlobalFailHandler {

    void handleGlobalFail(@NotNull CommandExecutionContext commandContext, @NotNull ArgumentResolveFailContext<?> failContext);
}
