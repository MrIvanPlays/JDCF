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
package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@CommandDescription("Performs an action for the bot's command prefix, depending on what arguments are being specified.")
@CommandUsage("prefix (set [new prefix])")
public class CommandPrefix extends Command {

    private final CommandManager commandManager;

    public CommandPrefix(CommandManager commandManager) {
        super("prefix");
        this.commandManager = commandManager;
    }

    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        CommandSettings settings = commandManager.getSettings();
        if (args.size() == 0) {
            context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getPrefixCommandEmbed().get(), context.getAuthor())
                    .setDescription("Prefix is: " + settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong())).build()).queue();
            return;
        }
        args.nextString().ifPresent(subCommand -> {
            if (subCommand.equalsIgnoreCase("set")) {
                if (!context.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getNoPermissionEmbed().get(), context.getAuthor()).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                args.nextString().ifPresent(prefix -> {
                    commandManager.getSettings().getPrefixHandler().setGuildPrefix(prefix, context.getGuild().getIdLong());
                    context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getSuccessEmbed().get(),
                            context.getAuthor()).setDescription("Prefix successfully changed to: " + prefix).build()).queue();
                }).orElse(failReason -> {
                    if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                        context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getErrorEmbed().get(), context.getAuthor())
                                .setDescription("You should also specify a prefix to set.").build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    }
                });
            }
        });
    }
}
