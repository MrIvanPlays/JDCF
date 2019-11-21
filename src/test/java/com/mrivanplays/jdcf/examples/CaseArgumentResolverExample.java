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
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.builtin.CaseArgumentResolver;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This is example of understanding the CaseArgumentResolver usage. It isn't meant to be used in any bot.
 */
public class CaseArgumentResolverExample extends Command {

    private CaseArgumentResolver<ExampleCommandArgument> argumentResolver;

    public CaseArgumentResolverExample() {
        super("example");
        Map<String, ExampleCommandArgument> caseMap = new HashMap<>();
        caseMap.put("teamTreesRequest", new ExampleCommandArgument(false, "https://teamtrees.org"));
        caseMap.put("googleRequest", new ExampleCommandArgument(false, "https://google.com"));
        caseMap.put("googleSearchRequest", new ExampleCommandArgument(true, "https://google.com"));
        argumentResolver = new CaseArgumentResolver<>(caseMap);
    }

    @Override
    public void execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        args.next(argumentResolver).ifPresent(argument -> {
            String url = argument.getUrl();
            boolean performSearch = argument.isPerformSearch();
            // command logic handling
        }).orElse(failReason -> {
            // fail reason handling
        });
    }

    static class ExampleCommandArgument {

        private boolean performSearch;
        private String url;

        public ExampleCommandArgument(boolean performSearch, String url) {
            this.performSearch = performSearch;
            this.url = url;
        }

        public boolean isPerformSearch() {
            return performSearch;
        }

        public String getUrl() {
            return url;
        }
    }
}
