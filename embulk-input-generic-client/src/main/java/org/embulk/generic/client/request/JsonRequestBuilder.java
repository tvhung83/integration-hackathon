package org.embulk.generic.client.request;

import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

// TODO: output is Map<String, String>, not suitable with output of JsonRequestBuilder
public class JsonRequestBuilder implements RequestBuilder
{
    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        return null;
    }
}
