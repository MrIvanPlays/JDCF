package com.mrivanplays.jdcf.translation;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PropertyTranslationFile implements TranslationFile {

    private ResourceBundle bundle;

    public PropertyTranslationFile(InputStream in) throws IOException  {
        bundle = new PropertyResourceBundle(in);
    }

    public PropertyTranslationFile(Reader in) throws IOException {
        bundle = new PropertyResourceBundle(in);
    }

    @Override
    public @NotNull String getString(String key) {
        return bundle.getString(key);
    }

    @Override
    public @NotNull String getFileType() {
        return ".properties";
    }

    @Override
    public @NotNull Collection<String> getKeys() {
        Collection<String> mutable = new ArrayList<>();
        Enumeration<String> keyEnumeration = bundle.getKeys();
        while (keyEnumeration.hasMoreElements()) {
            mutable.add(keyEnumeration.nextElement());
        }
        return Collections.unmodifiableCollection(mutable);
    }
}
