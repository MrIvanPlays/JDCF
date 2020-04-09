package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;

import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Represents a command. Extending classes may use the annotations located in {@link com.mrivanplays.jdcf.data} to give
 * more information about the command itself (aliases, description, usage)
 */
// todo: should we transform this to interface in v1.1.0
public abstract class Command {

    /**
     * An overridable method which checks if the member has the required permission to execute the command.
     *
     * @param context permission check context
     * @return <code>true</code> if has, <code>false</code> otherwise
     */
    public boolean hasPermission(@NotNull PermissionCheckContext context) {
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
    public abstract boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args);
}
