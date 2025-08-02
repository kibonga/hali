package org.hali.common.serde;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ObjectSerializer<T> {
    byte[] serialize(T object) throws JsonProcessingException;
}
