package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.CommandData;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.ArgumentParseGlobalFailHandler;
import com.mrivanplays.jdcf.args.ArgumentParsingState;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Represents the default fail reason handler.
 *
 * @see ArgumentParseGlobalFailHandler
 */
public class DefaultFailReasonHandler implements ArgumentParseGlobalFailHandler {

    @Override
    public void handleGlobalFail(@NotNull CommandExecutionContext context, @NotNull ArgumentParsingState<?> parsingState, @Nullable String argument, int argumentPosition) {
        CommandData commandData = context.getCommandData();
        CommandSettings settings = context.getCommandManagerCreator().getSettings();
        Translations translations = settings.getTranslations();
        Supplier<EmbedBuilder> errorEmbedSupplier = settings.getErrorEmbed();
        if (commandData.getUsage() != null) {
            String prefix;
            if (context.getGuild() != null) {
                prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
            } else {
                prefix = settings.getPrefixHandler().getPrefix(context.getAuthor());
            }
            String usage = prefix + commandData.getUsage();
            EmbedBuilder embed = Utils.setAuthor(errorEmbedSupplier, context.getAuthor())
                    .setDescription(translations.getTranslation("incorrect_usage", usage));
            context.getChannel().sendMessage(embed.build()).queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
            context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
        }
    }
}
