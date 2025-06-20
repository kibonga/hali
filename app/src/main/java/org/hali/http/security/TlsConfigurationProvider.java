package org.hali.http.security;

public interface TlsConfigurationProvider {
    boolean isTlsEnabled();
    String getKeystorePath();
    String getKeystorePassword();
}
