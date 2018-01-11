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

    private StepExecutor stepExecutor;

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

    public FlowExecutionResult execute(ExecutionContext executionContext)
    {
        Map<String, String> stepInput = new HashMap<>();
        String nextStep = getFirstStep();
        while (nextStep != null) {
            StepConfig stepConfig = getStep(nextStep);
            StepExecutionResult result = stepExecutor.execute(stepConfig, executionContext, stepInput);
            nextStep = result.getNextStep();
            stepInput = result.getOutput();
        }
        return new FlowExecutionResult();
    }
}
