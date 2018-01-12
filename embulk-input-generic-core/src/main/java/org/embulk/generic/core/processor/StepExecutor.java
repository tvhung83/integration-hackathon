package org.embulk.generic.core.processor;

import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepConfig;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class StepExecutor
{

    private Map<String, Step> stepMap;

    public StepExecutionResult execute(StepConfig stepConfig, ExecutionContext executionContext, Map<String, String> input)
    {
        Step step = getStep(stepConfig.getStepName());
        StepExecutionResult result = step.run(executionContext, stepConfig.getConfiguration(), input);
        //if step didn't set nextStep then we do it here
        result.setNextStep(stepConfig.getNextStepId());
        return result;
    }

    public Step getStep(String stepName)
    {
        return stepMap.get(stepName);
    }
}
