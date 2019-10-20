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
import com.mrivanplays.jdcf.util.EmbedUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HelpPaginator {

    private final List<EmbedBuilder> pages;
    private final Supplier<EmbedBuilder> errorEmbed;

    public HelpPaginator(List<RegisteredCommand> commands, int listedCommandsPerPage, Supplier<EmbedBuilder> helpCommandEmbed,
                         Member commandExecutor, User author, Supplier<EmbedBuilder> errorEmbed) {
        List<EmbedBuilder> pagesWithCommands = new ArrayList<>();
        for (int i = 0; i < commands.size(); i += listedCommandsPerPage) {
            EmbedBuilder embed = helpCommandEmbed.get();
            for (RegisteredCommand includedInThisEmbed : commands.subList(i, Math.min(i + listedCommandsPerPage, commands.size()))) {
                String description = includedInThisEmbed.getDescription();
                String usage = includedInThisEmbed.getUsage();
                if (description == null || usage == null) {
                    continue;
                }
                if (includedInThisEmbed.getName().equalsIgnoreCase("help")) {
                    continue;
                }
                if (!hasPermission(commandExecutor, includedInThisEmbed.getPermissions())) {
                    continue;
                }
                embed.addField("`" + usage + "`", description, false);
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
        this.errorEmbed = errorEmbed;
        pagesWithCommands.clear();
    }

    private boolean hasPermission(Member member, Permission[] permissions) {
        return permissions == null || member.hasPermission(permissions);
    }

    public EmbedBuilder getPage(int page) {
        try {
            return pages.get(page - 1);
        } catch (IndexOutOfBoundsException e) {
            return errorEmbed.get().setDescription("Page #" + page + " does not exist");
        }
    }

    public boolean hasNext(int current) {
        return pages.size() > current;
    }
}
