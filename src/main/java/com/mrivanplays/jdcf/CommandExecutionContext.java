package com.mrivanplays.jdcf;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a generic data about the {@link Command} executed.
 */
public final class CommandExecutionContext {

    private final Message message;
    private final String alias;
    private final boolean fromDispatcher;
    private final CommandData commandData;
    private final CommandManager creator;

    public CommandExecutionContext(@NotNull Message message, @NotNull String alias, boolean fromDispatcher,
                                   @NotNull CommandData commandData, @NotNull CommandManager creator) {
        this.message = message;
        this.alias = alias;
        this.fromDispatcher = fromDispatcher;
        this.commandData = commandData;
        this.creator = creator;
    }

    /**
     * Returns the {@link MessageChannel} where the command was executed.
     *
     * @return channel
     */
    @NotNull
    public MessageChannel getChannel() {
        return message.getChannel();
    }

    /**
     * Returns whenever the command was executed in a guild.
     *
     * @return <code>true</code> if executed in guild channel, <code>false</code> otherwise
     */
    public boolean wasExecutedInGuild() {
        return message.isFromGuild();
    }

    /**
     * Returns the {@link TextChannel} if the command was executed in a guild.
     *
     * @return channel or null
     */
    @Nullable
    public TextChannel getTextChannel() {
        return wasExecutedInGuild() ? message.getTextChannel() : null;
    }

    /**
     * Returns the {@link PrivateChannel} if the command was executed in the specified {@link User author}'s DMs.
     *
     * @return channel or null
     */
    @Nullable
    public PrivateChannel getPrivateChannel() {
        return !wasExecutedInGuild() ? message.getPrivateChannel() : null;
    }

    /**
     * Returns the guild {@link Member}, author of the command if the command was executed in guild.
     *
     * <p>Keep in mind this is not the same as {@link #getAuthor()}
     *
     * @return member
     */
    @Nullable
    public Member getMember() {
        return wasExecutedInGuild() ? message.getMember() : null;
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
     * Returns the {@link Guild} where the {@link #getMember()} have executed the command if it was executed in a guild.
     *
     * @return guild
     */
    @Nullable
    public Guild getGuild() {
        return wasExecutedInGuild() ? message.getGuild() : null;
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
     * Returns the data of the command execution triggered.
     *
     * @return command data
     */
    @NotNull
    public CommandData getCommandData() {
        return commandData;
    }

    /**
     * Returns the {@link CommandManager}, whom have initialized this command execution context.
     *
     * @return command manager initializer
     */
    @NotNull
    public CommandManager getCommandManagerCreator() {
        return creator;
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
