package org.embulk.generic.core.step;

import com.google.common.base.Optional;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.*;
import org.embulk.generic.core.processor.FlowExecutor;
import org.embulk.generic.core.processor.StepExecutor;

import javax.inject.Inject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Group multiple step into 1
 * Created by tai.khuu on 1/17/18.
 */
public class LoopStep implements Step
{

    private interface Configuration extends Task
    {

        @Config("steps")
        Map<String, StepConfig> getStepMap();

        @Config("on")
        @ConfigDefault("null")
        Optional<String> getOn();

        @Config("until")
        @ConfigDefault("null")
        Optional<Integer> getUntil();

        @Config("output")
        @ConfigDefault("null")
        Optional<Map<String, String>> getOutputMap();
    }

    @Inject
    private StepExecutor executor;

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        Map<String, StepConfig> stepMap = configuration.getStepMap();
        ExecutionContext loopContext = new SimpleExecutionContext();
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        loopContext.putAll(executionContext);
        if (configuration.getOn().isPresent()) {
            List records = ELParser.getInstance().eval(configuration.getOn().get(), executionContext, input, List.class);
            for (Object record : records) {
                Map<String, Object> stepInput = new HashMap<>();
                String nextStep = stepMap.keySet().iterator().next();
                while (nextStep != null) {
                    executionContext.put("currentRecord", record);
                    StepConfig stepConfig = stepMap.get(nextStep);
                    StepExecutionResult result = executor.execute(stepConfig, executionContext, stepInput);
                    nextStep = result.getNextStep();
                    stepInput = result.getOutput();
                }
            }
        }
        for (String key : executionContext.keySet()) {
            loopContext.remove(key);
        }
        stepExecutionResult.setOutput(loopContext);
        stepExecutionResult.setStatus(Status.SUCCESS);
        return stepExecutionResult;
    }
}
