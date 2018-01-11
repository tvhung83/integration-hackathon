package org.embulk.generic.client.request;

import com.google.common.base.Optional;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

import static org.embulk.generic.core.model.StepExecutionResult.SUCCESS;

public class RequestParamsBuilder implements RequestBuilder
{
    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        StepExecutionResult result = new StepExecutionResult();

        KeyValueTask task = config.loadConfig(KeyValueTask.class);
        // TODO
        if ("GET".equalsIgnoreCase(task.getMethod())) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(task.getUrl()).newBuilder();
            Optional<Map<String, String>> params = task.getParams();
            if (params.isPresent()) {
                Map<String, String> pairs = params.get();
                for (Map.Entry<String, String> param : pairs.entrySet()) {
                    urlBuilder.addQueryParameter(param.getKey(), param.getValue());
                }
                result.setOutput(params.get());
            }
            // Build request
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();
            // TODO: set to result (when result support generic output)
        }
        // TODO: support POST
        // TODO: support other methods?

        result.setStatus(SUCCESS);
        return result;
    }

    public interface KeyValueTask extends RequestBuilder.RequestTask
    {
        // TODO: support multiple values for the same key, ie. List<Map<String, String>>
        @Config("params")
        @ConfigDefault("null")
        Optional<Map<String, String>> getParams();
    }
}
