package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.ArgumentHolder;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a command. Extending classes may use the annotations located in {@link com.mrivanplays.jdcf.data} to give
 * more information about the command itself (aliases, description, usage)
 */
public interface Command {

    /**
     * An overridable method which checks if the member has the required permission to execute the command.
     *
     * @param context permission check context
     * @return <code>true</code> if has, <code>false</code> otherwise
     */
    default boolean hasPermission(@NotNull PermissionCheckContext context) {
        return true;
    }

    /**
     * JDCF calls this method when the command was triggered from a message. This will always happen when the command
     * name was prefixed with the bot's prefix. According to settings in {@link CommandManager} it also might happen
     * when the bot was mentioned except of the bot prefix. Also according to settings in {@link CommandManager}
     * prefixes may vary between servers.
     *
     * @param context data about the trigger
     * @param args    the arguments typed when triggered
     * @return command execution success state
     */
    boolean execute(@NotNull CommandExecutionContext context, @NotNull ArgumentHolder args);
}
