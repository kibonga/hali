package org.hali.resource;

import java.io.InputStream;
import org.hali.exception.ClasspathResourceLoadingException;

public class ClasspathResourceLoader implements ResourceLoader {

    private final Class<?> resourceClass;

    public ClasspathResourceLoader(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    @Override
    public InputStream getInputStream(String path)
        throws ClasspathResourceLoadingException {
        final InputStream is =
            this.resourceClass.getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new ClasspathResourceLoadingException(
                "Could not find resource on path: " + path);
        }

        return is;
    }
}
