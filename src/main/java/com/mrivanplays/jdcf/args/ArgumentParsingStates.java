package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import jdk.internal.jline.internal.Nullable;

public final class ArgumentParsingStates {

    public static <T> ArgumentParsingState<T> success() {
        return createParsingState("Successful", true, null);
    }

    public static <T> ArgumentParsingState<T> notPresent() {
        return createParsingState("Argument not present", false, null);
    }

    public static <D, CT> ArgumentParsingState<D> typeNotFound(Class<? extends CT> type) {
        return createParsingState(
                "Typed argument cannot be found for type '" + type.getSimpleName() + "'",
                false,
                null
        );
    }

    public static <T> ArgumentParsingState<T> commandNotExecutedInGuild() {
        return createParsingState(
                "Argument accessed in command, the command has not been executed in guild",
                false,
                null
        );
    }

    public static <T> ArgumentParsingState<T> notType() {
        return notType(null);
    }

    public static <T> ArgumentParsingState<T> notType(T data) {
        return createParsingState("Argument not type", false, data);
    }

    public static <T> ArgumentParsingState<T> createParsingState(@NotNull String message, boolean successful, T data) {
        Objects.requireNonNull(message, "message");
        return new ArgumentParsingState<T>() {
            @Override
            @NotNull
            public String getMessage() {
                return message;
            }

            @Override
            public boolean isSuccess() {
                return successful;
            }

            @Override
            @Nullable
            public T getDataStored() {
                return data;
            }
        };
    }

    public static boolean equals(ArgumentParsingState<?> checkedState, ArgumentParsingState<?> anotherState) {
        return checkedState.getMessage().equalsIgnoreCase(anotherState.getMessage());
    }
}
