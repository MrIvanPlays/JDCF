package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class EmbedUtil {

    public static EmbedBuilder setAuthor(EmbedBuilder embed, User author) {
        return embed.setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl());
    }
}
