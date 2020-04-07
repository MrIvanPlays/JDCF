package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.CommandExecutionContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link FailReason} handler, which will be applied on every command argument, if so no handling was
 * applied.
 */
public interface FailReasonHandler {

    /**
     * Method which will be called to every argument fail reason.
     *
     * @param context command context
     * @param failReason fail reason
     * @param argument raw string argument, if present
     */
    void handleFailReason(@NotNull CommandExecutionContext context, @NotNull FailReason failReason, @Nullable String argument);
}
