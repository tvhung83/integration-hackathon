package org.embulk.generic.core.processor;

import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class FlowExecutor
{

    private StepExecutor stepExecutor;

    public static final String END_STEP = "END!";
    public FlowExecutor(StepExecutor stepExecutor)
    {
        this.stepExecutor = stepExecutor;
    }

    public FlowExecutionResult execute(Flow flow, ExecutionContext executionContext)
    {
        Map<String, Object> stepInput = null;
        String nextStep = flow.getFirstStep();
        while (nextStep != null && !nextStep.equals(END_STEP)) {
            StepConfig stepConfig = flow.getStep(nextStep);
            StepExecutionResult result = stepExecutor.execute(stepConfig, executionContext, stepInput);
            nextStep = result.getNextStep();
            stepInput = result.getOutput();
        }
        return new FlowExecutionResult();
    }
}
