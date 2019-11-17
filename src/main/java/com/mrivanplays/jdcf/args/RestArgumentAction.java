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
package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a <i>rest</i> action, which comes role when the argument you wanted to get back in the code chain is null.
 * This provides a {@link #orElse(Consumer)} method, which is being called only when there was no value present back.
 */
public final class RestArgumentAction {

    private final FailReason failReason;
    private String argument;

    public RestArgumentAction(FailReason failReason, String argument) {
        this.failReason = failReason;
        this.argument = argument;
    }

    /**
     * Returns whenever the value was present.
     *
     * @return <code>true</code> if present, <code>false</code> otherwise
     */
    public boolean wasValuePresent() {
        return failReason == FailReason.NO_FAIL_REASON;
    }

    /**
     * The specified {@link Consumer} gets invoked with the {@link FailReason} when the value wasn't present.
     *
     * @param toRun the action to run
     */
    public void orElse(@NotNull Consumer<FailReason> toRun) {
        Objects.requireNonNull(toRun, "toRun");
        if (failReason != FailReason.NO_FAIL_REASON) {
            toRun.accept(failReason);
        }
    }

    public void orElse(@NotNull BiConsumer<FailReason, String> toRun) {
        Objects.requireNonNull(toRun, "toRun");
        if (failReason != FailReason.NO_FAIL_REASON) {
            toRun.accept(failReason, argument);
        }
    }

    /**
     * The specified {@link Runnable} gets invoked whatever the {@link FailReason} was when the value wasn't present.
     *
     * @param runnable the runnable to run
     */
    public void orElse(@NotNull Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        orElse(failReason -> runnable.run());
    }
}
