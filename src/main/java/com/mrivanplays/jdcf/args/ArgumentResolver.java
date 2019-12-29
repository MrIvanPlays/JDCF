package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents a argument resolver which resolves a single argument into a type specified.
 *
 * <p>This is a functional interface whose abstract method is {@link #resolve(ArgumentResolverContext)}
 *
 * @param <T> resolved to type
 */
@FunctionalInterface
public interface ArgumentResolver<T> {

    /**
     * Resolves the input argument into the type this resolver resolves. This method may throw exceptions which will
     * trigger {@link RestArgumentAction#orElse(Consumer)} with {@link FailReason} of
     * <code>ARGUMENT_PARSED_NOT_TYPE</code> if used upon {@link CommandArguments#next(ArgumentResolver)}.
     *
     * @param context context containing data about the argument
     * @return a resolved argument, or null.
     */
    @Nullable
    T resolve(@NotNull ArgumentResolverContext context) throws Exception;
}
