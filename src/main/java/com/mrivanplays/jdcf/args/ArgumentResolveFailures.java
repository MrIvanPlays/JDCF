package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ArgumentResolveFailures {

    @NotNull
    public static <D> ArgumentResolveFailure<D> notPresent() {
        return notPresent(null);
    }

    @NotNull
    public static <D> ArgumentResolveFailure<D> notPresent(@Nullable D additionalData) {
        return ArgumentResolveFailure.create("Argument not present", additionalData);
    }

    @NotNull
    public static <D> ArgumentResolveFailure<D> specifiedArgumentNotType(@NotNull Class<?> typeClass, int position) {
        return specifiedArgumentNotType(typeClass, position, null);
    }

    @NotNull
    public static <D> ArgumentResolveFailure<D> specifiedArgumentNotType(@NotNull Class<?> typeClass, int position, @Nullable D additionalData) {
        Objects.requireNonNull(typeClass, "typeClass");
        return ArgumentResolveFailure.create(
                "The specified argument at position '" +
                        position +
                        "' does not match type(s) '" +
                        typeClass.getSimpleName() +
                        "' or cannot be found",
                additionalData
        );
    }

    @NotNull
    public static <D> ArgumentResolveFailure<D> commandNotExecutedInGuild() {
        return ArgumentResolveFailure.create(
                "Argument retrieved's command is not executed in guild.",
                null
        );
    }

    // checks

    public static boolean isNotPresent(@NotNull ArgumentResolveFailure<?> failure) {
        Objects.requireNonNull(failure, "failure");
        return failure.getReasonString().contains("not present");
    }

    public static boolean isNotType(@NotNull ArgumentResolveFailure<?> failure) {
        Objects.requireNonNull(failure, "failure");
        return failure.getReasonString().contains("does not match type(s)");
    }
}
