package org.hali.functional;

public interface ParserResolver<T, R, C> {
   Parser<T, R, C> resolve(String type);
}
