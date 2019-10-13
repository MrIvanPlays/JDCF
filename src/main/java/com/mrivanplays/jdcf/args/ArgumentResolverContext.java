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
package com.mrivanplays.jdcf.args;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a data about a specific argument
 */
public final class ArgumentResolverContext
{

    private final String argument;
    private final Guild guild;
    private final JDA jda;

    public ArgumentResolverContext(@NotNull String argument, @NotNull Guild guild, @NotNull JDA jda)
    {
        this.argument = argument;
        this.guild = guild;
        this.jda = jda;
    }

    /**
     * Returns the specific argument.
     *
     * @return argument
     */
    @NotNull
    public String getArgument()
    {
        return argument;
    }

    /**
     * Returns the {@link Guild} where the argument was send.
     *
     * @return guild
     */
    @NotNull
    public Guild getGuild()
    {
        return guild;
    }

    /**
     * Returns the shard.
     *
     * @return shard
     */
    @NotNull
    public JDA getJda()
    {
        return jda;
    }
}
