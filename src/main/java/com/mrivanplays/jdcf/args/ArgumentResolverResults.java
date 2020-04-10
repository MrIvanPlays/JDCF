package com.mrivanplays.jdcf.args;

public final class ArgumentResolverResults {

    public static <T, D> ArgumentResolverResult<T, D> success(T value) {
        return ArgumentResolverResult.create(value, null);
    }

    public static <T, D> ArgumentResolverResult<T, D> valueNotPresent() {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.notPresent());
    }

    public static <T, D> ArgumentResolverResult<T, D> valueNotType(Class<?> typeClass, int position) {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.specifiedArgumentNotType(typeClass, position));
    }

    public static <T, D> ArgumentResolverResult<T, D> commandNotExecutedInGuild() {
        return ArgumentResolverResult.create(null, ArgumentResolveFailures.commandNotExecutedInGuild());
    }
}
