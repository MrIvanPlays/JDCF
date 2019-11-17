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
package com.mrivanplays.jdcf.settings;

import com.mrivanplays.jdcf.settings.prefix.DefaultPrefixHandler;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.Executors;

public class DefaultCommandSettings {

    private static final CommandSettings settings;

    static {
        settings = new CommandSettings();
        settings.setEnableHelpCommand(false);
        settings.setEnableMentionInsteadPrefix(true);
        settings.setEnablePrefixCommand(true);
        settings.setExecutorService(Executors.newSingleThreadScheduledExecutor());
        settings.setNoPermissionEmbed(
                () -> new EmbedBuilder().setColor(Color.RED).setTimestamp(Instant.now())
                        .setTitle("Insufficient permissions")
                        .setDescription("You don't have permission to perform this command."));
        settings.setPrefixCommandEmbed(() -> new EmbedBuilder().setColor(Color.BLUE).setTimestamp(Instant.now()).setTitle("Prefix"));
        settings.setErrorEmbed(() -> new EmbedBuilder().setTimestamp(Instant.now()).setColor(Color.RED).setTitle("Error"));
        settings.setSuccessEmbed(() -> new EmbedBuilder().setTimestamp(Instant.now()).setColor(Color.GREEN).setTitle("Success"));
        settings.setPrefixHandler(new DefaultPrefixHandler(settings.getExecutorService()));
    }

    public static CommandSettings get() {
        return settings;
    }
}
