package org.hali.security.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;

import org.springframework.stereotype.Component;

@Component
public class DefaultKeyStoreFactory implements KeyStoreFactory {

    @Override
    public KeyStore createKeyStore() throws KeyStoreException {
        return KeyStore.getInstance(KeyStore.getDefaultType());
    }
}
