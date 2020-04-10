package com.mrivanplays.jdcf.args.chain;

import com.mrivanplays.jdcf.args.ArgumentResolveFailContext;

import java.util.List;

// work in progress
public class ChainArgumentHolder {

    private List<Object> arguments;
    private List<ArgumentResolveFailContext<?>> argumentFailures;

    public ChainArgumentHolder(List<Object> arguments, List<ArgumentResolveFailContext<?>> argumentFailures) {
        this.arguments = arguments;
        this.argumentFailures = argumentFailures;
    }

    public <T> T getArgument(Class<? extends T> argumentType, int argumentNumber) {
        if (!isArgumentSuccessful(argumentNumber)) {
            return null;
        }
        try {
            return argumentType.cast(arguments.get(argumentNumber));
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getStringArgument(int argumentNumber) {
        return getArgument(String.class, argumentNumber);
    }

    public boolean isArgumentSuccessful(int argumentNumber) {
        try {
            return arguments.get(argumentNumber) != null;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public <T> ArgumentResolveFailContext<T> getArgumentFailContext(int argumentNumber) {
        if (isArgumentSuccessful(argumentNumber)) {
            return null;
        }
        try {
            return (ArgumentResolveFailContext<T>) argumentFailures.get(argumentNumber);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
