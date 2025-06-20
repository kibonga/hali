package org.hali.resource;

import java.io.InputStream;

public class ClasspathResourceLoader implements ResourceLoader {

    private final Class<?> resourceClass;

    public ClasspathResourceLoader(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    @Override
    public InputStream getInputStream(String path) {
        final InputStream is =
            this.resourceClass.getClassLoader().getResourceAsStream(path);

        if (is == null) {
            // TODO - add error handler
        }

        return is;
    }
}
