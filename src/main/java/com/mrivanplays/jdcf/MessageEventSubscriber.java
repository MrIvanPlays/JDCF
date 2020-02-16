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
 * Represents a object, holding data for message event.
 */
public final class MessageEventSubscriber {

    private Message message;

    public MessageEventSubscriber(Message message) {
        this.message = message;
    }

    /**
     * Returns whenever the {@link Message} was from {@link Guild}
     *
     * @return <code>true</code> if from guild, <code>false</code> otherwise
     */
    public boolean isGuildMessage() {
        return message.isFromGuild();
    }

    /**
     * Returns the {@link User}, author of the {@link Message}
     *
     * @return author
     */
    @NotNull
    public User getUser() {
        return message.getAuthor();
    }

    /**
     * Returns the {@link Guild} {@link Member} which have send the {@link Message}, if the {@link Message} was sent in
     * a {@link Guild}.
     *
     * @return member
     */
    @Nullable
    public Member getMember() {
        return message.isFromGuild() ? message.getMember() : null;
    }

    /**
     * Returns the {@link Guild} if the {@link Message} was sent in one.
     *
     * @return guild
     */
    @Nullable
    public Guild getGuild() {
        return message.isFromGuild() ? message.getGuild() : null;
    }

    /**
     * Returns the sent {@link Message}
     *
     * @return message
     */
    @NotNull
    public Message getMessage() {
        return message;
    }

    /**
     * Returns the {@link MessageChannel} where the {@link Message} was sent.
     *
     * @return channel
     */
    @NotNull
    public MessageChannel getChannel() {
        return message.getChannel();
    }

    /**
     * Returns the {@link TextChannel} where the {@link Message} was sent, if it was sent in a {@link Guild}
     *
     * @return text channel
     */
    @Nullable
    public TextChannel getTextChannel() {
        return message.isFromGuild() ? message.getTextChannel() : null;
    }

    /**
     * Returns the {@link PrivateChannel} where the {@link Message} was sent, if it as sent in one.
     *
     * @return private channel
     */
    @Nullable
    public PrivateChannel getPrivateChannel() {
        return !message.isFromGuild() ? message.getPrivateChannel() : null;
    }

    /**
     * Returns the {@link JDA} instance, from where the message event was triggered
     *
     * @return jda
     */
    @NotNull
    public JDA getJda() {
        return message.getJDA();
    }
}
