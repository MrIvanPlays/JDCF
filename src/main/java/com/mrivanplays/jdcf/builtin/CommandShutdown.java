package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.MarkGuildOnly;

import net.dv8tion.jda.api.JDA;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a shutdown command. Due to okhttp bug the shutdown isn't being properly executed by {@link JDA#shutdown()}
 * and so some okhttp threads get left causing the bot to not stop so not save any prefixes or such and so this is why
 * this command was born. You should register it by yourself and so this is why no settings are being present about it
 * into {@link com.mrivanplays.jdcf.settings.CommandSettings}
 */
// no data annotations present as this shouldn't be showed in help command
@CommandAliases("shutdown")
@MarkGuildOnly
public class CommandShutdown extends Command {

    private String botOwnerId;

    /**
     * Creates a new shutdown command instance
     *
     * @param botOwnerId the discord user id, owner of this bot (the only one who can execute this command)
     */
    public CommandShutdown(@NotNull String botOwnerId) {
        this.botOwnerId = Objects.requireNonNull(botOwnerId, "botOwnerId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(@NotNull PermissionCheckContext context) {
        return context.getMember().getId().equalsIgnoreCase(botOwnerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        context.getJda().shutdownNow();
        context.getJda().getHttpClient().connectionPool().evictAll();
        context.getJda().getHttpClient().dispatcher().executorService().shutdown();
        return true;
    }
}
