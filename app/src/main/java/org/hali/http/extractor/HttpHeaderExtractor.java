package org.hali.http.extractor;

import com.sun.net.httpserver.Headers;
import org.springframework.stereotype.Component;

@Component
public class HttpHeaderExtractor implements HeaderExtractor {

    @Override
    public String extract(Headers headers, String name) {
        return headers.getFirst(name);
    }
}
