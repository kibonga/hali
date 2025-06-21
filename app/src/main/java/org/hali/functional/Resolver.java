package org.hali.functional;

public interface Resolver<T> {
    T resolve(String type);
}
