package org.embulk.generic.core.model;

import java.util.Map;

import static org.embulk.generic.core.model.Status.SUCCESS;

public class StepExecutionResult {

    private Status status;

    private String nextStep;

    private Map<String,Object> output;

    public String getNextStep()
    {
        return nextStep;
    }

    public void setNextStep(String nextStep)
    {
        this.nextStep = nextStep;
    }

    public Map<String,Object> getOutput()
    {
        return output;
    }

    public void setOutput(Map<String,Object> output)
    {
        this.output = output;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public boolean isSuccess()
    {
        return status.equals(SUCCESS);
    }
}
