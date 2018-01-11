package org.embulk.generic.core.step;

import okhttp3.OkHttpClient;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepConfig;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class RestClientStep implements Step
{

    private OkHttpClient okHttpClient;

    public RestClientStep(OkHttpClient okHttpClient)
    {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        return null;
    }

    public Map<String, String> buildHeader(Map<String, String> headerConfiguration, ExecutionContext executionContext, Map<String, String> input)
    {
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : headerConfiguration.entrySet()) {
            headerMap.put(entry.getKey(), evalWithScope(entry.getValue(), executionContext, input));
        }
        return headerMap;
    }
}

