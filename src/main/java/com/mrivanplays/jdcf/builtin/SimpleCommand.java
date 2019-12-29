package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;

import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a normal command which gives you a {@link String[]} for arguments except the traditional argument handler
 * for JDCF.
 */
public abstract class SimpleCommand extends Command {

    public SimpleCommand(@NotNull String name) {
        super(name);
    }

    public SimpleCommand(@NotNull String name, @Nullable Permission... permissions) {
        super(name, permissions);
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        return execute(context, args.getArgsLeft());
    }

    /**
     * Command execution logic, called when the specified command was executed
     *
     * @param context the command context
     * @param args arguments
     * @return execution success state
     */
    public abstract boolean execute(@NotNull CommandExecutionContext context, @NotNull String[] args);
}
