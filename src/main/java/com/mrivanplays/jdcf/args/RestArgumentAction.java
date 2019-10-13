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

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a <i>rest</i> action, which comes role when the argument you wanted to get back in the
 * code chain is null. This provides a {@link #orElse(Consumer)} method, which is being called only
 * when there was no value present back.
 */
public final class RestArgumentAction
{

    private final FailReason failReason;

    public RestArgumentAction(FailReason failReason)
    {
        this.failReason = failReason;
    }

    /**
     * Returns whenever the value was present.
     *
     * @return <code>true</code> if present, <code>false</code> otherwise
     */
    public boolean wasValuePresent()
    {
        return failReason == FailReason.NO_FAIL_REASON;
    }

    /**
     * The specified {@link Consumer} gets invoked with the {@link FailReason} when the value wasn't
     * present.
     *
     * @param consumer the consumer to run
     */
    public void orElse(@NotNull Consumer<FailReason> consumer)
    {
        if (failReason != FailReason.NO_FAIL_REASON)
        {
            consumer.accept(failReason);
        }
    }

    /**
     * The specified {@link Runnable} gets invoked whatever the {@link FailReason} was when the value
     * wasn't present.
     *
     * @param runnable the runnable to run
     */
    public void orElse(@NotNull Runnable runnable)
    {
        orElse(failReason -> runnable.run());
    }
}
