package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface ArgumentResolveFailure<D> {

    static <D> ArgumentResolveFailure<D> create(@NotNull String reasonString, @Nullable D additionalData) {
        Objects.requireNonNull(reasonString, "reasonString");
        return new ArgumentResolveFailure<D>() {
            @Override
            public @NotNull String getReasonString() {
                return reasonString;
            }

            @Nullable
            @Override
            public D getAdditionalData() {
                return additionalData;
            }
        };
    }

    @NotNull
    String getReasonString();

    @Nullable
    D getAdditionalData();
}
