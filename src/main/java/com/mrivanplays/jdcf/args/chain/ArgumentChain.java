package com.mrivanplays.jdcf.args.chain;

import com.mrivanplays.jdcf.args.ArgumentHolder;
import com.mrivanplays.jdcf.args.ArgumentResolveFailContext;
import com.mrivanplays.jdcf.args.ArgumentResolver;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ArgumentChain {

    private final ArgumentHolder holderParent;
    private List<Object> arguments;
    private List<ArgumentResolveFailContext<?>> argumentFailures;

    public ArgumentChain(@NotNull ArgumentHolder holderParent) {
        this.holderParent = Objects.requireNonNull(holderParent, "holderParent");
        arguments = new ArrayList<>();
        argumentFailures = new ArrayList<>();
    }

    @NotNull
    public <T, D> ArgumentChain resolveArgumentToType(@NotNull ArgumentResolver<T, D> resolver, int argumentNumber) {
        holderParent.getArgument(resolver, argumentNumber)
                .ifPresent(type -> arguments.set(argumentNumber, type))
                .ifNotPresent(failure -> argumentFailures.set(argumentNumber, failure))
                .execute();
        return this;
    }

    public void handleExecution(@NotNull Consumer<ChainArgumentHolder> handler) {
        handler.accept(new ChainArgumentHolder(arguments, argumentFailures));
    }
}
