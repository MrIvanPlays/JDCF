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
package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.builtin.CommandPrefix;
import com.mrivanplays.jdcf.builtin.help.CommandHelp;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.settings.DefaultCommandSettings;
import com.mrivanplays.jdcf.util.EmbedUtil;
import com.mrivanplays.jdcf.util.EventWaiter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Represents a command manager.
 */
public final class CommandManager implements EventListener {

    private static final Pattern ALIAS_SPLIT_PATTERN = Pattern.compile("\\|");

    private final List<RegisteredCommand> commands;
    private CommandSettings commandSettings;

    public CommandManager(JDA jda) {
        this(jda, DefaultCommandSettings.get());
    }

    public CommandManager(JDA jda, CommandSettings settings) {
        commands = new ArrayList<>();
        commandSettings = settings;
        jda.addEventListener(this);
        getSettings().getExecutorService().schedule(() -> {
            if (getSettings().isEnableHelpCommand()) {
                if (!getCommand("help").isPresent()) {
                    EventWaiter eventWaiter = new EventWaiter();
                    jda.addEventListener(eventWaiter);
                    registerCommand(new CommandHelp(this, eventWaiter));
                }
            }
            if (getSettings().isEnablePrefixCommand()) {
                if (!getCommand("prefix").isPresent()) {
                    registerCommand(new CommandPrefix(this));
                }
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * Registers the specified command into the manager.
     *
     * @param command the command you wish to register
     */
    public void registerCommand(@NotNull Command command) {
        String[] aliases = null;
        String description = null;
        String usage = null;
        Class<? extends Command> commandClass = command.getClass();
        CommandAliases annoAliases = commandClass.getAnnotation(CommandAliases.class);
        if (annoAliases != null) {
            aliases = ALIAS_SPLIT_PATTERN.split(annoAliases.value());
        }
        CommandDescription annoDescription = commandClass.getAnnotation(CommandDescription.class);
        if (annoDescription != null) {
            description = annoDescription.value();
        }
        CommandUsage annoUsage = commandClass.getAnnotation(CommandUsage.class);
        if (annoUsage != null) {
            usage = annoUsage.value();
        }
        RegisteredCommand registeredCommand = new RegisteredCommand(command, usage, description, aliases);
        if (!commands.contains(registeredCommand)) {
            commands.add(registeredCommand);
        }
    }

    /**
     * Returns the settings for all inbuilt things, handlers and stuff.
     *
     * @return settings
     */
    public CommandSettings getSettings() {
        return commandSettings;
    }

    /**
     * Sets a new settings
     *
     * @param settings the new settings you wish to set
     */
    public void setSettings(CommandSettings settings) {
        commandSettings = settings;
    }

    /**
     * Returns a unmodifiable list of the registered commands.
     *
     * @return registered commands
     */
    public List<RegisteredCommand> getRegisteredCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Retrieves the first command found which has the specified alias as alias or the specified alias is the command's
     * name.
     *
     * @param alias the command name/alias for the command you wish to get
     * @return optional of registered command if present, empty optional otherwise
     */
    public Optional<RegisteredCommand> getCommand(String alias) {
        Optional<RegisteredCommand> nameSearch = commands.stream().filter(command -> command.getName().equalsIgnoreCase(alias)).findFirst();
        if (nameSearch.isPresent()) {
            return nameSearch;
        } else {
            return commands.stream().filter(command -> command.getAliases() != null && Arrays.stream(command.getAliases()).anyMatch(al -> al.equalsIgnoreCase(alias))).findFirst();
        }
    }

    // command execution handling and bot shutdown handling
    @Override
    public void onEvent(@Nonnull GenericEvent generic) {
        if (generic.getClass().isAssignableFrom(GuildMessageReceivedEvent.class)) // checks if that's our event
        {
            // yes! thats the event we want
            GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) generic;
            if (event.getAuthor().isBot() || event.getMember() == null) {
                // we don't want to handle if the author is bot or the message is a webhook message
                // (event.getMember() is null if the message is webhook message)
                return;
            }
            String prefix = commandSettings.getPrefixHandler().getPrefix(event.getGuild().getIdLong()); // retrieves the current guild prefix
            String[] content = event.getMessage().getContentRaw().split(" ");
            String aliasPrefix = content[0];
            if (commandSettings.isEnableMentionInsteadPrefix()) {
                // checks if the first typed thing is a mention to our bot
                if (aliasPrefix.equalsIgnoreCase(event.getJDA().getSelfUser().getAsMention())) {
                    Optional<RegisteredCommand> commandOptional = getCommand(content[1]);
                    if (commandOptional.isPresent()) {
                        RegisteredCommand command = commandOptional.get();
                        if (command.getPermissions() != null) {
                            if (!event.getMember().hasPermission(command.getPermissions())) {
                                event.getChannel().sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), event.getAuthor()).build())
                                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                                return;
                            }
                        }
                        command.execute(
                                new CommandExecutionContext(event.getChannel(), event.getMember(), event.getAuthor(),
                                        event.getJDA(), event.getMessage(), event.getGuild(),
                                        content[1]),
                                new CommandArguments(Arrays.copyOfRange(content, 2, content.length), event.getJDA(), event.getGuild()));
                    }
                }
            }
            if (aliasPrefix.startsWith(prefix)) // checks if the first typed thing starts with the guild prefix
            {
                String name = aliasPrefix.replace(prefix, "");
                Optional<RegisteredCommand> commandOptional = getCommand(name);
                if (commandOptional.isPresent()) {
                    RegisteredCommand command = commandOptional.get();
                    if (command.getPermissions() != null) {
                        if (!event.getMember().hasPermission(command.getPermissions())) {
                            event.getChannel().sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), event.getAuthor()).build())
                                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                            return;
                        }
                    }
                    command.execute(
                            new CommandExecutionContext(event.getChannel(), event.getMember(), event.getAuthor(),
                                    event.getJDA(), event.getMessage(), event.getGuild(),
                                    name),
                            new CommandArguments(Arrays.copyOfRange(content, 1, content.length), event.getJDA(), event.getGuild()));
                }
            }
        }
        if (generic.getClass().isAssignableFrom(ShutdownEvent.class)) {
            // this event listening is basically to shutdown and terminate the
            // executor service we have and also save prefixes to a long-term storage
            ScheduledExecutorService executor = commandSettings.getExecutorService();
            executor.shutdownNow();
            try {
                executor.awaitTermination(500, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // be aware that it's your fault if you don't implement savePrefixes method
            // or any of the other methods for the prefix handler
            commandSettings.getPrefixHandler().savePrefixes();
        }
    }
}
