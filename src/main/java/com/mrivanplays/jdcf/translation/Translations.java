package com.mrivanplays.jdcf.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Represents a class, holding translations for a specific language.
 */
public class Translations {

    private ResourceBundle resourceBundle;

    private Translations(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Retrieves a new {@link PropertyResourceBundle} {@link Translations}
     *
     * @param in a ".properties" resource's input stream
     * @return translations
     * @throws IOException if an i/o error occurs
     */
    @NotNull
    public static Translations get(@NotNull InputStream in) throws IOException {
        Objects.requireNonNull(in, "in");
        return new Translations(new PropertyResourceBundle(in));
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
        String translation = "<translation " + key + " missing>";
        String bundle = resourceBundle.getString(key);
        if (!bundle.isEmpty()) {
            translation = bundle;
        }
        if (args == null) {
            return translation;
        } else {
            return MessageFormat.format(translation, args);
        }
    }
}
