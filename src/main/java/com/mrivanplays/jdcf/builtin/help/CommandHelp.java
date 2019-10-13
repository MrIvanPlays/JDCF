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
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.util.EmbedUtil;
import com.mrivanplays.jdcf.util.EventWaiter;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class CommandHelp extends Command
{

    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;
    private final String arrowRight = "\u27A1";
    private final String arrowLeft = "\u2B05";

    public CommandHelp(CommandManager commandManager, EventWaiter eventWaiter)
    {
        super("help");
        this.commandManager = commandManager;
        this.eventWaiter = eventWaiter;
    }

    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args)
    {
        CommandSettings settings = commandManager.getSettings();
        HelpPaginator paginator = new HelpPaginator(commandManager.getRegisteredCommands(), settings.getCommandsPerHelpPage(), settings.getHelpCommandEmbed(),
                context.getMember(), context.getAuthor(), settings.getErrorEmbed());
        if (args.size() == 0)
        {
            context.getChannel().sendMessage(paginator.getPage(1).build()).queue(message -> {
                if (paginator.hasNext(1))
                {
                    message.addReaction(arrowRight).queue();
                    eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                        ReactionEmote emote = event.getReactionEmote();
                        return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                && !emote.isEmote())
                                && (arrowRight.equals(emote.getName()));
                    }, event -> {
                        // todo
                    }, () -> {
                        message.clearReactions().queue();
                        message.editMessage(settings.getHelpCommandEmbed().get().setDescription("Listening for reaction stopped").build()).queue();
                    });
                }
            });
            return;
        }
        args.nextInt().ifPresent(pageNumber -> {
            context.getChannel().sendMessage(paginator.getPage(pageNumber).build()).queue(message -> {
                if (!message.getEmbeds().isEmpty())
                {
                    for (MessageEmbed embed : message.getEmbeds())
                    {
                        if (embed.getTitle() != null && embed.getTitle().equalsIgnoreCase("Error"))
                        {
                            message.delete().queueAfter(15, TimeUnit.SECONDS);
                            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                        }
                        if (embed.getDescription() != null && embed.getDescription().contains("Page"))
                        {
                            if (paginator.hasNext(pageNumber))
                            {
                                if (pageNumber != 1)
                                {
                                    message.addReaction(arrowLeft).queue();
                                }
                                message.addReaction(arrowRight).queue();
                                eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                                    ReactionEmote emote = event.getReactionEmote();
                                    return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                            && !emote.isEmote())
                                            && (arrowRight.equals(emote.getName()));
                                }, event -> {
                                    // todo
                                }, () -> {
                                    message.clearReactions().queue();
                                    context.getChannel().sendMessage(settings.getHelpCommandEmbed().get()
                                            .setDescription("Listening for reaction for last executed help command").build()).queue();
                                });
                            } else
                            {
                                if (pageNumber != 1)
                                {
                                    message.addReaction(arrowLeft).queue();
                                    eventWaiter.waitFor(GuildMessageReactionAddEvent.class, event -> {
                                        ReactionEmote emote = event.getReactionEmote();
                                        return (!event.getUser().isBot() && event.getMessageIdLong() == message.getIdLong()
                                                && !emote.isEmote())
                                                && (arrowRight.equals(emote.getName()));
                                    }, event -> {
                                        // todo
                                    }, () -> {
                                        message.clearReactions().queue();
                                        context.getChannel().sendMessage(settings.getHelpCommandEmbed().get()
                                                .setDescription("Listening for reaction for last executed help command").build()).queue();
                                    });
                                }
                            }
                        }
                    }
                }
            });
        }).orElse(failReason -> {
            if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE)
            {
                EmbedBuilder errorEmbed = settings.getErrorEmbed().get();
                context.getChannel().sendMessage(EmbedUtil.setAuthor(errorEmbed, context.getAuthor())
                        .setDescription("You should type a number").build())
                        .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            }
        });
    }
}
