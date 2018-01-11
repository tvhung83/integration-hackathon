package org.embulk.generic.core.model;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class StepConfig
{

    private String stepName;

    private int order;

    private Map<String, Object> configuration;

    public String getStepName()
    {
        return stepName;
    }

    public void setStepName(String stepName)
    {
        this.stepName = stepName;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public <T> T getConfig(String key, Class<T> klass)
    {
        return klass.cast(configuration.get(key));
    }
}
