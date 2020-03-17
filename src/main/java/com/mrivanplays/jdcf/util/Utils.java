package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class Utils {

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> boolean contains(T value, T[] array) {
        for (T t : array) {
            if (t.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T> List<List<T>> getPages(Collection<T> c, int pageSize) {
        List<T> list = new ArrayList<>(c);
        if (pageSize <= 0 || pageSize > list.size()) {
            pageSize = list.size();
        }
        int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
        List<List<T>> pages = new ArrayList<>(numPages);
        for (int pageNum = 0; pageNum < numPages; ) {
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        }
        return pages;
    }

    public static EmbedBuilder setAuthor(Supplier<EmbedBuilder> embed, User author) {
        return embed.get().setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl());
    }
}
