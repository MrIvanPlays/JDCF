package com.mrivanplays.jdcf.translation;

import com.mrivanplays.jdcf.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
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
            String translationFileName = "jdcf_translations_" + language.toLowerCase() + ".properties";
            File physicalTranslationFile = new File(".", translationFileName);
            if (physicalTranslationFile.exists()) {
                try (Reader reader = Files.newBufferedReader(physicalTranslationFile.toPath(), StandardCharsets.UTF_8)) {
                    return Translations.get(reader, language);
                }
            } else {
                try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(translationFileName), StandardCharsets.UTF_8)) {
                    return Translations.get(reader, language);
                }
            }
        }
    }

    /**
     * Returns a copy of the supported languages.
     *
     * @return supported languages
     */
    @NotNull
    public String[] getSupportedLanguages() {
        return Arrays.copyOf(supportedLanguages, supportedLanguages.length);
    }

    private Translations getDefault() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("jdcf_translations_en.properties")) {
            return Translations.get(in, "en");
        }
    }
}
