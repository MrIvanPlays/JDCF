package com.mrivanplays.jdcf.translation;

import com.mrivanplays.jdcf.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Represents a collector of all known translations.
 */
public class TranslationCollector {

    private static TranslationCollector instance = null;

    public static TranslationCollector getInstance() {
        if (instance == null) {
            instance = new TranslationCollector();
        }
        return instance;
    }

    private TranslationCollector() {
    }

    private final String[] supportedLanguages = new String[]{"en", "bg"};

    /**
     * Retrieves the translations of the specified language
     *
     * @param language the language you want to get the translations of
     * @return translations object
     * @throws IOException if an i/o occur
     */
    @NotNull
    public Translations getTranslations(@NotNull String language) throws IOException {
        Objects.requireNonNull(language, "language");
        if (!Utils.contains(language.toLowerCase(), supportedLanguages)) {
            return getDefault();
        } else {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("jdcf_translations_" + language.toLowerCase() + ".properties")) {
                if (in == null) {
                    return getDefault();
                }
                return Translations.get(in);
            }
        }
    }

    private Translations getDefault() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("jdcf_translations_en.properties")) {
            return Translations.get(in);
        }
    }
}
