package org.embulk.generic.core.model;

import java.util.HashMap;

/**
 * Created by tai.khuu on 1/14/18.
 */
public class SimpleExecutionContext extends HashMap<String,Object> implements ExecutionContext
{

    @Override
    public <T> T get(String key, Class<T> klass)
    {
        if (!this.containsKey(key)) {
            return null;
        }
        return klass.cast(this.get(key));
    }
}
