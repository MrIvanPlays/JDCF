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
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class HelpPaginator {

    private final List<EmbedBuilder> pages;
    private final Supplier<EmbedBuilder> errorEmbed;
    private final Translations translations;

    public HelpPaginator(List<List<RegisteredCommand>> commands, CommandSettings settings, Member commandExecutor, User author, long guildId) {
        this.translations = settings.getTranslations();
        this.errorEmbed = settings.getErrorEmbed();
        String prefix = settings.getPrefixHandler().getPrefix(guildId);
        if (commands.size() == 1) {
            List<RegisteredCommand> page = commands.get(0)
                    .stream()
                    .filter(cmd ->
                            cmd.getDescription() != null & cmd.getUsage() != null && hasPermission(commandExecutor, cmd.getPermissions()))
                    .collect(Collectors.toList());
            EmbedBuilder embed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), author);
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
                cmd -> cmd.getDescription() != null && cmd.getUsage() != null && hasPermission(commandExecutor, cmd.getPermissions()));
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < filteredPages.size(); i++) {
            List<RegisteredCommand> page = filteredPages.get(i);
            EmbedBuilder embed = EmbedUtil.setAuthor(settings.getHelpCommandEmbed().get(), author);
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

    private boolean hasPermission(Member member, Permission[] permissions) {
        return permissions == null || member.hasPermission(permissions);
    }

    private List<List<RegisteredCommand>> filterPages(List<List<RegisteredCommand>> unfiltered, int pageSize,
                                                      Predicate<RegisteredCommand> filter) {
        List<List<RegisteredCommand>> filtered = new ArrayList<>();
        List<List<RegisteredCommand>> fillInCommands = new ArrayList<>();
        List<Integer> shouldSkip = new ArrayList<>();
        for (int i = 0; i < unfiltered.size(); i++) {
            if (shouldSkip.contains(i)) {
                continue;
            }
            int next = i + 1;
            List<RegisteredCommand> pageUnfiltered = unfiltered.get(i);
            if (fillInCommands.contains(pageUnfiltered)) {
                if (next >= unfiltered.size()) {
                    filtered.add(pageUnfiltered.stream().filter(filter).collect(Collectors.toList()));
                    continue;
                }
                List<RegisteredCommand> nextPage = unfiltered.get(next).stream()
                        .filter(filter).collect(Collectors.toList());
                int commandsToGet = pageSize - pageUnfiltered.size();
                if (nextPage.size() <= commandsToGet) {
                    pageUnfiltered.addAll(nextPage);
                    filtered.add(pageUnfiltered);
                    shouldSkip.add(next);
                    continue;
                }

                for (int i1 = 0; i1 < commandsToGet; i1++) {
                    RegisteredCommand command = nextPage.get(i1);
                    nextPage.remove(command);
                    pageUnfiltered.add(command);
                }
                filtered.add(pageUnfiltered);
                fillInCommands.add(nextPage);
                continue;
            }
            List<RegisteredCommand> pageFiltered = pageUnfiltered.stream()
                    .filter(filter).collect(Collectors.toList());
            if (pageFiltered.size() < pageSize) {
                if (i + 1 >= unfiltered.size()) {
                    filtered.add(pageFiltered);
                } else {
                    List<RegisteredCommand> nextPage = unfiltered.get(next).stream()
                            .filter(filter).collect(Collectors.toList());
                    int commandsToGet = pageSize - pageFiltered.size();
                    if (nextPage.size() <= commandsToGet) {
                        pageFiltered.addAll(nextPage);
                        filtered.add(pageFiltered);
                        shouldSkip.add(next);
                        continue;
                    }

                    for (int i1 = 0; i1 < commandsToGet; i1++) {
                        RegisteredCommand command = nextPage.get(i1);
                        nextPage.remove(command);
                        pageFiltered.add(command);
                    }
                    filtered.add(pageFiltered);
                    fillInCommands.add(nextPage);
                }
            } else {
                filtered.add(pageFiltered);
            }
        }
        fillInCommands.clear();
        return filtered;
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
