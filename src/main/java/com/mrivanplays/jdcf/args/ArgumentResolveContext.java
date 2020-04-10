package com.mrivanplays.jdcf.args;

import com.mrivanplays.jdcf.MayBeEmpty;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a data about a specific argument
 */
public final class ArgumentResolveContext {

    private final String argument;
    private final Guild guild;
    private final JDA jda;
    private final int argumentPosition;

    public ArgumentResolveContext(String argument, @NotNull JDA jda, @Nullable Guild guild, int argumentPosition) {
        this.argument = argument;
        this.guild = guild;
        this.jda = jda;
        this.argumentPosition = argumentPosition;
    }

    /**
     * Returns the specific argument. Depending if the argument is present or not, this may be empty.
     *
     * @return argument
     */
    @MayBeEmpty
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

    /**
     * Returns the argument position in the current {@link com.mrivanplays.jdcf.Command} argument hierarchy.
     *
     * @return argument position
     */
    public int getArgumentPosition() {
        return argumentPosition;
    }
}
