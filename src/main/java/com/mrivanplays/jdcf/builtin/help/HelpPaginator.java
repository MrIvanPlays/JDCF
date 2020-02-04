package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.translation.Translations;
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class HelpPaginator {

    private final List<EmbedBuilder> pages;
    private final Supplier<EmbedBuilder> errorEmbed;
    private final Translations translations;

    public HelpPaginator(List<List<RegisteredCommand>> commands, CommandSettings settings, CommandExecutionContext context) {
        this.translations = settings.getTranslations();
        this.errorEmbed = settings.getErrorEmbed();
        String prefix;
        if (context.wasExecutedInGuild()) {
            prefix = settings.getPrefixHandler().getPrefix(context.getGuild().getIdLong());
        } else {
            prefix = settings.getPrefixHandler().getPrefix(context.getJda().getSelfUser(), context.getAuthor());
        }
        PermissionCheckContext permissionCheck = new PermissionCheckContext(context.getJda(), context.getAuthor(), context.getGuild(), context.getMember(), context.getAlias());
        if (commands.size() == 1) {
            List<RegisteredCommand> page = commands.get(0)
                    .stream()
                    .filter(cmd ->
                            cmd.getDescription() != null & cmd.getUsage() != null && cmd.hasPermission(permissionCheck))
                    .collect(Collectors.toList());
            EmbedBuilder embed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), context.getAuthor());
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
        List<List<RegisteredCommand>> filteredPages = filterPages(commands,
                settings.getCommandsPerHelpPage(),
                cmd -> cmd.getDescription() != null && cmd.getUsage() != null && cmd.hasPermission(permissionCheck));
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < filteredPages.size(); i++) {
            List<RegisteredCommand> page = filteredPages.get(i);
            EmbedBuilder embed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), context.getAuthor());
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

    private List<List<RegisteredCommand>> filterPages(List<List<RegisteredCommand>> unfiltered, int pageSize,
                                                      Predicate<RegisteredCommand> filter) {
        List<List<RegisteredCommand>> filtered = unfiltered.stream()
                .map(list ->
                        list.stream().filter(filter).collect(Collectors.toList())
                ).collect(Collectors.toList());
        List<List<RegisteredCommand>> joined = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            List<RegisteredCommand> page = filtered.get(i);
            if (page.size() != pageSize) {
                int next = i + 1;
                if (next < filtered.size()) {
                    List<RegisteredCommand> nextPage = filtered.get(next);
                    int commandsToGet = (pageSize - page.size());
                    if (nextPage.size() < commandsToGet) {
                        page.addAll(nextPage);
                        joined.add(page);
                        i++;
                        continue;
                    }

                    for (int i1 = 0; i1 < commandsToGet; i1++) {
                        RegisteredCommand command = filtered.get(next).remove(i1);
                        page.add(command);
                    }
                }
            }
            joined.add(page);
        }
        return joined;
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
