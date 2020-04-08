package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.EventWaiter;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandAliases("help")
public class CommandHelp extends Command {

    private final EventWaiter eventWaiter;
    private final String arrowRight = "\u27A1";
    private final String arrowLeft = "\u2B05";
    private final Map<Long, Integer> currentPageMap = new HashMap<>();

    public CommandHelp(EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        CommandManager commandManager = context.getCommandManagerCreator();
        CommandSettings settings = commandManager.getSettings();
        if (settings.getHelpCommandEmbed() == null) {
            EmbedBuilder embed = Utils.setAuthor(settings.getErrorEmbed(), context.getAuthor())
                    .setDescription("Whoops; bot help command not properly configured. Send this to the developer of the bot.");
            context.getChannel().sendMessage(embed.build()).queue();
            return true;
        }
        Translations translations = settings.getTranslations();
        if (context.isFromDispatcher()) {
            throw new UnsupportedOperationException(translations.getTranslation("help_not_executed"));
        }
        String pageName = translations.getTranslation("help_page_specify").split(" ")[0];
        User author = context.getAuthor();
        MessageChannel channel = context.getChannel();
        HelpPaginator paginator = new HelpPaginator(commandManager.getRegisteredCommands(), settings, context);
        args.nextInt().ifPresent(pageNumber -> {
            MessageEmbed page = paginator.getPage(pageNumber).build();
            if (page.getTitle().equalsIgnoreCase(settings.getErrorEmbed().get().build().getTitle())) {
                channel.sendMessage(page).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            channel.sendMessage(page).queue(message -> {
                if (!context.wasExecutedInGuild()) {
                    return;
                }
                if (!message.getEmbeds().isEmpty()) {
                    for (MessageEmbed embed : message.getEmbeds()) {
                        if (embed.getDescription() != null && embed.getDescription().contains(pageName)) {
                            if (paginator.hasNext(pageNumber)) {
                                if (pageNumber != 1) {
                                    message.addReaction(arrowLeft).queue();
                                }
                                message.addReaction(arrowRight).queue();
                                eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                                    ReactionEmote emote = event.getReactionEmote();
                                    if (event.getMessageIdLong() == message.getIdLong() && (!event.getUser().isBot() && !event.getUser().getId().equalsIgnoreCase(author.getId()))) {
                                        message.removeReaction(emote.getEmoji(), event.getUser()).queue();
                                        return false;
                                    }
                                    return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                            && !emote.isEmote())
                                            && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
                                }, event -> {
                                    ReactionEmote emote = event.getReactionEmote();
                                    if (emote.getName().equals(arrowLeft)) {
                                        handleAction(message, author, true, paginator, pageNumber, author.getIdLong());
                                    } else {
                                        handleAction(message, author, false, paginator, pageNumber, author.getIdLong());
                                    }
                                }, () -> message.clearReactions().queue());
                            } else {
                                if (pageNumber != 1) {
                                    message.addReaction(arrowLeft).queue();
                                    eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                                        ReactionEmote emote = event.getReactionEmote();
                                        if (event.getMessageIdLong() == message.getIdLong() && (!event.getUser().isBot() && !event.getUser().getId().equalsIgnoreCase(author.getId()))) {
                                            message.removeReaction(emote.getEmoji(), event.getUser()).queue();
                                            return false;
                                        }
                                        return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                                && !emote.isEmote())
                                                && (arrowLeft.equals(emote.getName()));
                                    }, event -> {
                                        handleAction(message, author, true, paginator, pageNumber, author.getIdLong());
                                    }, () -> message.clearReactions().queue());
                                }
                            }
                        }
                    }
                }
            });
        }).orElse((failReason, parsed) -> {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                channel.sendMessage(paginator.getPage(1).build()).queue(message -> {
                    if (!context.wasExecutedInGuild()) {
                        return;
                    }
                    if (paginator.hasNext(1)) {
                        message.addReaction(arrowRight).queue();
                        eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                            ReactionEmote emote = event.getReactionEmote();
                            if (event.getMessageIdLong() == message.getIdLong() && (!event.getUser().isBot() && !event.getUser().getId().equalsIgnoreCase(author.getId()))) {
                                message.removeReaction(emote.getEmoji(), event.getUser()).queue();
                                return false;
                            }
                            return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                    && !emote.isEmote())
                                    && (arrowRight.equals(emote.getName()));
                        }, event -> {
                            handleAction(message, author, false, paginator, 1, author.getIdLong());
                        }, () -> message.clearReactions().queue());
                    }
                });
                return;
            }
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE) {
                RegisteredCommand command = commandManager.getCommand(parsed).orElse(null);
                String prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
                if (command == null) {
                    channel.sendMessage(Utils.setAuthor(settings.getErrorEmbed(), context.getAuthor())
                            .setDescription(translations.getTranslation("help_invalid_command", prefix)).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                PermissionCheckContext permissionCheck = new PermissionCheckContext(
                        context.getJda(), context.getAuthor(), context.getGuild(), context.getMember(), context.getAlias()
                );
                if (!command.hasPermission(permissionCheck)) {
                    context.getChannel().sendMessage(Utils.setAuthor(settings.getNoPermissionEmbed(), context.getAuthor()).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                if (command.getUsage() == null || command.getDescription() == null) {
                    if (command.getName().equalsIgnoreCase("help")) {
                        channel.sendMessage(translations.getTranslation("help_asked_help", context.getAuthor().getAsMention())).queue();
                        return;
                    }
                    channel.sendMessage(Utils.setAuthor(settings.getErrorEmbed(), context.getAuthor())
                            .setDescription(translations.getTranslation("help_no_data")).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                EmbedBuilder helpCommandEmbed = Utils.setAuthor(settings.getHelpCommandEmbed(), context.getAuthor());
                helpCommandEmbed.addField(getKeyword(settings, "usage"), "`" + prefix + command.getUsage() + "`", true);
                helpCommandEmbed.addField(getKeyword(settings, "description"), command.getDescription(), true);
                if (command.getAliases() != null) { // todo: when deprecation removal happens, this check will be redundant
                    helpCommandEmbed.addField(getKeyword(settings, "aliases"), String.join(", ", command.getAliases()), true);
                }
                channel.sendMessage(helpCommandEmbed.build()).queue();
            }
        });
        System.gc();
        return true;
    }

    private String getKeyword(CommandSettings settings, String keyword) {
        return settings.getTranslations().getTranslation("help_" + keyword + "_keyword");
    }

    private void handleAction(Message message, User author, boolean isArrowLeft, HelpPaginator paginator, int currentPage, long userId) {
        message.clearReactions().queue();
        if (isArrowLeft) {
            int page = currentPage - 1;
            message.editMessage(paginator.getPage(page).build()).queue();
            if (currentPageMap.containsKey(userId)) {
                currentPageMap.replace(userId, page);
            } else {
                currentPageMap.put(userId, page);
            }
            if (page != 1) {
                message.addReaction(arrowLeft).queue();
            }
            if (paginator.hasNext(page)) {
                message.addReaction(arrowRight).queue();
            }
            eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (event.getMessageIdLong() == message.getIdLong() && (!event.getUser().isBot() && !event.getUser().getId().equalsIgnoreCase(author.getId()))) {
                    message.removeReaction(emote.getEmoji(), event.getUser()).queue();
                    return false;
                }
                return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                        && !emote.isEmote())
                        && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
            }, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (emote.getName().equals(arrowLeft)) {
                    handleAction(message, author, true, paginator, currentPageMap.get(userId), userId);
                } else {
                    handleAction(message, author, false, paginator, currentPageMap.get(userId), userId);
                }
            }, () -> message.clearReactions().queue());
        } else {
            int page = currentPage + 1;
            if (currentPageMap.containsKey(userId)) {
                currentPageMap.replace(userId, page);
            } else {
                currentPageMap.put(userId, page);
            }
            message.editMessage(paginator.getPage(page).build()).queue();
            message.addReaction(arrowLeft).queue();
            if (paginator.hasNext(page)) {
                message.addReaction(arrowRight).queue();
            }
            eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (event.getMessageIdLong() == message.getIdLong() && (!event.getUser().isBot() && !event.getUser().getId().equalsIgnoreCase(author.getId()))) {
                    message.removeReaction(emote.getEmoji(), event.getUser()).queue();
                    return false;
                }
                return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                        && !emote.isEmote())
                        && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
            }, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (emote.getName().equals(arrowLeft)) {
                    handleAction(message, author, true, paginator, (currentPageMap.get(userId)), userId);
                } else {
                    handleAction(message, author, false, paginator, (currentPageMap.get(userId)), userId);
                }
            }, () -> message.clearReactions().queue());
        }
    }
}
