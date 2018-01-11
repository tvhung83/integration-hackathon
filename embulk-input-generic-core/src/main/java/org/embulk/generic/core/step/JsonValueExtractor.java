package org.embulk.generic.core.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

/**
 * This step use json path to extract value from input json string
 * Created by tai.khuu on 1/11/18.
 */
public class JsonValueExtractor implements Step
{

    private interface StepTask extends Task
    {
        @Config("jsonpath_map")
        Map<String, String> getJsonPahMap();

    }
    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
//        stepExecutionResult.setOutput();
//
        return stepExecutionResult;
    }
}
