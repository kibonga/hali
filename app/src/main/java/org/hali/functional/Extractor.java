package org.hali.functional;

import org.hali.exception.ExtractingException;

public interface Extractor<I, O> {
    O extract(I input) throws ExtractingException;
}
