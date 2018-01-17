package org.embulk.generic.core.step;

import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.processor.FlowExecutor;

import java.util.Map;

/**
 * Created by tai.khuu on 1/15/18.
 */
public class ConditionStep implements Step
{


    public interface Configuration extends Task
    {

        @Config("condition_map")
        Map<String, String> getConditionMap();
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String,Object> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        stepExecutionResult.setOutput(input);
        for (Map.Entry<String, String> condition : configuration.getConditionMap().entrySet()) {
            Boolean eval = ELParser.getInstance().eval(condition.getKey(), executionContext, input, Boolean.class);
            if (eval != null && eval) {
                stepExecutionResult.setNextStep(condition.getValue());
                return stepExecutionResult;
            }
        }
        return stepExecutionResult;
    }
}
