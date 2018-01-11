package org.embulk.generic.core.model;

public interface ExecutionContext {
    <T> T get(String key, Class<T> klass);
    Object get(String key);

    void put(String key, Object value);
}
