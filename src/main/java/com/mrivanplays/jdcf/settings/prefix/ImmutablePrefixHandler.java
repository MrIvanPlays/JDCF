package com.mrivanplays.jdcf.settings.prefix;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a prefix handler with immutable prefix.
 */
public final class ImmutablePrefixHandler implements PrefixHandler {

    private final String prefix;

    public ImmutablePrefixHandler(@NotNull String prefix) {
        Objects.requireNonNull(prefix, "prefix");
        this.prefix = prefix;
    }

    @Override
    public @NotNull String getDefaultPrefix() {
        return prefix;
    }

    @Override
    public void setDefaultPrefix(@NotNull String defaultPrefix) {
    }

    @Override
    public @Nullable String getGuildPrefix(long guildId) {
        return prefix;
    }

    @Override
    public void setGuildPrefix(@NotNull String prefix, long guildId) {
    }

    @Override
    public void savePrefixes() {
    }
}
