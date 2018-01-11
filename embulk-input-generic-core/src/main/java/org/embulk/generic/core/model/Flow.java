package org.embulk.generic.core.model;

import org.embulk.generic.core.processor.StepExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class Flow
{
    private Map<String, StepConfig> steps;

    public Map<String, StepConfig> getSteps()
    {
        return steps;
    }

    public StepConfig getStep(String nextStep)
    {
        return steps.get(nextStep);
    }
    public void setSteps(Map<String, StepConfig> steps)
    {
        this.steps = steps;
    }

    public String getFirstStep()
    {
        for (StepConfig stepConfig : steps.values()) {
            return stepConfig.toString();
        }
        return null;
    }


}
