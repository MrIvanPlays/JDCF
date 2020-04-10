package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.ArgumentResolveFailContext;
import com.mrivanplays.jdcf.args.ArgumentResolveGlobalFailHandler;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Represents the default global argument fail handler.
 *
 * @see ArgumentResolveGlobalFailHandler
 */
public class DefaultArgumentResolveGlobalFailHandler implements ArgumentResolveGlobalFailHandler {

    @Override
    public void handleGlobalFail(@NotNull CommandExecutionContext context, @NotNull ArgumentResolveFailContext<?> failContext) {
        CommandSettings settings = context.getCommandManagerCreator().getSettings();
        Supplier<EmbedBuilder> errorEmbedSupplier = settings.getErrorEmbed();
        EmbedBuilder embed = Utils.setAuthor(errorEmbedSupplier, context.getAuthor())
                .setDescription(failContext.getFailureReason().getReasonString());
        context.getChannel().sendMessage(embed.build()).queue(msg -> msg.delete().queueAfter(15, TimeUnit.SECONDS));
        context.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
    }
}
