package com.mrivanplays.jdcf.args;

import java.util.function.Function;

@FunctionalInterface
public interface ArgumentResolver<T, D> {

    default ArgumentResolverResult<T, D> checkIfArgumentEmpty(
            ArgumentResolveContext context,
            Function<ArgumentResolveContext, ArgumentResolverResult<T, D>> proceed
    ) {
        if (context.getArgument().isEmpty()) {
            return ArgumentResolverResults.valueNotPresent();
        }
        return proceed.apply(context);
    }

    ArgumentResolverResult<T, D> parse(ArgumentResolveContext context);
}
