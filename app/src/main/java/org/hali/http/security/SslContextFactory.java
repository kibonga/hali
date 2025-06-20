package org.hali.http.security;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

public interface SslContextFactory {

    SSLContext createSslContext() throws NoSuchAlgorithmException;
}
