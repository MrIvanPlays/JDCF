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
        orElse((fr, $) -> toRun.accept(fr));
    }

    /**
     * The specified {@link BiConsumer} gets invoked with the {@link FailReason} and the specified {@link String}
     * argument. Depending on the fail reason, the argument may or may not be null.
     *
     * @param toRun the action to run
     */
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
