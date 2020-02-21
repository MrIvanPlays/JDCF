package com.mrivanplays.jdcf.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Represents a class, holding translations for a specific language.
 */
public class Translations {

    private TranslationFile translationFile;
    private String language;

    private Translations(TranslationFile translationFile, String language) {
        this.translationFile = translationFile;
        this.language = language;
    }

    /**
     * Retrieves a new {@link Translations} instance with the specified {@link TranslationFile}
     *
     * @param translationFile translation file
     * @param language language of the translations
     * @return translations
     */
    @NotNull
    public static Translations get(@NotNull TranslationFile translationFile, @NotNull String language) {
        Objects.requireNonNull(translationFile, "translationFile");
        Objects.requireNonNull(language, "language");
        return new Translations(translationFile, language);
    }

    /**
     * Retrieves a new {@link Translations} which are bound to the ".properties" file type.
     *
     * @param in a ".properties" resource's input stream
     * @param language the language of these translations
     * @return translations
     * @throws IOException if an i/o error occurs
     */
    @NotNull
    public static Translations get(@NotNull InputStream in, @NotNull String language) throws IOException {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(language, "language");
        return new Translations(new PropertyTranslationFile(in), language);
    }

    /**
     * Retrieves a new {@link Translations} which are bound to the ".properties" file type.
     *
     * @param reader a ".properties" resource's reader
     * @param language the language of these translations
     * @return translations
     * @throws IOException if an i/o error occurs
     */
    @NotNull
    public static Translations get(@NotNull Reader reader, @NotNull String language) throws IOException {
        Objects.requireNonNull(reader, "reader");
        Objects.requireNonNull(language, "language");
        return new Translations(new PropertyTranslationFile(reader), language);
    }

    /**
     * Retrieves the translation, held by the specified key.
     *
     * @param key the key of the translation you want to get
     * @param args the arguments to replace in the translation
     * @return translation, or a message saying that it is missing
     */
    @NotNull
    public String getTranslation(@NotNull String key, @Nullable Object... args) {
        Objects.requireNonNull(key, "key");
        String translation = String.format("Translation [%s][%s] does not exist", language, key);
        String bundle = translationFile.getString(key);
        if (!bundle.isEmpty()) {
            translation = bundle;
        }
        return args == null ? translation : MessageFormat.format(translation, args);
    }

    /**
     * Returns a immutable collection of the translation keys
     *
     * @return keys
     */
    @NotNull
    public Collection<String> getKeys() {
        return translationFile.getKeys();
    }

    /**
     * Returns the file type, where the translations are stored.
     *
     * @return file type
     */
    @NotNull
    public String getTranslationFileType() {
        return translationFile.getFileType();
    }

    /**
     * Returns the language of the translations held
     *
     * @return language
     */
    @NotNull
    public String getLanguage() {
        return language;
    }
}
