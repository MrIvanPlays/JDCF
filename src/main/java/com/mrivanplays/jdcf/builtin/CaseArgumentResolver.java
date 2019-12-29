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
