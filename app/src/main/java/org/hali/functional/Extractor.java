package org.hali.functional;

import java.util.Optional;

public interface Extractor<I, O> {
    Optional<O> extract(I input);
}
