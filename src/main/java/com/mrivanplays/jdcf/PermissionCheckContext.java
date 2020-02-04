package com.mrivanplays.jdcf;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a context for checking if a member/user has permission to execute the specified comamnd.
 */
public final class PermissionCheckContext {

    private JDA jda;
    private User user;
    private Guild guild;
    private Member member;
    private String commandAlias;

    public PermissionCheckContext(@NotNull JDA jda, @NotNull User user, @Nullable Guild guild, @Nullable Member member, @NotNull String commandAlias) {
        this.jda = jda;
        this.user = user;
        this.guild = guild;
        this.member = member;
        this.commandAlias = commandAlias;
    }

    /**
     * Returns the {@link JDA} instance, where the command was triggered.
     *
     * @return jda
     */
    @NotNull
    public JDA getJda() {
        return jda;
    }

    /**
     * Returns the {@link User} instance, which executed the command.
     *
     * @return command execution triggered message's author
     */
    @NotNull
    public User getUser() {
        return user;
    }

    /**
     * Returns the {@link Guild} instance if the command was executed in a guild.
     *
     * @return guild or null
     */
    @Nullable
    public Guild getGuild() {
        return guild;
    }

    /**
     * Returns whenever the command was triggered to be executed in a guild.
     *
     * @return <code>true</code> if from guild, <code>false</code> otherwise
     */
    public boolean isFromGuild() {
        return getGuild() != null;
    }

    @Nullable
    public Member getMember() {
        return member;
    }

    /**
     * Returns the command alias, from which the command was triggered.
     *
     * @return alias
     */
    @NotNull
    public String getCommandAlias() {
        return commandAlias;
    }
}
