package com.mrivanplays.jdcf.args;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArgumentParsingState<T> {

    @NotNull
    String getMessage();

    boolean isSuccess();

    @Nullable
    T getDataStored();
}
