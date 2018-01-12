package org.embulk.generic.core.processor;

import org.embulk.generic.core.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class FlowExecutor
{

    StepExecutor stepExecutor;
    public FlowExecutionResult execute(Flow flow, ExecutionContext executionContext)
    {
        Map<String, String> stepInput = new HashMap<>();
        String nextStep = flow.getFirstStep();
        while (nextStep != null) {
            StepConfig stepConfig = flow.getStep(nextStep);
            StepExecutionResult<Map<String, String>> result = stepExecutor.execute(stepConfig, executionContext, stepInput);
            nextStep = result.getNextStep();
            Map<String, String> stepOutput = result.getOutput();
            stepInput = stepOutput;
            //Expose variable to context
            if (stepConfig.getContextOutput() == null) {
            continue;
            }
            for (Map.Entry<String, String> outputContext : stepConfig.getContextOutput().entrySet()) {
                String value = stepOutput.get(outputContext.getValue());
                if (value != null) {
                    executionContext.put(outputContext.getKey(), value);
                }
            }
        }
        return new FlowExecutionResult();
    }
}
