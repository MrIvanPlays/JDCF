package com.mrivanplays.jdcf;

import org.jetbrains.annotations.Nullable;

/**
 * Represents command data. Stores data about a specific command.
 */
public final class CommandData {

    private String usage;
    private String description;
    private String[] names;

    public CommandData(@Nullable String usage, @Nullable String description, String[] names) {
        this.usage = usage;
        this.description = description;
        this.names = names;
    }

    /**
     * Returns the usage of the command, if present.
     *
     * @return usage
     */
    @Nullable
    public String getUsage() {
        return usage;
    }

    /**
     * Returns the description of the command, if present.
     *
     * @return description
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    // todo: i still don't wanna javadoc this, perhaps after deprecation removal?
    public String[] getNames() {
        return names;
    }
}
