package com.mrivanplays.jdcf.args;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a data about a specific argument
 */
public final class ArgumentResolverContext {

    private final String argument;
    private final Guild guild;
    private final JDA jda;

    public ArgumentResolverContext(String argument, @NotNull JDA jda) {
        this(argument, null, jda);
    }

    public ArgumentResolverContext(String argument, @Nullable Guild guild, @NotNull JDA jda) {
        this.argument = argument;
        this.guild = guild;
        this.jda = jda;
    }

    /**
     * Returns the specific argument. Depending if the argument is present or not, this may be empty.
     *
     * @return argument
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Returns the {@link Guild} where the argument was send if it was sent into a guild text channel.
     *
     * @return guild
     */
    @Nullable
    public Guild getGuild() {
        return guild;
    }

    /**
     * Returns the shard.
     *
     * @return shard
     */
    @NotNull
    public JDA getJda() {
        return jda;
    }
}
