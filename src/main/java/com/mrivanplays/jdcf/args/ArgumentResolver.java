package com.mrivanplays.jdcf.args;

import java.util.function.Function;

public interface ArgumentResolver<T, P> {

    default ArgumentParsingState<P> checkIfArgumentEmpty(ArgumentResolverContext context,
                                                         Function<ArgumentResolverContext, ArgumentParsingState<P>> proceed) {
        if (context.getArgument().isEmpty()) {
            return ArgumentParsingStates.notPresent();
        }
        return proceed.apply(context);
    }

    ArgumentParsingState<P> tryParse(ArgumentResolverContext resolverContext);

    T parseNoTests(ArgumentResolverContext context);
}
