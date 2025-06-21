package org.hali.functional;

public interface ParserResolver<T, R> {
   Parser<T, R> resolve(String type);
}
