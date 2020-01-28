package com.mrivanplays.jdcf.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrivanplays.jdcf.settings.prefix.PrefixHandler;
import com.mrivanplays.jdcf.translation.TranslationCollector;
import com.mrivanplays.jdcf.translation.Translations;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Represents a bunch of settings for the manager and for the command.
 */
public final class CommandSettings {

    private PrefixHandler prefixHandler;
    private boolean enableHelpCommand;
    private boolean enableMentionInsteadPrefix;
    private boolean enablePrefixCommand;
    private ScheduledExecutorService executorService;
    private Supplier<EmbedBuilder> helpCommandEmbed;
    private int commandsPerHelpPage;
    private Supplier<EmbedBuilder> noPermissionEmbed;
    private Supplier<EmbedBuilder> prefixCommandEmbed;
    private Supplier<EmbedBuilder> errorEmbed;
    private Supplier<EmbedBuilder> successEmbed;
    private TextChannel commandExecuteChannel;
    private Translations translations;
    private boolean logExecutedCommands;

    /**
     * Returns the default settings object
     *
     * @return default settings
     */
    public static CommandSettings defaultSettings() {
        CommandSettings settings = new CommandSettings();
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
        settings.setPrefixHandler(PrefixHandler.defaultHandler(new ObjectMapper()));
        try {
            settings.setTranslations(TranslationCollector.getInstance().getTranslations("en"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        settings.setLogExecutedCommands(false);
        return settings;
    }

    /**
     * Returns the prefix handler.
     *
     * @return prefix handler
     */
    @NotNull
    public PrefixHandler getPrefixHandler() {
        return prefixHandler;
    }

    /**
     * Sets a new prefix handler
     *
     * @param prefixHandler new prefix handler
     */
    public void setPrefixHandler(@NotNull PrefixHandler prefixHandler) {
        this.prefixHandler = requireNonNull(prefixHandler, "prefixHandler");
    }

    /**
     * Returns whenever the help command is being enabled.
     *
     * @return true or false
     */
    public boolean isEnableHelpCommand() {
        return enableHelpCommand;
    }

    /**
     * Sets the help command enabled state. You should have on <b>all</b> of your commands the {@link
     * com.mrivanplays.jdcf.data.CommandDescription} and {@link com.mrivanplays.jdcf.data.CommandUsage} annotations, or
     * else the commands that don't have them won't be listed in the help.
     *
     * @param enableHelpCommand value
     */
    public void setEnableHelpCommand(boolean enableHelpCommand) {
        this.enableHelpCommand = enableHelpCommand;
    }

    /**
     * Returns whenever the bot mention can be used instead of a prefix to run a command.
     *
     * @return true or false
     */
    public boolean isEnableMentionInsteadPrefix() {
        return enableMentionInsteadPrefix;
    }

    /**
     * Sets whenever the bot mention can be used instead of a prefix to run a command.
     *
     * @param enableMentionInsteadPrefix value
     */
    public void setEnableMentionInsteadPrefix(boolean enableMentionInsteadPrefix) {
        this.enableMentionInsteadPrefix = enableMentionInsteadPrefix;
    }

    /**
     * Returns whenever the in bundled prefix command is being enabled.
     *
     * @return value
     */
    public boolean isEnablePrefixCommand() {
        return enablePrefixCommand;
    }

    /**
     * Sets if the prefix command is enabled. You may also set the prefix command embed.
     *
     * @param enablePrefixCommand value
     */
    public void setEnablePrefixCommand(boolean enablePrefixCommand) {
        this.enablePrefixCommand = enablePrefixCommand;
    }

    /**
     * Returns a {@link ScheduledExecutorService}. You are not forced to set it if you're gonna use a custom prefix
     * handler as on current version (1.0.0) the only use is prefix saving to file.
     *
     * @return executor
     */
    @NotNull
    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Sets a new executor service.
     *
     * @param executorService executor
     */
    public void setExecutorService(@NotNull ScheduledExecutorService executorService) {
        this.executorService = requireNonNull(executorService, "executorService");
    }

    /**
     * Returns the embed for the help command. You can set the title, color and footer, if other stuff are being
     * modified the command will automatically override them. You are not forced to set it if you're not gonna use the
     * bundled help command.
     *
     * @return embed for help command
     */
    @Nullable
    public Supplier<EmbedBuilder> getHelpCommandEmbed() {
        return helpCommandEmbed;
    }

    /**
     * Sets the help command embed.
     *
     * @param helpCommandEmbed help command embed
     */
    public void setHelpCommandEmbed(@Nullable Supplier<EmbedBuilder> helpCommandEmbed) {
        this.helpCommandEmbed = helpCommandEmbed;
    }

    /**
     * Returns the commands, listed per page on help command.
     *
     * @return number
     */
    public int getCommandsPerHelpPage() {
        return commandsPerHelpPage;
    }

    /**
     * Sets how much commands should be listed per page on help command. You are not forced to set it if you're not
     * using the inbuilt help command.
     *
     * @param commandsPerHelpPage number
     */
    public void setCommandsPerHelpPage(int commandsPerHelpPage) {
        this.commandsPerHelpPage = commandsPerHelpPage;
    }

    /**
     * Returns the no permission embed.
     *
     * @return no permission embed
     */
    @NotNull
    public Supplier<EmbedBuilder> getNoPermissionEmbed() {
        return noPermissionEmbed;
    }

    /**
     * Sets the no permission embed.
     *
     * @param noPermissionEmbed no permission embed
     */
    public void setNoPermissionEmbed(@NotNull Supplier<EmbedBuilder> noPermissionEmbed) {
        this.noPermissionEmbed = requireNonNull(noPermissionEmbed, "noPermissionEmbed");
    }

    /**
     * Returns the prefix command embed. You are not forced to set it if you're not going to use the bundled prefix
     * command.
     *
     * @return prefix embed
     */
    @Nullable
    public Supplier<EmbedBuilder> getPrefixCommandEmbed() {
        return prefixCommandEmbed;
    }

    /**
     * Sets the prefix command embed
     *
     * @param prefixCommandEmbed embed
     */
    public void setPrefixCommandEmbed(@Nullable Supplier<EmbedBuilder> prefixCommandEmbed) {
        this.prefixCommandEmbed = prefixCommandEmbed;
    }

    /**
     * Returns the error embed.
     *
     * @return error embed
     */
    @NotNull
    public Supplier<EmbedBuilder> getErrorEmbed() {
        return errorEmbed;
    }

    /**
     * Sets the error embed
     *
     * @param errorEmbed embed
     */
    public void setErrorEmbed(@NotNull Supplier<EmbedBuilder> errorEmbed) {
        this.errorEmbed = requireNonNull(errorEmbed, "errorEmbed");
    }

    /**
     * Returns the success embed
     *
     * @return success embed
     */
    @NotNull
    public Supplier<EmbedBuilder> getSuccessEmbed() {
        return successEmbed;
    }

    /**
     * Sets the success embed. Usage is being in the "prefix set" command.
     *
     * @param successEmbed embed
     */
    public void setSuccessEmbed(@NotNull Supplier<EmbedBuilder> successEmbed) {
        this.successEmbed = requireNonNull(successEmbed, "successEmbed");
    }

    /**
     * Returns the channel, where commands should be executed. If null, commands can be executed anywhere.
     *
     * @return channel
     */
    @Nullable
    public TextChannel getCommandExecuteChannel() {
        return commandExecuteChannel;
    }

    /**
     * Sets the channel where commands should be executed.
     *
     * @param channel the channel where you want only commands to be executed
     */
    public void setCommandExecuteChannel(@Nullable TextChannel channel) {
        this.commandExecuteChannel = channel;
    }

    /**
     * Returns the translations for messages.
     *
     * @return translations
     */
    @NotNull
    public Translations getTranslations() {
        return translations;
    }

    /**
     * Sets new translations for messages
     *
     * @param translations new translations
     */
    public void setTranslations(@NotNull Translations translations) {
        this.translations = requireNonNull(translations, "translations");
    }

    /**
     * Should the framework log executed commands
     *
     * @return <code>true</code> if we should, <code>false</code> otherwise
     */
    public boolean isLogExecutedCommands() {
        return logExecutedCommands;
    }

    /**
     * Sets whenever the framework should log executed commands
     *
     * @param logExecutedCommands value
     */
    public void setLogExecutedCommands(boolean logExecutedCommands) {
        this.logExecutedCommands = logExecutedCommands;
    }
}
