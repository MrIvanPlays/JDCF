package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.data.MarkGuildOnly;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@CommandDescription("Performs an action for the bot's command prefix, depending on what arguments are being specified.")
@CommandUsage("prefix (set [new prefix])")
@CommandAliases("prefix")
@MarkGuildOnly
public class CommandPrefix extends Command {

    private final CommandManager commandManager;

    public CommandPrefix(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        CommandSettings settings = commandManager.getSettings();
        Translations translations = settings.getTranslations();
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
                            context.getAuthor()).setDescription(translations.getTranslation("prefix_changed", prefix)).build()).queue();
                }).orElse(failReason -> {
                    if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                        context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getErrorEmbed().get(), context.getAuthor())
                                .setDescription(translations.getTranslation("specify_prefix")).build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    }
                });
            }
        }).orElse(failReason -> {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                context.getChannel().sendMessage(EmbedUtil.setAuthor(settings.getPrefixCommandEmbed().get(), context.getAuthor())
                        .setDescription(translations.getTranslation("prefix_is",
                                settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong()))).build()).queue();
            }
        });
        return true;
    }
}
