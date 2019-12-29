package com.mrivanplays.jdcf.settings;

import com.mrivanplays.jdcf.settings.prefix.DefaultPrefixHandler;
import com.mrivanplays.jdcf.translation.TranslationCollector;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;

public class DefaultCommandSettings {

    private static final CommandSettings settings;

    static {
        settings = new CommandSettings();
        settings.setEnableHelpCommand(false);
        settings.setEnableMentionInsteadPrefix(true);
        settings.setEnablePrefixCommand(true);
        settings.setExecutorService(Executors.newSingleThreadScheduledExecutor());
        settings.setNoPermissionEmbed(
                () -> new EmbedBuilder().setColor(Color.RED).setTimestamp(Instant.now())
                        .setTitle("Insufficient permissions")
                        .setDescription("You don't have permission to perform this command."));
        settings.setPrefixCommandEmbed(() -> new EmbedBuilder().setColor(Color.BLUE).setTimestamp(Instant.now()).setTitle("Prefix"));
        settings.setErrorEmbed(() -> new EmbedBuilder().setTimestamp(Instant.now()).setColor(Color.RED).setTitle("Error"));
        settings.setSuccessEmbed(() -> new EmbedBuilder().setTimestamp(Instant.now()).setColor(Color.GREEN).setTitle("Success"));
        settings.setPrefixHandler(new DefaultPrefixHandler(settings.getExecutorService()));
        try {
            settings.setTranslations(TranslationCollector.getInstance().getTranslations("en"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CommandSettings get() {
        return settings;
    }
}
