package org.hali.http.security;

import javax.net.ssl.SSLContext;

public interface HttpsSslConfigurator {

    void apply(SSLContext sslContext);
}
