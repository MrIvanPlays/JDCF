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
package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.settings.CommandSettings;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandHelp extends Command {

    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;
    private final String arrowRight = "\u27A1";
    private final String arrowLeft = "\u2B05";
    private final Map<Long, Integer> currentPageMap = new HashMap<>();

    public CommandHelp(CommandManager commandManager, EventWaiter eventWaiter) {
        super("help");
        this.commandManager = commandManager;
        this.eventWaiter = eventWaiter;
    }

    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        CommandSettings settings = commandManager.getSettings();
        User author = context.getAuthor();
        TextChannel channel = context.getChannel();
        HelpPaginator paginator = new HelpPaginator(commandManager.getRegisteredCommands(), settings, context.getMember(), context.getAuthor());
        args.nextInt().ifPresent(pageNumber -> {
            channel.sendMessage(paginator.getPage(pageNumber).build()).queue(message -> {
                if (!message.getEmbeds().isEmpty()) {
                    for (MessageEmbed embed : message.getEmbeds()) {
                        if (embed.getTitle() != null && embed.getTitle().equalsIgnoreCase("Error")) {
                            message.delete().queueAfter(15, TimeUnit.SECONDS);
                            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                        }
                        if (embed.getDescription() != null && embed.getDescription().contains("Page")) {
                            if (paginator.hasNext(pageNumber)) {
                                if (pageNumber != 1) {
                                    message.addReaction(arrowLeft).queue();
                                }
                                message.addReaction(arrowRight).queue();
                                eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                                    ReactionEmote emote = event.getReactionEmote();
                                    return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                            && !emote.isEmote())
                                            && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
                                }, event -> {
                                    ReactionEmote emote = event.getReactionEmote();
                                    if (emote.getName().equals(arrowLeft)) {
                                        handleAction(message, true, paginator, pageNumber, author.getIdLong());
                                    } else {
                                        handleAction(message, false, paginator, pageNumber, author.getIdLong());
                                    }
                                }, () -> message.clearReactions().queue());
                            } else {
                                if (pageNumber != 1) {
                                    message.addReaction(arrowLeft).queue();
                                    eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                                        ReactionEmote emote = event.getReactionEmote();
                                        return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                                && !emote.isEmote())
                                                && (arrowLeft.equals(emote.getName()));
                                    }, event -> {
                                        handleAction(message, true, paginator, pageNumber, author.getIdLong());
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
                        eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                            ReactionEmote emote = event.getReactionEmote();
                            return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                    && !emote.isEmote())
                                    && (arrowRight.equals(emote.getName()));
                        }, event -> {
                            handleAction(message, false, paginator, 1, author.getIdLong());
                        }, () -> message.clearReactions().queue());
                    }
                });
                return;
            }
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE) {
                RegisteredCommand command = commandManager.getCommand(parsed).orElse(null);
                if (command == null) {
                    EmbedBuilder errorEmbed = settings.getErrorEmbed().get();
                    channel.sendMessage(EmbedUtil.setAuthor(errorEmbed, context.getAuthor())
                            .setDescription("You should type either a page number or a command name.").build())
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
                        channel.sendMessage(
                                context.getAuthor().getAsMention() + ", can I ask you what can a help command do except provide help for commands?")
                                .queue();
                        return;
                    }
                    EmbedBuilder errorEmbed = settings.getErrorEmbed().get();
                    channel.sendMessage(EmbedUtil.setAuthor(errorEmbed, context.getAuthor())
                            .setDescription("This command hasn't got a description or a usage.").build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                EmbedBuilder helpCommandEmbed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), context.getAuthor());
                helpCommandEmbed.addField("Usage", "`" + command.getUsage() + "`", true);
                helpCommandEmbed.addField("Description", "`" + command.getDescription() + "`", true);
                if (command.getAliases() != null) {
                    helpCommandEmbed.addField("Aliases", String.join(", ", command.getAliases()), true);
                }
                channel.sendMessage(helpCommandEmbed.build()).queue();
            }
        });
    }

    private void handleAction(Message message, boolean isArrowLeft, HelpPaginator paginator, int currentPage, long userId) {
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
            eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                ReactionEmote emote = event.getReactionEmote();
                return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                        && !emote.isEmote())
                        && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
            }, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (emote.getName().equals(arrowLeft)) {
                    handleAction(message, true, paginator, currentPageMap.get(userId), userId);
                } else {
                    handleAction(message, false, paginator, currentPageMap.get(userId), userId);
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
            eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                ReactionEmote emote = event.getReactionEmote();
                return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                        && !emote.isEmote())
                        && (arrowLeft.equals(emote.getName()) || arrowRight.equals(emote.getName()));
            }, event -> {
                ReactionEmote emote = event.getReactionEmote();
                if (emote.getName().equals(arrowLeft)) {
                    handleAction(message, true, paginator, (currentPageMap.get(userId)), userId);
                } else {
                    handleAction(message, false, paginator, (currentPageMap.get(userId)), userId);
                }
            }, () -> message.clearReactions().queue());
        }
    }
}
