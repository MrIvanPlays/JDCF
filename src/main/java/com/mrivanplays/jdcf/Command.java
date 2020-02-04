package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Represents a command. Extending classes may use the annotations located in {@link com.mrivanplays.jdcf.data} to give
 * more information about the command itself (aliases, description, usage)
 */
public abstract class Command {

    private final String name;
    private final Permission[] permissions;
    private final boolean guildOnly;

    public Command(@NotNull String name) {
        this(name, true, (Permission[]) null);
    }

    public Command(@NotNull String name, boolean guildOnly) {
        this(name, guildOnly, (Permission[]) null);
    }

    public Command(@NotNull String name, @Nullable Permission... permissions) {
        this(name, true, permissions);
    }

    public Command(@NotNull String name, boolean guildOnly, @Nullable Permission... permissions) {
        this.name = Objects.requireNonNull(name, "name");
        this.guildOnly = guildOnly;
        this.permissions = permissions;
    }

    /**
     * Creates a new command builder.
     *
     * @return builder
     */
    public static Command.Builder builder() {
        return new Command.Builder();
    }

    /**
     * Returns the name of the command.
     *
     * @return name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Permission}s required to the author of the command for the command to be executed.
     *
     * @return permission(s)
     */
    @Nullable
    public Permission[] getPermissions() {
        return permissions;
    }

    /**
     * An overridable method which checks if the member has the required permission to execute the command.
     *
     * @param context permission check context
     * @return <code>true</code> if has, <code>false</code> otherwise
     */
    public boolean hasPermission(@NotNull PermissionCheckContext context) {
        if (guildOnly) {
            if (context.isFromGuild()) {
                return permissions == null || context.getMember().hasPermission(permissions);
            } else {
                return false;
            }
        } else {
            if (context.isFromGuild()) {
                return permissions == null || context.getMember().hasPermission(permissions);
            } else {
                for (Guild guild : context.getUser().getMutualGuilds()) {
                    if (guild.getMembers().stream().anyMatch(u -> u.getId().equalsIgnoreCase(context.getJda().getSelfUser().getId()))) {
                        Member member = guild.getMember(context.getUser());
                        return permissions == null || member.hasPermission(permissions);
                    }
                }
            }
        }
        return permissions == null;
    }

    /**
     * Returns whenever this command should be executed only in guilds.
     *
     * @return <code>true</code> if guild only, <code>false</code> otherwise
     */
    public boolean isGuildOnly() {
        return guildOnly;
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

    /**
     * Represents a "all-in-one" command builder. It removes the usage of annotations and other classes except that one
     * and the {@link CommandManager}
     */
    public static final class Builder {

        private BiFunction<CommandExecutionContext, CommandArguments, Boolean> executor;
        private String name;
        private String usage;
        private String description;
        private String[] aliases;
        private Permission[] permissions;
        private boolean guildOnly = true;

        /**
         * Sets the name of the command. Shouldn't be null.
         *
         * @param name name
         * @return this instance for chaining
         */
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the usage of the command. Can be null
         *
         * @param usage usage
         * @return this instance for chaining
         */
        public Builder usage(@Nullable String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Sets the description of the command. Can be null
         *
         * @param description description
         * @return this instance for chaining
         */
        public Builder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the aliases of the command. Can be null
         *
         * @param aliases aliases
         * @return this instance for chaining
         */
        public Builder aliases(@Nullable String... aliases) {
            this.aliases = aliases;
            return this;
        }

        /**
         * Sets the permissions, required to execute the command. Can be null
         *
         * @param permissions permissions
         * @return this instance for chaining
         */
        public Builder permissions(@Nullable Permission... permissions) {
            this.permissions = permissions;
            return this;
        }

        /**
         * Sets if the command should be guild only or not
         *
         * @param guildOnly value
         * @return this instance for chaining
         */
        public Builder guildOnly(boolean guildOnly) {
            this.guildOnly = guildOnly;
            return this;
        }

        /**
         * Sets the command executor. Shouldn't be null
         *
         * @param executor executor
         * @return this instance for chaining
         */
        public Builder executor(@NotNull BiFunction<CommandExecutionContext, CommandArguments, Boolean> executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Builds the specified parameters into a {@link RegisteredCommand} object which then is used to register that
         * command into the {@link CommandManager}
         *
         * @return command
         */
        public RegisteredCommand build() {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(executor, "executor");
            return new RegisteredCommand(new Command(name, guildOnly, permissions) {

                @Override
                public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
                    return executor.apply(context, args);
                }
            }, usage, description, aliases);
        }

        /**
         * Builds the specified parameters into a {@link RegisteredCommand} object and then registers it into the
         * specified {@link CommandManager}
         *
         * @param commandManager the command manager where to register that command, shouldn't be null
         */
        public void buildAndRegister(@NotNull CommandManager commandManager) {
            Objects.requireNonNull(commandManager, "commandManager");
            RegisteredCommand command;
            try {
                command = build();
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("An error has occurred trying to build the command.", e);
            }
            commandManager.registerCommand(command);
        }
    }
}
