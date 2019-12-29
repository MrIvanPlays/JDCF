package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;

/**
 * Represents a optional which contains things for our purpose and that's arguments.
 *
 * @param <T> argument type
 */
public final class ArgumentOptional<T> {

    /**
     * Creates a new argument optional. If the value given is null, the optional will be empty.
     *
     * @param value      the value of which you want argument optional
     * @param failReason the fail reason of why this argument optional would fail
     * @param argument   argument typed. Can be null
     * @param <T>        argument type
     * @return argument optional if value not null, empty argument optional else
     */
    public static <T> ArgumentOptional<T> of(@Nullable T value, @NotNull FailReason failReason, @Nullable String argument) {
        return new ArgumentOptional<>(value, failReason, argument);
    }

    private final T value;
    private final FailReason failReason;
    private String argument;

    private ArgumentOptional(@Nullable T value, @NotNull FailReason failReason, @Nullable String argument) {
        this.value = value;
        this.failReason = failReason;
        this.argument = argument;
    }

    /**
     * If value is being present, the executor will get executed and the return value will don't come in work, otherwise
     * the return value, a <i>rest</i> argument action will execute its {@link RestArgumentAction#orElse(Consumer)}
     * method if it was called.
     *
     * @param action executor of the argument
     * @return a <i>rest</i> argument action
     */
    @NotNull
    @CheckReturnValue
    public RestArgumentAction ifPresent(@NotNull Consumer<T> action) {
        Objects.requireNonNull(action, "action");
        if (isPresent()) {
            action.accept(value);
        }
        return new RestArgumentAction(failReason, argument);
    }

    /**
     * Leads the specified argument to a new argument.
     *
     * @param mapper mapper for converting the current argument to another
     * @param <U>    new argument type
     * @return argument optional with the new argument if present or a empty optional if the value was not present.
     */
    @NotNull
    public <U> ArgumentOptional<U> map(@NotNull Function<T, U> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        if (isPresent()) {
            U newValue = mapper.apply(value);
            if (newValue == null) {
                return ArgumentOptional.of(null, FailReason.ARGUMENT_PARSED_NULL, argument);
            }
            return ArgumentOptional.of(newValue, failReason, argument);
        } else {
            return ArgumentOptional.of(null, failReason, argument);
        }
    }

    /**
     * Returns whenever the value is present.
     *
     * @return <code>true</code> if value present, <code>false</code> otherwise
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Gets the specified value if present. If the value is not present, the method will throw a {@link
     * NullPointerException}. It is required to use instead {@link #ifPresent(Consumer)} to access the value, which also
     * provides you handling when the value is not present.
     *
     * @return value if present
     * @throws NullPointerException if value not present
     */
    @NotNull
    public T get() {
        Objects.requireNonNull(value, "Optional is empty");
        return value;
    }
}
