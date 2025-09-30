package org.hali.functional;

import java.util.Optional;

public interface Resolver<T> {
    Optional<T> resolve(String type);
}
