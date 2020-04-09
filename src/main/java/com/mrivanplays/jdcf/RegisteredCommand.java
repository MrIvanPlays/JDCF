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
    private final boolean guildOnly;

    public RegisteredCommand(Command command, String usage, String description, String[] aliases, boolean guildOnly) {
        this.command = command;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
        this.guildOnly = guildOnly;
    }

    /**
     * Returns the "potential" name of the command.
     *
     * @return first alias specified
     */
    @NotNull
    public String getName() {
        return aliases[0];
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
     * Returns the aliases (names) of the command (if specified)
     *
     * @return aliases
     */
    @NotNull
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Returns the data of the command, stored in this object, as a new {@link CommandData} object.
     *
     * @return command data
     */
    @NotNull
    public CommandData getDataAsCommandData() {
        return new CommandData(usage, description, aliases, guildOnly);
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
        return guildOnly;
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
