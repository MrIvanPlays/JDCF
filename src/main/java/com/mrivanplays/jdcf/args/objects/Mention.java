package com.mrivanplays.jdcf.args.objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Mention {

    public static final Pattern USER_MENTION_PATTERN = Pattern.compile("<@!?(\\d{17,20})>");
    public static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#?(\\d{17,20})>");
    public static final Pattern ROLE_MENTION_PATTERN = Pattern.compile("<@&?(\\d{17,20})>");

    @Nullable
    public static Mention parse(@NotNull String argument, @NotNull JDA jda, @Nullable Guild guild) {
        Objects.requireNonNull(argument, "argument");
        Objects.requireNonNull(jda, "jda");

        Matcher userMatcher = USER_MENTION_PATTERN.matcher(argument);
        if (userMatcher.matches()) {
            return new Mention(Long.parseLong(userMatcher.group(1)), MentionType.USER, jda, guild);
        }

        Matcher channelMatcher = CHANNEL_MENTION_PATTERN.matcher(argument);
        if (channelMatcher.matches()) {
            return new Mention(Long.parseLong(channelMatcher.group(1)), MentionType.CHANNEL, jda, guild);
        }

        Matcher roleMatcher = ROLE_MENTION_PATTERN.matcher(argument);
        if (roleMatcher.matches()) {
            return new Mention(Long.parseLong(roleMatcher.group(1)), MentionType.ROLE, jda, guild);
        }

        return null;
    }

    private final long snowflake;
    private final MentionType mentionType;
    private final JDA jda;
    private final Guild guild;

    private Mention(long snowflake, @NotNull MentionType mentionType, @NotNull JDA jda, @Nullable Guild guild) {
        this.snowflake = snowflake;
        this.mentionType = mentionType;
        this.jda = jda;
        this.guild = guild;
    }

    @NotNull
    public MentionType getMentionType() {
        return mentionType;
    }

    public boolean isUserMention() {
        return mentionType == MentionType.USER;
    }

    @Nullable
    public User toUser() {
        return isUserMention() ? jda.getUserById(snowflake) : null;
    }

    public boolean isChannelMention() {
        return mentionType == MentionType.CHANNEL;
    }

    @Nullable
    public TextChannel toChannel() {
        return isChannelMention() && guild != null ? guild.getTextChannelById(snowflake) : null;
    }

    public boolean isRoleMention() {
        return mentionType == MentionType.ROLE;
    }

    @Nullable
    public Role toRole() {
        return isRoleMention() && guild != null ? guild.getRoleById(snowflake) : null;
    }
}
