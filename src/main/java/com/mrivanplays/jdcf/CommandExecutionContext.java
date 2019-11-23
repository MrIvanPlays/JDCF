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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a generic data about the {@link Command} executed.
 */
public final class CommandExecutionContext {

    private final Message message;
    private final String alias;
    private final boolean fromDispatcher;

    public CommandExecutionContext(@NotNull Message message, @NotNull String alias, boolean fromDispatcher) {
        this.message = message;
        this.alias = alias;
        this.fromDispatcher = fromDispatcher;
    }

    /**
     * Returns the {@link TextChannel} where the command was executed.
     *
     * @return channel
     */
    @NotNull
    public TextChannel getChannel() {
        return message.getTextChannel();
    }

    /**
     * Returns the guild {@link Member}, author of the command.
     *
     * <p>Keep in mind this is not the same as {@link #getAuthor()}
     *
     * @return member
     */
    @NotNull
    public Member getMember() {
        return message.getMember();
    }

    /**
     * Returns the discord {@link User} as a whole, author of the command.
     *
     * <p>Keep in mind this is not the same as {@link #getMember()}
     *
     * @return discord user, author
     */
    @NotNull
    public User getAuthor() {
        return message.getAuthor();
    }

    /**
     * Returns the shard from where the command was executed.
     *
     * @return shard
     */
    @NotNull
    public JDA getJda() {
        return message.getJDA();
    }

    /**
     * Returns the whole command {@link Message}. It is discouraged to be used for retrieving the text.
     *
     * @return message
     */
    @NotNull
    public Message getMessage() {
        return message;
    }

    /**
     * Returns the {@link Guild} where the {@link #getMember()} have executed the command.
     *
     * @return guild
     */
    @NotNull
    public Guild getGuild() {
        return message.getGuild();
    }

    /**
     * Returns the alias which triggered command execution.
     *
     * @return alias
     */
    @NotNull
    public String getAlias() {
        return alias;
    }

    /**
     * Returns whenever this context was made from a dispatcher
     *
     * @return <code>true</code> if the command wasn't dispatched the normal way, <code>false</code> otherwise
     */
    public boolean isFromDispatcher() {
        return fromDispatcher;
    }
}
