package org.hali.http.security;

import java.security.KeyStore;
import java.security.KeyStoreException;

public interface KeyStoreFactory {

    KeyStore createKeyStore() throws KeyStoreException;
}
