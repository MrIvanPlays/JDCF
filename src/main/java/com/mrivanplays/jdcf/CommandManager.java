package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.builtin.CommandPrefix;
import com.mrivanplays.jdcf.builtin.help.CommandHelp;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.settings.DefaultCommandSettings;
import com.mrivanplays.jdcf.util.CommandDispatcherMessage;
import com.mrivanplays.jdcf.util.EmbedUtil;
import com.mrivanplays.jdcf.util.EventWaiter;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.ShardManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Represents a command manager.
 */
public final class CommandManager implements EventListener {

    private static final Pattern ALIAS_SPLIT_PATTERN = Pattern.compile("\\|");

    private List<RegisteredCommand> commands;
    private CommandSettings commandSettings;

    public CommandManager(@NotNull JDA jda) {
        this(jda, DefaultCommandSettings.get());
    }

    public CommandManager(@NotNull JDA jda, @NotNull CommandSettings settings) {
        Objects.requireNonNull(jda, "JDA instance cannot be null");
        jda.addEventListener(this);
        init(settings, jda::addEventListener);
    }

    public CommandManager(@NotNull ShardManager shardManager) {
        this(shardManager, DefaultCommandSettings.get());
    }

    public CommandManager(@NotNull ShardManager shardManager, @NotNull CommandSettings settings) {
        Objects.requireNonNull(shardManager, "ShardManager instance cannot be null");
        shardManager.addEventListener(this);
        init(settings, shardManager::addEventListener);
    }

    private void init(CommandSettings settings, Consumer<EventWaiter> waiterRegistry) {
        commands = new ArrayList<>();
        setSettings(settings);
        getSettings().getExecutorService().schedule(() -> {
            if (getSettings().isEnableHelpCommand()) {
                if (!getCommand("help").isPresent()) {
                    EventWaiter eventWaiter = new EventWaiter(getSettings().getExecutorService());
                    waiterRegistry.accept(eventWaiter);
                    registerCommand(new CommandHelp(this, eventWaiter));
                }
            }
            if (getSettings().isEnablePrefixCommand()) {
                if (!getCommand("prefix").isPresent()) {
                    registerCommand(new CommandPrefix(this));
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * Registers the specified command into the manager.
     *
     * @param command the command you wish to register
     */
    public void registerCommand(@NotNull Command command) {
        Objects.requireNonNull(command, "Command registered cannot be null");
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
        registerCommand(new RegisteredCommand(command, usage, description, aliases));
    }

    /**
     * Registers the specified command array into the manager
     *
     * @param commands the commands you want to register
     */
    public void registerCommands(@NotNull Command... commands) {
        Objects.requireNonNull(commands, "Commands registered cannot be null");
        Utils.checkState(commands.length != 0, "Commands registered cannot be empty");
        for (Command command : commands) {
            registerCommand(command);
        }
    }

    /**
     * Registers the specified command list into the manager
     *
     * @param commands the commands you want to register
     */
    public void registerCommands(@NotNull List<Command> commands) {
        Objects.requireNonNull(commands, "Commands registered cannot be null");
        Utils.checkState(!commands.isEmpty(), "Commands registered cannot be empty");
        for (Command command : commands) {
            registerCommand(command);
        }
    }

    /**
     * Registers the specified {@link RegisteredCommand} object into the manager.
     *
     * @param registeredCommand the command you want to register
     */
    public void registerCommand(@NotNull RegisteredCommand registeredCommand) {
        Objects.requireNonNull(registeredCommand, "registeredCommand");
        if (!commands.contains(registeredCommand)) {
            commands.add(registeredCommand);
        }
    }

    /**
     * Returns the settings for all inbuilt things, handlers and stuff.
     *
     * @return settings
     */
    @NotNull
    public CommandSettings getSettings() {
        return commandSettings;
    }

    /**
     * Sets a new settings
     *
     * @param settings the new settings you wish to set
     */
    public void setSettings(@NotNull CommandSettings settings) {
        commandSettings = Objects.requireNonNull(settings, "settings");
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
    public Optional<RegisteredCommand> getCommand(@NotNull String alias) {
        Objects.requireNonNull(alias, "alias");
        Utils.checkState(alias.length() != 0, "empty alias");
        Optional<RegisteredCommand> nameSearch = commands.stream().filter(command -> command.getName().equalsIgnoreCase(alias)).findFirst();
        if (nameSearch.isPresent()) {
            return nameSearch;
        } else {
            return commands.stream().filter(command -> command.getAliases() != null && Arrays.stream(command.getAliases()).anyMatch(al -> al.equalsIgnoreCase(alias))).findFirst();
        }
    }

    /**
     * Executes the specified command line with the specified arguments. When dispatched, the author and member of the
     * command are the {@link JDA#getSelfUser()} and {@link Guild#getSelfMember()}
     *
     * @param jda         the jda instance from which the command should be executed
     * @param guild       the guild where the command should be executed
     * @param channel     the channel, used for the command to reply with response
     * @param member      the member for which the command is going to watch parameters
     * @param commandLine the command line to execute
     * @return execution success state
     */
    public boolean dispatchCommand(@NotNull JDA jda, @NotNull Guild guild, @NotNull TextChannel channel, @NotNull Member member, @NotNull String commandLine) {
        Objects.requireNonNull(jda, "jda");
        Objects.requireNonNull(guild, "guild");
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(member, "member");
        Objects.requireNonNull(commandLine, "commandLine");
        Utils.checkState(commandLine.length() != 0, "commandLine length = 0");
        Utils.checkState(guild.getTextChannels().contains(channel), "channel should be from guild");

        String[] content = commandLine.split(" ");
        String alias = content[0];
        String[] args = Arrays.copyOfRange(content, 1, content.length);

        Optional<RegisteredCommand> commandOptional = getCommand(alias);
        if (commandOptional.isPresent()) {
            RegisteredCommand command = commandOptional.get();
            String messageContent = commandSettings.getPrefixHandler().getPrefix(guild.getIdLong()) + commandLine;
            CommandExecutionContext context = new CommandExecutionContext(
                    new CommandDispatcherMessage(messageContent, jda, guild, channel, member, commandSettings.getExecutorService()), alias, true);
            return command.execute(context, new CommandArguments(args, jda, guild));
        }
        return false;
    }

    // command execution handling and bot shutdown handling
    @Override
    public void onEvent(@Nonnull GenericEvent generic) {
        if (generic.getClass().isAssignableFrom(GuildMessageReceivedEvent.class)) { // checks if that's our event
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
                    executeCommand(content[1], event, content, 2);
                }
            }
            if (aliasPrefix.startsWith(prefix)) { // checks if the first typed thing starts with the guild prefix
                String name = aliasPrefix.replace(prefix, "");
                executeCommand(name, event, content, 1);
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

    private void executeCommand(String name, GuildMessageReceivedEvent event, String[] content, int argsFrom) {
        Optional<RegisteredCommand> commandOptional = getCommand(name);
        if (commandOptional.isPresent()) {
            RegisteredCommand command = commandOptional.get();
            if (command.getPermissions() != null) {
                if (!event.getMember().hasPermission(command.getPermissions())) {
                    event.getChannel().sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), event.getAuthor()).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    event.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
            }
            if (commandSettings.getCommandExecuteChannel() != null &&
                    !event.getMember().hasPermission(Permission.ADMINISTRATOR) &&
                    event.getChannel().getIdLong() != commandSettings.getCommandExecuteChannel().getIdLong()) {
                event.getChannel().sendMessage(EmbedUtil.setAuthor(commandSettings.getErrorEmbed().get(), event.getAuthor())
                        .setDescription(commandSettings.getTranslations().getTranslation("commands_channel",
                                commandSettings.getCommandExecuteChannel().getAsMention()))
                        .build()).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                event.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            command.execute(
                    new CommandExecutionContext(event.getMessage(), name, false),
                    new CommandArguments(Arrays.copyOfRange(content, argsFrom, content.length), event.getJDA(), event.getGuild()));
        }
    }
}
