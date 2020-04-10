package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArgumentResolverResults {

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> success(T value) {
        return ArgumentResolverResult.create(value, null);
    }

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> valueNotPresent() {
        return valueNotPresent(null);
    }

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> valueNotPresent(@Nullable D additionalData) {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.notPresent(additionalData));
    }

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> valueNotType(@NotNull Class<?> typeClass, int position) {
        return valueNotType(typeClass, position, null);
    }

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> valueNotType(@NotNull Class<?> typeClass, int position, @Nullable D data) {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.specifiedArgumentNotType(typeClass, position, data));
    }

    @NotNull
    public static <T, D> ArgumentResolverResult<T, D> commandNotExecutedInGuild() {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.commandNotExecutedInGuild());
    }
}
