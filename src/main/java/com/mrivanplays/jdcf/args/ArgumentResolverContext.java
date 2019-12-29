package com.mrivanplays.jdcf.args;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a data about a specific argument
 */
public final class ArgumentResolverContext {

    private final String argument;
    private final Guild guild;
    private final JDA jda;

    public ArgumentResolverContext(@NotNull String argument, @NotNull Guild guild, @NotNull JDA jda) {
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
    public String getArgument() {
        return argument;
    }

    /**
     * Returns the {@link Guild} where the argument was send.
     *
     * @return guild
     */
    @NotNull
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
