package org.hali.functional;

import java.util.function.Consumer;

public interface ConsumerHandler<T> {
    Consumer<T> consumer();
}
