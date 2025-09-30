package org.hali.functional;

import java.util.Optional;

public interface ParserResolver<T, R, C> {
   Optional<Parser<T, R, C>> resolve(String type);
}
