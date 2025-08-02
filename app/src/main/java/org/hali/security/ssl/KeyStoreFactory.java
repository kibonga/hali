package org.hali.security.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

public interface KeyStoreFactory {

    KeyStore createKeyStore() throws KeyStoreException;
}
