package org.hali.functional;

public interface Parser<I, O> {
    O parse(I input);
}
