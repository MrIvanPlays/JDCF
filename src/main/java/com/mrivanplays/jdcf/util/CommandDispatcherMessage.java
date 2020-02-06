package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.entities.AbstractMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CommandDispatcherMessage extends AbstractMessage {

    private JDA jda;
    private Guild guild;
    private TextChannel channel;
    private Member member;
    private ScheduledExecutorService executor;

    public CommandDispatcherMessage(String content, JDA jda, Guild guild, TextChannel channel, Member member, ScheduledExecutorService executor) {
        super(content, "0", false);
        this.jda = jda;
        this.guild = guild;
        this.channel = channel;
        this.member = member;
        this.executor = executor;
    }

    @Override
    protected void unsupported() {
        throw new UnsupportedOperationException("Command was executed thru CommandManager#dispatchCommand meaning that this is not supported.");
    }

    @Nonnull
    @Override
    public MessageChannel getChannel() {
        return channel;
    }

    @Override
    public boolean isFromGuild() {
        return true;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return jda;
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return guild;
    }

    @Nonnull
    @Override
    public User getAuthor() {
        return jda.getSelfUser();
    }

    @Nonnull
    @Override
    public Member getMember() {
        return member;
    }

    @Nonnull
    @Override
    public List<Member> getMentionedMembers() {
        return Collections.emptyList();
    }

    @Override
    public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull MentionType... types) {
        return false;
    }

    @Nonnull
    @Override
    public List<Member> getMentionedMembers(@Nonnull Guild guild) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<User> getMentionedUsers() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<TextChannel> getMentionedChannels() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<Role> getMentionedRoles() {
        return Collections.emptyList();
    }

    @Override
    public boolean mentionsEveryone() {
        return false;
    }

    @Override
    public boolean isEdited() {
        return false;
    }

    @Override
    public boolean isWebhookMessage() {
        return false;
    }

    @Nonnull
    @Override
    public TextChannel getTextChannel() {
        return channel;
    }

    @Nonnull
    @Override
    public AuditableRestAction<Void> delete() {
        return new CommandDispatcherARS<>(jda, executor);
    }

    @Nullable
    @Override
    public MessageActivity getActivity() {
        return null;
    }

    @Override
    public long getIdLong() {
        unsupported();
        return 0;
    }

    @Nonnull
    @Override
    public MessageType getType() {
        return MessageType.DEFAULT;
    }
}
