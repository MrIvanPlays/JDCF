package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.EmbedUtil;
import com.mrivanplays.jdcf.util.EventWaiter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandHelp extends Command {

    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;
    private final String arrowRight = "\u27A1";
    private final String arrowLeft = "\u2B05";
    private final Map<Long, Integer> currentPageMap = new HashMap<>();
    private List<List<RegisteredCommand>> paginatedCommands;

    public CommandHelp(CommandManager commandManager, EventWaiter eventWaiter, List<List<RegisteredCommand>> paginatedCommands) {
        super("help");
        this.commandManager = commandManager;
        this.eventWaiter = eventWaiter;
        this.paginatedCommands = paginatedCommands;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        CommandSettings settings = commandManager.getSettings();
        Translations translations = settings.getTranslations();
        if (context.isFromDispatcher()) {
            throw new UnsupportedOperationException(translations.getTranslation("help_not_executed"));
        }
        String pageName = translations.getTranslation("help_page_specify").split(" ")[0];
        User author = context.getAuthor();
        TextChannel channel = context.getChannel();
        HelpPaginator paginator = new HelpPaginator(paginatedCommands, settings,
                context.getMember(), context.getAuthor(), context.getGuild().getIdLong(), context.getAlias());
        args.nextInt().ifPresent(pageNumber -> {
            MessageEmbed page = paginator.getPage(pageNumber).build();
            if (page.getTitle().equalsIgnoreCase(settings.getErrorEmbed().get().build().getTitle())) {
                channel.sendMessage(page).queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                return;
            }
            channel.sendMessage(page).queue(message -> {
                if (!message.getEmbeds().isEmpty()) {
                    for (MessageEmbed embed : message.getEmbeds()) {
                        if (embed.getDescription() != null && embed.getDescription().contains(pageName)) {
                            if (paginator.hasNext(pageNumber)) {
                                if (pageNumber != 1) {
                                    message.addReaction(arrowLeft).queue();
                                }
                                message.addReaction(arrowRight).queue();
                                eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
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
                                    eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
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
                    if (paginator.hasNext(1)) {
                        message.addReaction(arrowRight).queue();
                        eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
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
                    EmbedBuilder errorEmbed = settings.getErrorEmbed().get();
                    channel.sendMessage(EmbedUtil.setAuthor(errorEmbed, context.getAuthor())
                            .setDescription(translations.getTranslation("help_invalid_command", prefix)).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                if (command.getPermissions() != null && !context.getMember().hasPermission(command.getPermissions())) {
                    context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getNoPermissionEmbed().get(), context.getAuthor()).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                if (command.getUsage() == null || command.getDescription() == null) {
                    if (command.getName().equalsIgnoreCase("help")) {
                        channel.sendMessage(translations.getTranslation("help_asked_help", context.getAuthor().getAsMention())).queue();
                        return;
                    }
                    EmbedBuilder errorEmbed = settings.getErrorEmbed().get();
                    channel.sendMessage(EmbedUtil.setAuthor(errorEmbed, context.getAuthor())
                            .setDescription(translations.getTranslation("help_no_data")).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                EmbedBuilder helpCommandEmbed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), context.getAuthor());
                helpCommandEmbed.addField(getKeyword("usage"), "`" + prefix + command.getUsage() + "`", true);
                helpCommandEmbed.addField(getKeyword("description"), command.getDescription(), true);
                if (command.getAliases() != null) {
                    helpCommandEmbed.addField(getKeyword("aliases"), String.join(", ", command.getAliases()), true);
                }
                channel.sendMessage(helpCommandEmbed.build()).queue();
            }
        });
        System.gc();
        return true;
    }

    private String getKeyword(String keyword) {
        return commandManager.getSettings().getTranslations().getTranslation("help_" + keyword + "_keyword");
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
            eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
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
            eventWaiter.waitForEvent(GuildMessageReactionAddEvent.class, event -> {
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
