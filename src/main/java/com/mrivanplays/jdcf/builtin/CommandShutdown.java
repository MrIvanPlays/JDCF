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
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents a shutdown command. Due to okhttp bug the shutdown isn't being properly executed by {@link JDA#shutdown()}
 * and so some okhttp threads get left causing the bot to not stop so not save any prefixes or such and so this is why
 * this command was born. You should register it by yourself and so this is why no settings are being present about it
 * into {@link com.mrivanplays.jdcf.settings.CommandSettings}
 */
// no data annotations present as this shouldn't be showed in help command
public class CommandShutdown extends Command {

    private CommandManager commandManager;
    private String botOwnerId;

    /**
     * Creates a new shutdown command instance
     *
     * @param commandManager command manager
     * @param botOwnerId     the discord user id, owner of this bot (the only one who can execute this command)
     */
    public CommandShutdown(@NotNull CommandManager commandManager, @NotNull String botOwnerId) {
        super("shutdown");
        this.commandManager = Objects.requireNonNull(commandManager, "commandManager");
        this.botOwnerId = Objects.requireNonNull(botOwnerId, "botOwnerId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        User author = context.getAuthor();
        if (!author.getId().equalsIgnoreCase(botOwnerId)) {
            context.getChannel().sendMessage(EmbedUtil.setAuthor(commandManager.getSettings().getNoPermissionEmbed().get(), author).build())
                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
            return;
        }
        context.getJda().shutdownNow();
        context.getJda().getHttpClient().connectionPool().evictAll();
        context.getJda().getHttpClient().dispatcher().executorService().shutdown();
    }
}
