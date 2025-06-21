package org.hali.resource;

import java.io.InputStream;
import org.hali.exception.ClasspathResourceLoadingException;

public interface ResourceLoader {
    InputStream getInputStream(String path)
        throws ClasspathResourceLoadingException;
}
