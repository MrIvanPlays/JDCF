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
package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.settings.CommandSettings;
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

    HelpPaginator(List<RegisteredCommand> commands, CommandSettings settings, Member commandExecutor, User author) {
        List<EmbedBuilder> pagesWithCommands = new ArrayList<>();
        List<RegisteredCommand> filteredCommands = commands.stream()
                .filter(cmd -> cmd.getDescription() != null && cmd.getUsage() != null && hasPermission(commandExecutor, cmd.getPermissions()))
                .collect(Collectors.toList());
        for (int i = 0; i < commands.size(); i += settings.getCommandsPerHelpPage()) {
            EmbedBuilder embed = settings.getHelpCommandEmbed().get();
            for (RegisteredCommand in : filteredCommands.subList(i, Math.min(i + settings.getCommandsPerHelpPage(), filteredCommands.size()))) {
                embed.addField("`" + in.getUsage() + "`", in.getDescription(), false);
            }
            if (embed.getFields().isEmpty()) {
                continue;
            }
            pagesWithCommands.add(embed);
        }
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < pagesWithCommands.size(); i++) {
            EmbedBuilder embed = pagesWithCommands.get(i);
            pages.add(EmbedUtil.setAuthor(embed, author).setDescription("Page " + (i + 1) + "/" + pagesWithCommands.size()));
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
            return errorEmbed.get().setDescription("Page #" + page + " does not exist");
        }
    }

    boolean hasNext(int current) {
        return pages.size() > current;
    }
}
