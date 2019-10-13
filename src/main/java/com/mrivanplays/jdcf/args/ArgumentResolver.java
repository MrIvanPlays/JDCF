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
import org.jetbrains.annotations.Nullable;

/**
 * Represents a argument resolver which resolves a single argument into a type specified.
 *
 * <p>This is a functional interface whose abstract method is {@link #resolve(ArgumentResolverContext)}
 *
 * @param <T> resolved to type
 */
@FunctionalInterface
public interface ArgumentResolver<T>
{

    /**
     * Resolves the input argument into the type this resolver resolves. This method may throw
     * exceptions which will trigger {@link RestArgumentAction#orElse(Consumer)} with {@link
     * FailReason} of <code>ARGUMENT_PARSED_NOT_TYPE</code> if used upon {@link
     * CommandArguments#next(ArgumentResolver)}.
     *
     * @param context context containing data about the argument
     * @return a resolved argument, or null.
     */
    @Nullable
    T resolve(@NotNull ArgumentResolverContext context) throws Exception;
}
