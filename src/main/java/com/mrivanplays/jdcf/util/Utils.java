package com.mrivanplays.jdcf.util;

public class Utils {

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }
}
