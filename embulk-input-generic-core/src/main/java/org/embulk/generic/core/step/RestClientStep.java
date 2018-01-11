package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepConfig;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class RestClientStep implements Step
{

//    @Override
//    public StepExecutionResult run(ExecutionContext executionContext, StepConfig stepConfig, Map<String, String> input)
//    {
//        Map<String, String> headerConfiguration = stepConfig.getConfig("header", Map.class);
//    }
//
//    private Map<String, String> buildHeader(ExecutionContext executionContext, Map<String, String> headerConfiguration, Map<String, String> input)
//    {
//        headerConfiguration.
//    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        return null;
    }
}
