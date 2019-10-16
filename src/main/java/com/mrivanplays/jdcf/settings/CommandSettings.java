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
package com.mrivanplays.jdcf.settings;

import com.mrivanplays.jdcf.settings.prefix.PrefixHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a bunch of settings for the manager
 * and for the command.
 */
public final class CommandSettings
{

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

    /**
     * Returns the prefix handler.
     *
     * @return prefix handler
     */
    @NotNull
    public PrefixHandler getPrefixHandler()
    {
        return prefixHandler;
    }

    /**
     * Sets a new prefix handler
     *
     * @param prefixHandler new prefix handler
     */
    public void setPrefixHandler(@NotNull PrefixHandler prefixHandler)
    {
        this.prefixHandler = prefixHandler;
    }

    /**
     * Returns whenever the help command is being enabled.
     *
     * @return true or false
     */
    public boolean isEnableHelpCommand()
    {
        return enableHelpCommand;
    }

    /**
     * Sets the help command enabled state. You should have on <b>all</b>
     * of your commands the {@link com.mrivanplays.jdcf.data.CommandDescription}
     * and {@link com.mrivanplays.jdcf.data.CommandUsage} annotations, or else
     * the commands that don't have them won't be listed in the help.
     *
     * @param enableHelpCommand value
     */
    public void setEnableHelpCommand(boolean enableHelpCommand)
    {
        this.enableHelpCommand = enableHelpCommand;
    }

    /**
     * Returns whenever the bot mention can be used instead of
     * a prefix to run a command.
     *
     * @return true or false
     */
    public boolean isEnableMentionInsteadPrefix()
    {
        return enableMentionInsteadPrefix;
    }

    /**
     * Sets whenever the bot mention can be used instead of a
     * prefix to run a command.
     *
     * @param enableMentionInsteadPrefix value
     */
    public void setEnableMentionInsteadPrefix(boolean enableMentionInsteadPrefix)
    {
        this.enableMentionInsteadPrefix = enableMentionInsteadPrefix;
    }

    /**
     * Returns whenever the in bundled prefix command is being enabled.
     *
     * @return value
     */
    public boolean isEnablePrefixCommand()
    {
        return enablePrefixCommand;
    }

    /**
     * Sets if the prefix command is enabled. You may also set the prefix
     * command embed.
     *
     * @param enablePrefixCommand value
     */
    public void setEnablePrefixCommand(boolean enablePrefixCommand)
    {
        this.enablePrefixCommand = enablePrefixCommand;
    }

    /**
     * Returns a {@link ScheduledExecutorService}. You are not forced to set it
     * if you're gonna use a custom prefix handler as on current version (1.0.0)
     * the only use is prefix saving to file.
     *
     * @return executor
     */
    @NotNull
    public ScheduledExecutorService getExecutorService()
    {
        return executorService;
    }

    /**
     * Sets a new executor service.
     *
     * @param executorService executor
     */
    public void setExecutorService(@NotNull ScheduledExecutorService executorService)
    {
        this.executorService = executorService;
    }

    /**
     * Returns the embed for the help command. You can set the title, color and
     * footer, if other stuff are being modified the command will automatically
     * override them. You are not forced to set it if you're not gonna use the
     * bundled help command.
     *
     * @return embed for help command
     */
    @Nullable
    public Supplier<EmbedBuilder> getHelpCommandEmbed()
    {
        return helpCommandEmbed;
    }

    /**
     * Sets the help command embed.
     *
     * @param helpCommandEmbed help command embed
     */
    public void setHelpCommandEmbed(@Nullable Supplier<EmbedBuilder> helpCommandEmbed)
    {
        this.helpCommandEmbed = helpCommandEmbed;
    }

    /**
     * Returns the commands, listed per page on help command.
     *
     * @return number
     */
    public int getCommandsPerHelpPage()
    {
        return commandsPerHelpPage;
    }

    /**
     * Sets how much commands should be listed per page on help command.
     * You are not forced to set it if you're not using the inbuilt help command.
     *
     * @param commandsPerHelpPage number
     */
    public void setCommandsPerHelpPage(int commandsPerHelpPage)
    {
        this.commandsPerHelpPage = commandsPerHelpPage;
    }

    /**
     * Returns the no permission embed.
     *
     * @return no permission embed
     */
    @NotNull
    public Supplier<EmbedBuilder> getNoPermissionEmbed()
    {
        return noPermissionEmbed;
    }

    /**
     * Sets the no permission embed.
     *
     * @param noPermissionEmbed no permission embed
     */
    public void setNoPermissionEmbed(@NotNull Supplier<EmbedBuilder> noPermissionEmbed)
    {
        this.noPermissionEmbed = noPermissionEmbed;
    }

    /**
     * Returns the prefix command embed. You are not forced to set it
     * if you're not going to use the bundled prefix command.
     *
     * @return prefix embed
     */
    @Nullable
    public Supplier<EmbedBuilder> getPrefixCommandEmbed()
    {
        return prefixCommandEmbed;
    }

    /**
     * Sets the prefix command embed
     *
     * @param prefixCommandEmbed embed
     */
    public void setPrefixCommandEmbed(@Nullable Supplier<EmbedBuilder> prefixCommandEmbed)
    {
        this.prefixCommandEmbed = prefixCommandEmbed;
    }

    /**
     * Returns the error embed.
     *
     * @return error embed
     */
    @NotNull
    public Supplier<EmbedBuilder> getErrorEmbed()
    {
        return errorEmbed;
    }

    /**
     * Sets the error embed
     *
     * @param errorEmbed embed
     */
    public void setErrorEmbed(@NotNull Supplier<EmbedBuilder> errorEmbed)
    {
        this.errorEmbed = errorEmbed;
    }
}
