package com.mrivanplays.jdcf.args;

public final class ArgumentResolveFailures {

    public static <D> ArgumentResolveFailure<D> notPresent() {
        return ArgumentResolveFailure.create("Argument not present", null);
    }

    public static <D> ArgumentResolveFailure<D> specifiedArgumentNotType(Class<?> typeClass, int position) {
        return ArgumentResolveFailure.create(
                "The specified argument at position '" +
                        position +
                        "' does not match type(s) '" +
                        typeClass.getSimpleName() +
                        "' or cannot be found",
                null
        );
    }

    public static <D> ArgumentResolveFailure<D> commandNotExecutedInGuild() {
        return ArgumentResolveFailure.create(
                "Argument retrieved's command is not executed in guild.",
                null
        );
    }

    // checks

    public static boolean isNotPresent(ArgumentResolveFailure<?> failure) {
        return failure.getReasonString().contains("not present");
    }

    public static boolean isNotType(ArgumentResolveFailure<?> failure) {
        return failure.getReasonString().contains("does not match type(s)");
    }
}
