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

/**
 * Represents a registered command. A registered command is a command that was
 * registered into a command manager. In order to have easy access to all the
 * data for the command, this class takes in place, storing all the things
 * possible for a command.
 */
public final class RegisteredCommand
{

    private final Command command;
    private final String usage;
    private final String description;
    private final String[] aliases;

    public RegisteredCommand(Command command, String usage, String description, String[] aliases)
    {
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
    public String getName()
    {
        return command.getName();
    }

    /**
     * Returns all the permissions of the command (if specified)
     *
     * @return permissions
     */
    @Nullable
    public Permission[] getPermissions()
    {
        return command.getPermissions();
    }

    /**
     * Returns the usage of the command (if specified)
     *
     * @return usage
     */
    @Nullable
    public String getUsage()
    {
        return usage;
    }

    /**
     * Returns the description of the command (if specified)
     *
     * @return description
     */
    @Nullable
    public String getDescription()
    {
        return description;
    }

    /**
     * Returns the aliases of the command (if specified)
     *
     * @return aliases
     */
    @Nullable
    public String[] getAliases()
    {
        return aliases;
    }

    /**
     * Executes the command with the specified context and arguments.
     *
     * @param context context
     * @param args    arguments
     */
    public void execute(CommandExecutionContext context, CommandArguments args)
    {
        command.execute(context, args);
    }
}
