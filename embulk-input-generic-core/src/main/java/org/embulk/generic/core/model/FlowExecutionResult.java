package org.embulk.generic.core.model;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class FlowExecutionResult
{
    private Status status;

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}
