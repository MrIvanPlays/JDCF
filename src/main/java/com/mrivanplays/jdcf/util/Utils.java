package com.mrivanplays.jdcf.util;

public class Utils {

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> boolean contains(T value, T[] array) {
        for (T t : array) {
            if (t.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
