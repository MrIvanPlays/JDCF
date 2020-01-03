package com.mrivanplays.jdcf.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
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

    private ResourceBundle resourceBundle;
    private String language;

    private Translations(ResourceBundle resourceBundle, String language) {
        this.resourceBundle = resourceBundle;
        this.language = language;
    }

    /**
     * Retrieves a new {@link PropertyResourceBundle} {@link Translations}
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
        return new Translations(new PropertyResourceBundle(in), language);
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
        String translation = "<translation '" + key + "' missing>";
        String bundle = resourceBundle.getString(key);
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
        Collection<String> mutable = new ArrayList<>();
        Enumeration<String> keyEnumeration = resourceBundle.getKeys();
        while (keyEnumeration.hasMoreElements()) {
            mutable.add(keyEnumeration.nextElement());
        }
        return Collections.unmodifiableCollection(mutable);
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
