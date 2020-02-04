package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a registered command. A registered command is a command that was registered into a command manager. In
 * order to have easy access to all the data for the command, this class takes in place, storing all the things possible
 * for a command.
 */
public final class RegisteredCommand {

    private final Command command;
    private final String usage;
    private final String description;
    private final String[] aliases;

    public RegisteredCommand(Command command, String usage, String description, String[] aliases) {
        this.command = command;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
    }

    /**
     * Returns the name of the command.
     *
     * @return name
     */
    @NotNull
    public String getName() {
        return command.getName();
    }

    /**
     * Returns all the permissions of the command (if specified)
     *
     * @return permissions
     */
    @Nullable
    public Permission[] getPermissions() {
        return command.getPermissions();
    }

    /**
     * Returns the usage of the command (if specified)
     *
     * @return usage
     */
    @Nullable
    public String getUsage() {
        return usage;
    }

    /**
     * Returns the description of the command (if specified)
     *
     * @return description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Returns the aliases of the command (if specified)
     *
     * @return aliases
     */
    @Nullable
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Checks if the specified member has the required permission to execute this command.
     *
     * @param context permission check context
     * @return <code>true</code> if has permission, <code>false</code> otherwise
     */
    public boolean hasPermission(@NotNull PermissionCheckContext context) {
        return command.hasPermission(context);
    }

    /**
     * Returns whenever this command should be only executed in guild.
     *
     * @return <code>true</code> if guild only, <code>false</code> otherwise
     */
    public boolean isGuildOnly() {
        return command.isGuildOnly();
    }

    /**
     * Executes the command with the specified context and arguments.
     *
     * @param context context
     * @param args    arguments
     * @return command execution success
     */
    public boolean execute(CommandExecutionContext context, CommandArguments args) {
        return command.execute(context, args);
    }
}
