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
package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageActivity;
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
    private ScheduledExecutorService executor;

    public CommandDispatcherMessage(String content, JDA jda, Guild guild, TextChannel channel, ScheduledExecutorService executor) {
        super(content, "0", false);
        this.jda = jda;
        this.guild = guild;
        this.channel = channel;
        this.executor = executor;
    }

    @Override
    protected void unsupported() {
        throw new UnsupportedOperationException("Command was executed thru CommandManager#dispatchCommand meaning that this is not supported.");
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
        return guild.getSelfMember();
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
        unsupported();
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
