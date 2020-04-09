package com.mrivanplays.jdcf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents command data. Stores data about a specific command.
 */
public final class CommandData {

    private String usage;
    private String description;
    private String[] names;
    private boolean guildOnly;

    public CommandData(@Nullable String usage, @Nullable String description, @NotNull String[] names, boolean guildOnly) {
        this.usage = usage;
        this.description = description;
        this.names = names;
        this.guildOnly = guildOnly;
    }

    /**
     * Returns the usage of the {@link Command} this command data is holding, if present.
     *
     * @return usage
     */
    @Nullable
    public String getUsage() {
        return usage;
    }

    /**
     * Returns the description of the {@link Command} this command data is holding, if present.
     *
     * @return description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name(s) of the {@link Command} this command data is holding.
     *
     * @return name(s)
     */
    @NotNull
    public String[] getNames() {
        return names;
    }

    /**
     * Returns whenever the {@link Command} this command data is holding is guild only.
     *
     * @return <code>true</code> if guild only, <code>false</code> otherwise
     */
    public boolean isGuildOnly() {
        return guildOnly;
    }
}
