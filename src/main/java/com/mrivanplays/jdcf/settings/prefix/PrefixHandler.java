package com.mrivanplays.jdcf.settings.prefix;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a {@link com.mrivanplays.jdcf.Command} prefix handler.
 */
public interface PrefixHandler {

    /**
     * Default implementation of a prefix handler, using json file as a long term storage.
     *
     * @param jsonMapper jackson object mapper
     * @return non null default prefix handler
     */
    @NotNull
    static PrefixHandler defaultHandler(@NotNull ObjectMapper jsonMapper) {
        Objects.requireNonNull(jsonMapper, "jsonMapper");
        PrefixHandler prefixHandler = null;
        TypeReference<HashMap<Long, String>> mapType = new TypeReference<HashMap<Long, String>>() {
        };
        File file = new File("prefixes.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        Consumer<Map<Long, String>> saveFunction = saveMap -> {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
            try (Writer writer = new FileWriter(file)) {
                writer.write(jsonMapper.writer().writeValueAsString(saveMap));
            } catch (IOException ignored) {
            }
        };
        try (Reader reader = new FileReader(file)) {
            Map<Long, String> map = jsonMapper.readValue(reader, mapType);
            if (map == null) {
                map = new HashMap<>();
            }
            prefixHandler = new MapPrefixHandler(map, saveFunction);
        } catch (IOException ignored) {
        }
        if (prefixHandler == null) {
            prefixHandler = new MapPrefixHandler(new HashMap<>(), saveFunction);
        }
        return prefixHandler;
    }

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
     * Saves the prefixes
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

    /**
     * Gets the prefix of which the bot is going to listen for commands in DMs of this user.
     *
     * @param user the user who wants to execute commands in DMs
     * @return prefix of guild
     */
    @NotNull
    default String getPrefix(User user) {
        return getPrefix(user.getMutualGuilds().get(0).getIdLong());
    }
}
