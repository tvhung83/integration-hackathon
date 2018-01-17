package org.embulk.generic.core.step;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Status;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * This step use json path to extract value from input json string
 * Created by tai.khuu on 1/11/18.
 */
public class JsonPathExtractor implements Step
{

    private com.jayway.jsonpath.Configuration jsonPathConfiguration = com.jayway.jsonpath.Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL);
    private interface Configuration extends Task
    {
        @Config("jsonpath_map")
        Map<String, String> getJsonPahMap();
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        DocumentContext documentContext = JsonPath.using(jsonPathConfiguration).parse(input.get("json_value").toString());
        Configuration configuration = config.loadConfig(Configuration.class);
        Map<String, Object> output = new HashMap();
        for (Map.Entry<String, String> jsonPath : configuration.getJsonPahMap().entrySet()) {
            Object value = documentContext.read(jsonPath.getValue());
            output.put(jsonPath.getKey(), value);
        }
        stepExecutionResult.setOutput(output);
        stepExecutionResult.setStatus(Status.SUCCESS);
        return stepExecutionResult;
    }
}
