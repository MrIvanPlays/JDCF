package com.mrivanplays.jdcf.settings.prefix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link com.mrivanplays.jdcf.Command} prefix handler.
 */
public interface PrefixHandler {

    /**
     * Returns the default command prefix of the bot.
     *
     * @return default prefix
     */
    @NotNull
    String getDefaultPrefix();

    /**
     * Sets a new default prefix of the bot.
     *
     * @param defaultPrefix prefix
     */
    void setDefaultPrefix(@NotNull String defaultPrefix);

    /**
     * Returns the prefix, which belongs to the specified guild id.
     *
     * @param guildId the guild id
     * @return guild prefix
     */
    @Nullable
    String getGuildPrefix(long guildId);

    /**
     * Sets a new guild prefix.
     *
     * @param prefix  the prefix you want to set
     * @param guildId the guild id for which you want to set the prefix
     */
    void setGuildPrefix(@NotNull String prefix, long guildId);

    /**
     * Saves the prefixes to a file
     */
    void savePrefixes();

    /**
     * Returns a usable command prefix.
     *
     * @param guildId the guild of which we want to get the prefix
     * @return guild prefix if found, else the default one
     */
    @NotNull
    default String getPrefix(long guildId) {
        String guildPrefix = getGuildPrefix(guildId);
        return guildPrefix == null ? getDefaultPrefix() : guildPrefix;
    }
}
