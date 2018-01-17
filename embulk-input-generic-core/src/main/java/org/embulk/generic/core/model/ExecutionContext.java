package org.embulk.generic.core.model;

import java.util.Map;

public interface ExecutionContext extends Map<String,Object>
{

    <T> T get(String key, Class<T> klass);
}
