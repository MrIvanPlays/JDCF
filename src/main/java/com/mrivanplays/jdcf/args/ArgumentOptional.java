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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a optional which contains things for our purpose and that's arguments.
 *
 * @param <T> argument type
 */
public final class ArgumentOptional<T>
{

    /**
     * Creates a new argument optional. If the value given is null, the optional will be empty.
     *
     * @param value      the value of which you want argument optional
     * @param failReason the fail reason of why this argument optional would fail
     * @param <T>        argument type
     * @return argument optional if value not null, empty argument optional else
     */
    public static <T> ArgumentOptional<T> of(@Nullable T value, @NotNull FailReason failReason)
    {
        return value != null ? new ArgumentOptional<>(value, failReason) : new ArgumentOptional<>(null, failReason);
    }

    private final T value;
    private final FailReason failReason;

    private ArgumentOptional(@Nullable T value, @NotNull FailReason failReason)
    {
        this.value = value;
        this.failReason = failReason;
    }

    /**
     * If value is being present, the executor will get executed and the return value will don't come
     * in work, otherwise the return value, a <i>rest</i> argument action will execute its {@link
     * RestArgumentAction#orElse(Consumer)} method if it was called.
     *
     * @param action executor of the argument
     * @return a <i>rest</i> argument action
     */
    @NotNull
    public RestArgumentAction ifPresent(@NotNull Consumer<T> action)
    {
        if (isPresent())
        {
            action.accept(value);
        }
        return new RestArgumentAction(failReason);
    }

    /**
     * Leads the specified argument to a new argument.
     *
     * @param mapper mapper for converting the current argument to another
     * @param <U>    new argument type
     * @return argument optional with the new argument if present or a empty optional if the value was
     * not present.
     */
    @NotNull
    public <U> ArgumentOptional<U> map(@NotNull Function<T, U> mapper)
    {
        Objects.requireNonNull(mapper, "mapper");
        if (isPresent())
        {
            U newValue = mapper.apply(value);
            if (newValue == null)
            {
                return ArgumentOptional.of(null, FailReason.ARGUMENT_PARSED_NULL);
            }
            return ArgumentOptional.of(newValue, failReason);
        } else
        {
            return ArgumentOptional.of(null, failReason);
        }
    }

    /**
     * Returns whenever the value is present.
     *
     * @return <code>true</code> if value present, <code>false</code> otherwise
     */
    public boolean isPresent()
    {
        return value != null;
    }

    /**
     * Gets the specified value if present. If the value is not present, the method will throw a
     * {@link NullPointerException}. It is required to use instead {@link #ifPresent(Consumer)} to
     * access the value, which also provides you handling when the value is not present.
     *
     * @return value if present
     * @throws NullPointerException if value not present
     */
    @NotNull
    public T get()
    {
        Objects.requireNonNull(value, "Optional is empty");
        return value;
    }
}
