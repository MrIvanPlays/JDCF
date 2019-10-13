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
 * Represents a command. Extending classes may use the annotations located
 * in {@link com.mrivanplays.jdcf.data} to give more information about the
 * command itself (aliases, description, usage)
 */
public abstract class Command
{

    private final String name;
    private final Permission[] permissions;

    public Command(@NotNull String name)
    {
        this(name, (Permission[]) null);
    }

    public Command(@NotNull String name, @Nullable Permission... permissions)
    {
        this.name = name;
        this.permissions = permissions;
    }

    /**
     * Returns the name of the command.
     *
     * @return name
     */
    @NotNull
    public String getName()
    {
        return name;
    }

    /**
     * Returns the {@link Permission}s required to the author
     * of the command for the command to be executed.
     *
     * @return permission(s)
     */
    @Nullable
    public Permission[] getPermissions()
    {
        return permissions;
    }

    /**
     * JDCF calls this method when the command was triggered from a message.
     * This will always happen when the command name was prefixed with the
     * bot's prefix. According to settings in {@link CommandManager} it also
     * might happen when the bot was mentioned except of the bot prefix.
     * Also according to settings in {@link CommandManager} prefixes may vary
     * between servers.
     *
     * @param context data about the trigger
     * @param args    the arguments typed when triggered
     */
    public abstract void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args);
}
