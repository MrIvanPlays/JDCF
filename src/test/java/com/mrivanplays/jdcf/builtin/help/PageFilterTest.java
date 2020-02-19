package com.mrivanplays.jdcf.builtin.help;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.RegisteredCommand;
import com.mrivanplays.jdcf.util.Utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PageFilterTest {

    @Test
    public void performTest() {
        List<RegisteredCommand> registeredCommands = new ArrayList<>();
        registeredCommands.add(Command.builder().name("foo").executor((context, arg) -> true).build());
        registeredCommands.add(Command.builder().name("bar").executor((context, arg) -> true).build());
        registeredCommands.add(Command.builder().name("baz").executor((context, arg) -> true).build());

        List<List<RegisteredCommand>> pages = Utils.getPages(registeredCommands, 1);

        List<List<RegisteredCommand>> filteredPages = filterPages(pages, 1, command -> !command.getName().equalsIgnoreCase("baz"));

        Assert.assertEquals(2, filteredPages.size());
        Assert.assertEquals(1, filteredPages.get(0).size());
        Assert.assertEquals(1, filteredPages.get(1).size());
    }

    private List<List<RegisteredCommand>> filterPages(List<List<RegisteredCommand>> unfiltered, int pageSize,
                                                      Predicate<RegisteredCommand> filter) {
        List<List<RegisteredCommand>> filtered = unfiltered.stream()
                .map(list ->
                        list.stream().filter(filter).collect(Collectors.toList())
                ).filter(list -> !list.isEmpty())
                .collect(Collectors.toList());
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
                        if (filtered.get(next).size() == i1) {
                            continue;
                        }
                        RegisteredCommand command = filtered.get(next).remove(i1);
                        page.add(command);
                    }
                }
            }
            joined.add(page);
        }
        return joined;
    }
}
