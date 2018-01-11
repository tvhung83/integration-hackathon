package org.embulk.generic.core.model;

import java.util.Map;

public class StepExecutionResult {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private String status;

    private String nextStep;

    private Map<String, String> output;

    public String getNextStep()
    {
        return nextStep;
    }

    public void setNextStep(String nextStep)
    {
        this.nextStep = nextStep;
    }

    public Map<String, String> getOutput()
    {
        return output;
    }

    public void setOutput(Map<String, String> output)
    {
        this.output = output;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public boolean isSuccess()
    {
        return status.equals(SUCCESS);
    }
}
