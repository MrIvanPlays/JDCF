/*
    Copyright (c) 2019 Ivan Pekov
    Copyright (c) 2019 Contributors

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;

import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents a command. Extending classes may use the annotations located in {@link com.mrivanplays.jdcf.data} to give
 * more information about the command itself (aliases, description, usage)
 */
public abstract class Command {

    private final String name;
    private final Permission[] permissions;

    public Command(@NotNull String name) {
        this(name, (Permission[]) null);
    }

    public Command(@NotNull String name, @Nullable Permission... permissions) {
        this.name = Objects.requireNonNull(name, "name");
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
     * JDCF calls this method when the command was triggered from a message. This will always happen when the command
     * name was prefixed with the bot's prefix. According to settings in {@link CommandManager} it also might happen
     * when the bot was mentioned except of the bot prefix. Also according to settings in {@link CommandManager}
     * prefixes may vary between servers.
     *
     * @param context data about the trigger
     * @param args    the arguments typed when triggered
     */
    public abstract void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args);

    /**
     * Represents a "all-in-one" command builder. It removes the usage of annotations and other classes except that one
     * and the {@link CommandManager}
     */
    public static final class Builder {

        private BiConsumer<CommandExecutionContext, CommandArguments> executor;
        private String name;
        private String usage;
        private String description;
        private String[] aliases;
        private Permission[] permissions;

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
         * Sets the command executor. Shouldn't be null
         *
         * @param executor executor
         * @return this instance for chaining
         */
        public Builder executor(@NotNull BiConsumer<CommandExecutionContext, CommandArguments> executor) {
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
            return new RegisteredCommand(new Command(name, permissions) {

                @Override
                public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
                    executor.accept(context, args);
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
