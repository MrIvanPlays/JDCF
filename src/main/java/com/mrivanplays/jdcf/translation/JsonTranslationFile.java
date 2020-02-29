package com.mrivanplays.jdcf.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a translation file implementation, utilising the ".json" file type using jackson as library.
 */
public class JsonTranslationFile implements TranslationFile {

    private JsonNode object;

    public JsonTranslationFile(InputStream in, ObjectMapper mapper) throws IOException {
        this.object = mapper.reader().readTree(in);
    }

    public JsonTranslationFile(InputStream in) throws IOException {
        this(in, new ObjectMapper());
    }

    public JsonTranslationFile(Reader in, ObjectMapper mapper) throws IOException {
        this.object = mapper.reader().readTree(in);
    }

    public JsonTranslationFile(Reader in) throws IOException {
        this(in, new ObjectMapper());
    }

    @Override
    public @NotNull String getString(String key) {
        return object.get(key).asText();
    }

    @Override
    public @NotNull String getFileType() {
        return ".json";
    }

    @Override
    public @NotNull Collection<String> getKeys() {
        Collection<String> collection = new ArrayList<>();
        Iterator<String> keyIterator = object.fieldNames();
        while (keyIterator.hasNext()) {
            collection.add(keyIterator.next());
        }
        return collection;
    }
}
