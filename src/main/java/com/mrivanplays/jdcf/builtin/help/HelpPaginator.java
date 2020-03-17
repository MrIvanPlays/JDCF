package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class HelpPaginator {

    private final List<EmbedBuilder> pages;
    private final Supplier<EmbedBuilder> errorEmbed;
    private final Translations translations;

    public HelpPaginator(List<RegisteredCommand> commands, CommandSettings settings, CommandExecutionContext context) {
        this.translations = settings.getTranslations();
        this.errorEmbed = settings.getErrorEmbed();
        String prefix;
        Guild guild;
        if (context.wasExecutedInGuild()) {
            prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
            guild = context.getGuild();
        } else {
            prefix = settings.getPrefixHandler().getPrefix(context.getAuthor());
            guild = context.getAuthor().getMutualGuilds().get(0);
        }
        Member member = guild.getMember(context.getAuthor());
        PermissionCheckContext permissionCheck = new PermissionCheckContext(context.getJda(), context.getAuthor(), guild, member, context.getAlias());
        if (commands.size() <= settings.getCommandsPerHelpPage()) {
            List<RegisteredCommand> page = commands
                    .stream()
                    .filter(cmd ->
                            cmd.getDescription() != null & cmd.getUsage() != null && cmd.hasPermission(permissionCheck))
                    .collect(Collectors.toList());
            EmbedBuilder embed = Utils.setAuthor(settings.getHelpCommandEmbed(), context.getAuthor());
            StringBuilder commandDescription = new StringBuilder();
            for (RegisteredCommand in : page) {
                commandDescription.append("`")
                        .append(prefix)
                        .append(in.getUsage())
                        .append("` - ")
                        .append(in.getDescription())
                        .append("\n");
            }
            this.pages = Collections.singletonList(embed.setDescription(
                    translations.getTranslation("help_page_specify", 1, 1)
                            + "\n" + "\n" + commandDescription.toString()));
            return;
        }
        List<List<RegisteredCommand>> filteredPages =
                Utils.getPages(
                        commands.stream()
                                .filter(cmd ->
                                        cmd.getDescription() != null && cmd.getUsage() != null && cmd.hasPermission(permissionCheck))
                                .collect(Collectors.toList()),
                        settings.getCommandsPerHelpPage()
                );
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < filteredPages.size(); i++) {
            List<RegisteredCommand> page = filteredPages.get(i);
            EmbedBuilder embed = Utils.setAuthor(settings.getHelpCommandEmbed(), context.getAuthor());
            StringBuilder commandDescription = new StringBuilder();
            for (RegisteredCommand in : page) {
                commandDescription.append("`")
                        .append(prefix)
                        .append(in.getUsage())
                        .append("` - ")
                        .append(in.getDescription())
                        .append("\n");
            }
            pages.add(embed.setDescription(
                    translations.getTranslation("help_page_specify", (i + 1), filteredPages.size())
                            + "\n" + "\n" + commandDescription.toString()
            ));
        }
        this.pages = pages;
    }

    EmbedBuilder getPage(int page) {
        try {
            return pages.get(page - 1);
        } catch (IndexOutOfBoundsException e) {
            return errorEmbed.get().setDescription(translations.getTranslation("help_page_not_exist", page));
        }
    }

    boolean hasNext(int current) {
        return pages.size() > current;
    }
}
