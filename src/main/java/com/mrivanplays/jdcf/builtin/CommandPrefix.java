package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.ArgumentHolder;
import com.mrivanplays.jdcf.args.ArgumentResolveFailure;
import com.mrivanplays.jdcf.args.ArgumentResolveFailures;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import com.mrivanplays.jdcf.data.MarkGuildOnly;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@CommandDescription("Performs an action for the bot's command prefix, depending on what arguments are being specified.")
@CommandUsage("prefix (set [new prefix])")
@CommandAliases("prefix")
@MarkGuildOnly
public class CommandPrefix implements Command {

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull ArgumentHolder args) {
        CommandSettings settings = context.getCommandManagerCreator().getSettings();
        if (settings.getPrefixCommandEmbed() == null) {
            EmbedBuilder errorEmbed = Utils.setAuthor(settings.getErrorEmbed(), context.getAuthor())
                    .setDescription("Whoops; bot prefix command not properly configured. Send this to the developer of the bot.");
            context.getChannel().sendMessage(errorEmbed.build()).queue();
            return true;
        }
        Translations translations = settings.getTranslations();
        // todo: should replace with argument chain when it's done
        args.nextString().ifPresent(subCommand -> {
            if (subCommand.equalsIgnoreCase("set")) {
                if (!context.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    context.getChannel().sendMessage(Utils.setAuthor(settings.getNoPermissionEmbed(), context.getAuthor()).build())
                            .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                    context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    return;
                }
                args.nextString().ifPresent(prefix -> {
                    context.getCommandManagerCreator()
                            .getSettings()
                            .getPrefixHandler()
                            .setGuildPrefix(prefix, context.getGuild().getIdLong());
                    context.getChannel().sendMessage(Utils.setAuthor(settings.getSuccessEmbed(), context.getAuthor())
                            .setDescription(translations.getTranslation("prefix_changed", prefix)).build()).queue();
                }).ifNotPresent(failContext -> {
                    ArgumentResolveFailure<Object> failReason = failContext.getFailureReason();
                    if (ArgumentResolveFailures.isNotPresent(failReason)) {
                        context.getChannel().sendMessage(Utils.setAuthor(settings.getErrorEmbed(), context.getAuthor())
                                .setDescription(translations.getTranslation("specify_prefix")).build())
                                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
                        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
                    }
                }).execute();
            }
        }).ifNotPresent(failContext -> {
            ArgumentResolveFailure<Object> failReason = failContext.getFailureReason();
            if (ArgumentResolveFailures.isNotPresent(failReason)) {
                context.getChannel().sendMessage(Utils.setAuthor(settings.getPrefixCommandEmbed(), context.getAuthor())
                        .setDescription(translations.getTranslation("prefix_is",
                                settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong()))).build()).queue();
            }
        }).execute();
        return true;
    }
}
