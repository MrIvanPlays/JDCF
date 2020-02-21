package com.mrivanplays.jdcf.translation;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents a translation file
 */
public interface TranslationFile {

    /**
     * Returns the translation with the specified key, or empty if the translation doesn't exist.
     *
     * @param key key
     * @return translation or empty string
     */
    @NotNull
    String getString(String key);

    /**
     * Returns the translation file type.
     *
     * @return file type
     */
    @NotNull
    String getFileType();

    /**
     * Returns the keys of all translations present
     *
     * @return keys
     */
    @NotNull
    Collection<String> getKeys();
}
