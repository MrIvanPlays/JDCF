package com.mrivanplays.jdcf.settings.prefix;

import com.mrivanplays.jdcf.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a prefix handler, working with a simple map. Opens opportunities for much different long term storage
 * types.
 */
public class MapPrefixHandler implements PrefixHandler {

    private Map<Long, String> prefixesMap;
    private Consumer<Map<Long, String>> saveFunction;
    private String defaultPrefix;

    public MapPrefixHandler(Map<Long, String> prefixesMap, Consumer<Map<Long, String>> saveFunction) {
        this(prefixesMap, saveFunction, "!");
    }

    public MapPrefixHandler(Map<Long, String> prefixesMap, Consumer<Map<Long, String>> saveFunction, String defaultPrefix) {
        this.prefixesMap = prefixesMap;
        this.saveFunction = saveFunction;
        this.defaultPrefix = defaultPrefix;
    }

    @Override
    public @NotNull String getDefaultPrefix() {
        return defaultPrefix;
    }

    @Override
    public void setDefaultPrefix(@NotNull String defaultPrefix) {
        this.defaultPrefix = Objects.requireNonNull(defaultPrefix, "defaultPrefix");
    }

    @Override
    public @Nullable String getGuildPrefix(long guildId) {
        return prefixesMap.get(guildId);
    }

    @Override
    public void setGuildPrefix(@NotNull String prefix, long guildId) {
        Objects.requireNonNull(prefix, "prefix");
        Utils.checkState(!(guildId <= 0), "Guild id cannot be 0 or less than 0");
        if (!prefixesMap.containsKey(guildId)) {
            prefixesMap.put(guildId, prefix);
        } else {
            prefixesMap.replace(guildId, prefix);
        }
    }

    @Override
    public void savePrefixes() {
        saveFunction.accept(prefixesMap);
    }
}
