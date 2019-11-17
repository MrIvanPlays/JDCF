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
