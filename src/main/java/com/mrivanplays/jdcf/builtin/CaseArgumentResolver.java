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
package com.mrivanplays.jdcf.builtin;

import com.mrivanplays.jdcf.args.ArgumentResolver;
import com.mrivanplays.jdcf.args.ArgumentResolverContext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a cased {@link ArgumentResolver}. Works by retrieving the specified argument's specified case in the
 * specified map. It isn't recommended to use this if you have only 1 case as the JVM will allocate more memory because
 * of the map.
 *
 * @param <T> argument to resolve to
 */
public final class CaseArgumentResolver<T> implements ArgumentResolver<T> {

    private Map<String, T> cases;

    public CaseArgumentResolver(Map<String, T> cases) {
        this.cases = cases;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public T resolve(@NotNull ArgumentResolverContext context) throws Exception {
        T t = cases.get(context.getArgument());
        if (t == null) {
            throw new IllegalArgumentException();
        } else {
            return t;
        }
    }
}
