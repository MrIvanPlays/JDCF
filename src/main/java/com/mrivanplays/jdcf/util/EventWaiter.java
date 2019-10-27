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
package com.mrivanplays.jdcf.util;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class EventWaiter implements EventListener {

    private final Set<Waiting> waitings = new HashSet<>();
    private final ScheduledExecutorService executor;

    public EventWaiter(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public <T> void waitFor(Class<T> event, Predicate<T> filter, Consumer<T> onFilterPassed, Runnable whenTimedOut) {
        Waiting<T> waiting = new Waiting<>(event, filter, onFilterPassed);
        waitings.add(waiting);

        executor.schedule(() -> {
            waitings.remove(waiting);
            whenTimedOut.run();
        }, 5, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(@Nonnull GenericEvent genericEvent) {
        List<Waiting> toRemove = new ArrayList<>();
        for (Waiting waiting : waitings) {
            if (genericEvent.getClass().isAssignableFrom(waiting.waitingEvent)) {
                if (waiting.filter.test(genericEvent)) {
                    toRemove.add(waiting);
                    waiting.onFilterPassed.accept(genericEvent);
                }
            }
        }
        for (Waiting forRemove : toRemove) {
            waitings.remove(forRemove);
        }
        toRemove.clear();
    }

    private static class Waiting<T> {

        final Class<T> waitingEvent;
        final Predicate<T> filter;
        final Consumer<T> onFilterPassed;

        Waiting(Class<T> waitingEvent, Predicate<T> filter, Consumer<T> onFilterPassed) {
            this.waitingEvent = waitingEvent;
            this.filter = filter;
            this.onFilterPassed = onFilterPassed;
        }
    }
}
