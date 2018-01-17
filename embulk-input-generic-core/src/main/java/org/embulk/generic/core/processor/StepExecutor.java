package org.embulk.generic.core.processor;

import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepConfig;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class StepExecutor
{

    private Map<String, Step> stepMap;

    public StepExecutor(Map<String, Step> stepMap)
    {
        this.stepMap = stepMap;
    }

    public StepExecutionResult execute(StepConfig stepConfig, ExecutionContext executionContext, Map<String, Object> input)
    {
        Step step = getStep(stepConfig.getStepName());
        StepExecutionResult result = step.run(executionContext, stepConfig.getConfiguration(), input);
        //if step didn't set nextStep then we do it here
        if (result.getNextStep() == null) {
            result.setNextStep(stepConfig.getNextStepId());
        }
        for (Map.Entry<String, String> adapter : Optional.ofNullable(stepConfig.getInputAdapter()).orElse(new HashMap<>()).entrySet()) {
            input.put(adapter.getValue(), input.get(adapter.getKey()));
            input.remove(adapter.getKey());
        }
        Map<String, Object> output = result.getOutput();
        //Expose variable to context
        for (Map.Entry<String, String> outputContext : Optional.ofNullable(stepConfig.getContextOutput()).orElse(new HashMap<>()).entrySet()) {
            Object value = ELParser.getInstance().eval(outputContext.getValue(), executionContext, output, Object.class);
            if (value != null) {
                executionContext.put(outputContext.getKey(), value);
            } else {
                executionContext.remove(outputContext.getKey());
            }
        }
        if (stepConfig.isPassPreviousResult()) {
            input.putAll(output);
            result.setOutput(input);
        }
        return result;
    }

    public Step getStep(String stepName)
    {
        return stepMap.get(stepName);
    }
}
