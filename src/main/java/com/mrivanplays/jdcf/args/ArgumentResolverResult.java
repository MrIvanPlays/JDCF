package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ArgumentResolverResult<T, D> {

    static <T, D> ArgumentResolverResult<T, D> create(@Nullable T value, @Nullable ArgumentResolveFailure<D> failureReason) {
        if (value == null && failureReason == null) {
            throw new IllegalArgumentException("Parsed value is null but there's no failure reason.");
        }
        Optional<T> valueOptional = value == null ? Optional.empty() : Optional.of(value);
        return new ArgumentResolverResult<T, D>() {
            @Override
            public Optional<T> getValue() {
                return valueOptional;
            }

            @Override
            public @Nullable ArgumentResolveFailure<D> getFailReason() {
                return failureReason;
            }
        };
    }

    Optional<T> getValue();

    @Nullable
    ArgumentResolveFailure<D> getFailReason();
}
