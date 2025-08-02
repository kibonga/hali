package org.hali.resource;

import java.io.InputStream;
import org.hali.exception.ClasspathResourceLoadingException;

public class ClasspathResourceLoader implements ResourceLoader {

    private final ClassLoader classLoader;

    public ClasspathResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getInputStream(String path)
        throws ClasspathResourceLoadingException {
        final InputStream is = this.classLoader.getResourceAsStream(path);

        if (is == null) {
            throw new ClasspathResourceLoadingException(
                "Could not find resource on path: " + path);
        }

        return is;
    }
}
