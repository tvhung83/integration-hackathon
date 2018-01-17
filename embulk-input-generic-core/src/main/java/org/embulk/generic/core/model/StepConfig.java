package org.embulk.generic.core.model;

import org.embulk.config.ConfigSource;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class StepConfig
{

    private String stepName;

    private int order;

    private boolean passPreviousResult;

    private Map<String, String> inputAdapter;

    private ConfigSource configuration;

    private Map<String, String> contextOutput;

    private String nextStepId;

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

    public ConfigSource getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(ConfigSource configuration)
    {
        this.configuration = configuration;
    }

    public Map<String, String> getContextOutput()
    {
        return contextOutput;
    }

    public void setContextOutput(Map<String, String> contextOutput)
    {
        this.contextOutput = contextOutput;
    }

    public String getNextStepId()
    {
        return nextStepId;
    }

    public void setNextStepId(String nextStepId)
    {
        this.nextStepId = nextStepId;
    }

    public boolean isPassPreviousResult()
    {
        return passPreviousResult;
    }

    public void setPassPreviousResult(boolean passPreviousResult)
    {
        this.passPreviousResult = passPreviousResult;
    }

    public Map<String, String> getInputAdapter()
    {
        return inputAdapter;
    }

    public void setInputAdapter(Map<String, String> inputAdapter)
    {
        this.inputAdapter = inputAdapter;
    }
}
