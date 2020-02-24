package com.mrivanplays.jdcf;

import com.mrivanplays.jdcf.args.ArgumentResolverContext;
import com.mrivanplays.jdcf.args.ArgumentResolvers;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.builtin.CommandPrefix;
import com.mrivanplays.jdcf.builtin.help.CommandHelp;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.data.MarkGuildOnly;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.util.CommandDispatcherMessage;
import com.mrivanplays.jdcf.util.EmbedUtil;
import com.mrivanplays.jdcf.util.EventWaiter;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.ShardManager;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.naming.spi.DirObjectFactory;

/**
 * Represents a command manager.
 */
public final class CommandManager implements EventListener {

    private static final Pattern ALIAS_SPLIT_PATTERN = Pattern.compile("\\|");

    private List<RegisteredCommand> commands;
    private CommandSettings commandSettings;
    private Logger logger;
    private List<Consumer<MessageEventSubscriber>> eventSubscribers;

    public CommandManager(@NotNull JDA jda) {
        this(jda, CommandSettings.defaultSettings());
    }

    public CommandManager(@NotNull JDA jda, @NotNull CommandSettings settings) {
        Objects.requireNonNull(jda, "JDA instance cannot be null");
        jda.addEventListener(this);
        init(settings, jda::addEventListener);
    }

    public CommandManager(@NotNull ShardManager shardManager) {
        this(shardManager, CommandSettings.defaultSettings());
    }

    public CommandManager(@NotNull ShardManager shardManager, @NotNull CommandSettings settings) {
        Objects.requireNonNull(shardManager, "ShardManager instance cannot be null");
        shardManager.addEventListener(this);
        init(settings, shardManager::addEventListener);
    }

    private void init(CommandSettings settings, Consumer<EventWaiter> waiterRegistry) {
        commands = new ArrayList<>();
        eventSubscribers = new ArrayList<>();
        logger = LoggerFactory.getLogger(CommandManager.class);
        setSettings(settings);
        getSettings().getExecutorService().schedule(() -> {
            if (getSettings().isEnablePrefixCommand()) {
                if (!getCommand("prefix").isPresent()) {
                    registerCommand(new CommandPrefix(this));
                }
            }
            if (getSettings().isEnableHelpCommand()) {
                if (!getCommand("help").isPresent()) {
                    EventWaiter eventWaiter = new EventWaiter(getSettings().getExecutorService());
                    waiterRegistry.accept(eventWaiter);
                    registerCommand(new CommandHelp(this, eventWaiter));
                }
            }
        }, 1, TimeUnit.SECONDS);

        getSettings().getExecutorService()
                .scheduleAtFixedRate(() -> getSettings().getPrefixHandler().savePrefixes(), 5, 30, TimeUnit.MINUTES);
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
        boolean guildOnly;
        Class<? extends Command> commandClass = command.getClass();
        CommandAliases annoAliases = commandClass.getAnnotation(CommandAliases.class);
        if (annoAliases != null) {
            String value = annoAliases.value();
            boolean hasCharacter = false;
            for (char c : value.toCharArray()) {
                if (c == '|') {
                    hasCharacter = true;
                    break;
                }
            }
            if (hasCharacter) {
                aliases = ALIAS_SPLIT_PATTERN.split(annoAliases.value());
            } else {
                aliases = new String[] {value};
            }
        }
        CommandDescription annoDescription = commandClass.getAnnotation(CommandDescription.class);
        if (annoDescription != null) {
            description = annoDescription.value();
        }
        CommandUsage annoUsage = commandClass.getAnnotation(CommandUsage.class);
        if (annoUsage != null) {
            usage = annoUsage.value();
        }
        MarkGuildOnly guildOnlyMark = commandClass.getAnnotation(MarkGuildOnly.class);
        if (guildOnlyMark != null) {
            guildOnly = true;
        } else {
            guildOnly = command.isGuildOnly();
        }
        registerCommand(new RegisteredCommand(command, usage, description, aliases, guildOnly));
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
     * Adds a {@link MessageEventSubscriber}, called if no commands occur while handling events.
     *
     * @param subscriber subscriber
     */
    public void addMessageEventSubscription(@NotNull Consumer<MessageEventSubscriber> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");
        if (!eventSubscribers.contains(subscriber)) {
            eventSubscribers.add(subscriber);
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
        Optional<RegisteredCommand> nameSearch = commands.stream()
                .filter(command -> command.getDirectCommandName() != null && command.getDirectCommandName().equalsIgnoreCase(alias))
                .findFirst();
        if (nameSearch.isPresent()) {
            return nameSearch;
        } else {
            return commands.stream().filter(command -> command.getAliases() != null && Arrays.stream(command.getAliases()).anyMatch(al -> al.equalsIgnoreCase(alias))).findFirst();
        }
    }

    /**
     * Executes the specified command line with the specified arguments. When dispatched, the author and member of the
     * command are the {@link JDA#getSelfUser()} and {@link Guild#getSelfMember()}. DISCLAIMER: This method does not
     * check for permissions, meaning that when you call it you mean "I want this command executed no matter what".
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
        if (commandSettings.isAllowDMSCommands()) {
            if (generic.getClass().isAssignableFrom(MessageReceivedEvent.class)) {
                MessageReceivedEvent event = (MessageReceivedEvent) generic;
                Guild guild = null;
                Member member = null;
                if (event.getMessage().isFromGuild()) {
                    guild = event.getGuild();
                    member = event.getMember();
                }
                handleMessage(event.getMessage(), guild, event.getJDA(), event.getChannel(), event.getAuthor(), member);
            }
        } else {
            if (generic.getClass().isAssignableFrom(GuildMessageReceivedEvent.class)) {
                GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) generic;
                handleMessage(event.getMessage(), event.getGuild(), event.getJDA(), event.getChannel(), event.getAuthor(), event.getMember());
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

    private void handleMessage(Message message, Guild guild, JDA jda, MessageChannel channel, User author, Member member) {
        if (author.isBot() || message.isWebhookMessage()) {
            // we don't want to handle if the author is bot or the message is a webhook message
            return;
        }
        String prefix;
        if (message.isFromGuild()) {
            prefix = commandSettings.getPrefixHandler().getPrefix(guild.getIdLong());
        } else {
            prefix = commandSettings.getPrefixHandler().getPrefix(author);
        }
        String[] content = message.getContentRaw().split(" ");
        String aliasPrefix = content[0];
        MessageEventSubscriber subscriberEvent = new MessageEventSubscriber(message);
        if (commandSettings.isEnableMentionInsteadPrefix()) {
            try {
                ArgumentResolverContext context;
                if (message.isFromGuild()) {
                    context = new ArgumentResolverContext(aliasPrefix, guild, jda);
                } else {
                    context = new ArgumentResolverContext(aliasPrefix, jda);
                }
                User user = ArgumentResolvers.USER_MENTION.resolve(context);
                if (user.getId().equalsIgnoreCase(jda.getSelfUser().getId())) {
                    if (!executeCommand(content[1], content, 2, member, channel, author, message, jda, guild)) {
                        callSubscribers(subscriberEvent);
                    }
                } else {
                    callSubscribers(subscriberEvent);
                }
            } catch (Exception e) {
                // not a mention
                if (aliasPrefix.startsWith(prefix)) {
                    String name = aliasPrefix.replace(prefix, "");
                    if (!name.isEmpty()) {
                        if (!executeCommand(name, content, 1, member, channel, author, message, jda, guild)) {
                            callSubscribers(subscriberEvent);
                        }
                    } else {
                        callSubscribers(subscriberEvent);
                    }
                } else {
                    callSubscribers(subscriberEvent);
                }
            }
        } else {
            if (aliasPrefix.startsWith(prefix)) {
                String name = aliasPrefix.replace(prefix, "");
                if (!name.isEmpty()) {
                    if (!executeCommand(name, content, 1, member, channel, author, message, jda, guild)) {
                        callSubscribers(subscriberEvent);
                    }
                } else {
                    callSubscribers(subscriberEvent);
                }
            } else {
                callSubscribers(subscriberEvent);
            }
        }
    }

    private boolean executeCommand(String name, String[] content, int argsFrom,
                                   Member member, MessageChannel callbackChannel, User author, Message msg, JDA jda, Guild guild) {
        Optional<RegisteredCommand> commandOptional = getCommand(name);
        if (commandOptional.isPresent()) {
            RegisteredCommand command = commandOptional.get();
            if (command.isGuildOnly()) {
                if (msg.isFromGuild()) {
                    PermissionCheckContext permissionCheck = new PermissionCheckContext(jda, author, guild, member, name);
                    if (!command.hasPermission(permissionCheck)) {
                        callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), author).build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        msg.delete().queueAfter(15, TimeUnit.SECONDS);
                        return false;
                    }
                    TextChannel cec = commandSettings.getCommandExecuteChannel();
                    if (cec != null && !member.hasPermission(Permission.ADMINISTRATOR) && callbackChannel.getIdLong() != cec.getIdLong()) {
                        callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getErrorEmbed().get(), author)
                                .setDescription(commandSettings.getTranslations().getTranslation("commands_channel", cec.getAsMention()))
                                .build()).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        msg.delete().queueAfter(15, TimeUnit.SECONDS);
                        return false;
                    }
                    try {
                        return command.execute(
                                new CommandExecutionContext(msg, name, false),
                                new CommandArguments(Arrays.copyOfRange(content, argsFrom, content.length), jda, guild));
                    } catch (Throwable e) {
                        callbackChannel.sendMessage(commandSettings.getTranslations().getTranslation("error_executing")).queue();
                        e.printStackTrace();
                        return false;
                    } finally {
                        if (commandSettings.isLogExecutedCommands()) {
                            logger.info("\"" + author.getAsTag() +
                                    "\" has executed command \"" + msg.getContentRaw() +
                                    "\" in guild \"" + guild.getName() + "\" with guild id \"" + guild.getId() + "\"");
                        }
                    }
                } else {
                    callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getErrorEmbed().get(), author)
                            .setDescription(commandSettings.getTranslations().getTranslation("command_guild_only", name)).build())
                            .queue(m -> m.delete().queueAfter(15, TimeUnit.SECONDS));
                    return false;
                }
            } else {
                if (msg.isFromGuild()) {
                    PermissionCheckContext permissionCheck = new PermissionCheckContext(jda, author, guild, member, name);
                    if (!command.hasPermission(permissionCheck)) {
                        callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), author).build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        msg.delete().queueAfter(15, TimeUnit.SECONDS);
                        return false;
                    }

                    TextChannel cec = commandSettings.getCommandExecuteChannel();
                    if (cec != null && !member.hasPermission(Permission.ADMINISTRATOR) && callbackChannel.getIdLong() != cec.getIdLong()) {
                        callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getErrorEmbed().get(), author)
                                .setDescription(commandSettings.getTranslations().getTranslation("commands_channel", cec.getAsMention()))
                                .build()).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        msg.delete().queueAfter(15, TimeUnit.SECONDS);
                        return false;
                    }
                    try {
                        return command.execute(
                                new CommandExecutionContext(msg, name, false),
                                new CommandArguments(Arrays.copyOfRange(content, argsFrom, content.length), jda, guild));
                    } catch (Throwable e) {
                        callbackChannel.sendMessage(commandSettings.getTranslations().getTranslation("error_executing")).queue();
                        e.printStackTrace();
                        return false;
                    } finally {
                        if (commandSettings.isLogExecutedCommands()) {
                            logger.info("\"" + author.getAsTag() +
                                    "\" has executed command \"" + msg.getContentRaw() +
                                    "\" in guild \"" + guild.getName() + "\" with guild id \"" + guild.getId() + "\"");
                        }
                    }
                } else {
                    PermissionCheckContext permissionCheck = new PermissionCheckContext(jda, author, guild, member, name);
                    if (!command.hasPermission(permissionCheck)) {
                        callbackChannel.sendMessage(EmbedUtil.setAuthor(commandSettings.getNoPermissionEmbed().get(), author).build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        return false;
                    }
                    try {
                        return command.execute(
                                new CommandExecutionContext(msg, name, false),
                                new CommandArguments(Arrays.copyOfRange(content, argsFrom, content.length), jda, guild));
                    } catch (Throwable e) {
                        callbackChannel.sendMessage(commandSettings.getTranslations().getTranslation("error_executing")).queue();
                        e.printStackTrace();
                        return false;
                    } finally {
                        if (commandSettings.isLogExecutedCommands()) {
                            logger.info("\"" + author.getAsTag() + "\" has executed command \"" + msg.getContentRaw() + "\" in DMs");
                        }
                    }
                }
            }
        } else {
            return false;
        }
    }

    private void callSubscribers(MessageEventSubscriber event) {
        for (Consumer<MessageEventSubscriber> subscriber : eventSubscribers) {
            subscriber.accept(event);
        }
    }
}
