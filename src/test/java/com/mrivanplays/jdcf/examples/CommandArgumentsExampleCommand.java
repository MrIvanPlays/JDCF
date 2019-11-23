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
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.ArgumentResolvers;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;

import org.jetbrains.annotations.NotNull;

/**
 * This is example of understanding the command argument handler JDCF has. It isn't meant to be used in any bot.
 */
public class CommandArgumentsExampleCommand extends Command {

    public CommandArgumentsExampleCommand() {
        super("pm");
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        args.nextString().ifPresent(message -> {
            args.next(ArgumentResolvers.USER).ifPresent(user -> {
                user.openPrivateChannel().queue(channel -> channel.sendMessage(context.getMember().getUser().getName() + " messaged you: " + message).queue());
            }).orElse(failReason -> {
                if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                    context.getChannel().sendMessage("You should also provide a user either by mentioning him or passing his user id.").queue();
                }
                if (failReason == FailReason.ARGUMENT_PARSED_NOT_TYPE) {
                    context.getChannel().sendMessage("Invalid user").queue();
                }
            });
        }).orElse(failReason -> {
            if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                context.getChannel().sendMessage("Usage: pm <one-word-message> <user>").queue();
            }
        });
        return true;
    }
}
