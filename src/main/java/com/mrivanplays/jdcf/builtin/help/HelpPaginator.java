package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class HelpPaginator {

    private final List<EmbedBuilder> pages;
    private final Supplier<EmbedBuilder> errorEmbed;
    private final Translations translations;

    HelpPaginator(List<RegisteredCommand> commands, CommandSettings settings, Member commandExecutor, User author, long guildId) {
        this.translations = settings.getTranslations();
        List<EmbedBuilder> pagesWithCommands = new ArrayList<>();
        List<RegisteredCommand> filteredCommands = commands.stream()
                .filter(cmd -> cmd.getDescription() != null && cmd.getUsage() != null && hasPermission(commandExecutor, cmd.getPermissions()))
                .collect(Collectors.toList());
        String prefix = settings.getPrefixHandler().getPrefix(guildId);
        if (filteredCommands.size() == settings.getCommandsPerHelpPage()) {
            EmbedBuilder embed = settings.getHelpCommandEmbed().get();
            for (RegisteredCommand in : filteredCommands) {
                embed.appendDescription("`" + prefix + in.getUsage() + "` - " + in.getDescription() + "\n");
            }
            pagesWithCommands.add(embed);
        } else {
            for (int i = 0; i < commands.size(); i += settings.getCommandsPerHelpPage()) {
                EmbedBuilder embed = settings.getHelpCommandEmbed().get();
                for (RegisteredCommand in : filteredCommands.subList(i, Math.min(i + settings.getCommandsPerHelpPage(), filteredCommands.size()))) {
                    embed.appendDescription("`" + prefix + in.getUsage() + "` - " + in.getDescription() + "\n");
                }
                if (embed.getDescriptionBuilder().length() == 0) {
                    continue;
                }
                pagesWithCommands.add(embed);
            }
        }
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < pagesWithCommands.size(); i++) {
            EmbedBuilder embed = pagesWithCommands.get(i);
            StringBuilder currentDescriptionBuilder = embed.getDescriptionBuilder();
            String currentDescription = currentDescriptionBuilder.substring(0, currentDescriptionBuilder.length() - 2);
            pages.add(EmbedUtil.setAuthor(embed, author)
                    .setDescription(translations.getTranslation("help_page_specify", (i + 1), pagesWithCommands.size())
                    + "\n" + "\n" + currentDescription));
        }
        this.pages = pages;
        this.errorEmbed = settings.getErrorEmbed();
        pagesWithCommands.clear();
    }

    private boolean hasPermission(Member member, Permission[] permissions) {
        return permissions == null || member.hasPermission(permissions);
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
