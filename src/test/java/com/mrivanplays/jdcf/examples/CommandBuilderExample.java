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
package com.mrivanplays.jdcf.examples;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;

public class CommandBuilderExample {

    /**
     * This is example only for understanding the command builder API. It isn't meant to be used in any bot.
     */
    public void main() {
        JDA jda = null; // set jda instance
        CommandManager commandManager = new CommandManager(jda);
        Command.builder()
                .name("test")
                .description("This is the description")
                .usage("test")
                .aliases("testing", "t", "test")
                .permissions(Permission.MANAGE_CHANNEL)
                .executor((context, args) -> {
                    context.getChannel().sendMessage("The performed test was successful.").queue();
                    return true;
                })
                .buildAndRegister(commandManager);
    }
}
