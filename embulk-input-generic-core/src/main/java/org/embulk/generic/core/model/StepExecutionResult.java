package org.embulk.generic.core.model;

import static org.embulk.generic.core.model.StepExecutionResult.Status.SUCCESS;

public class StepExecutionResult<O> {
    public enum Status
    {
        SUCCESS, ERROR
    }

    private Status status;

    private String nextStep;

    private O output;

    public String getNextStep()
    {
        return nextStep;
    }

    public void setNextStep(String nextStep)
    {
        this.nextStep = nextStep;
    }

    public O getOutput()
    {
        return output;
    }

    public void setOutput(O output)
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
