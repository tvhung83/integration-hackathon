package org.embulk.generic.core.step.client.builder;

import com.google.common.base.Optional;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigException;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

import static org.embulk.generic.core.model.StepExecutionResult.Status.SUCCESS;

public class RequestParamsBuilder implements RequestBuilder
{
    public interface KeyValueTask extends RequestBuilder.RequestTask
    {
        // TODO: support multiple values for the same key, ie. List<Map<String, String>>
        @Config("params")
        @ConfigDefault("null")
        Optional<Map<String, String>> getParams();
    }

    @Override
    public StepExecutionResult<Request> run(ExecutionContext executionContext, ConfigSource config, Object input)
    {
        StepExecutionResult<Request> result = new StepExecutionResult<>();

        KeyValueTask task = config.loadConfig(KeyValueTask.class);

        Optional<Map<String, String>> params = task.getParams();
        if ("GET".equalsIgnoreCase(task.getMethod())) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(task.getUrl()).newBuilder();
            if (params.isPresent()) {
                Map<String, String> pairs = params.get();
                for (Map.Entry<String, String> param : pairs.entrySet()) {
                    urlBuilder.addQueryParameter(param.getKey(), param.getValue());
                }
            }
            // Build request
            result.setOutput(new Request.Builder()
                    .url(urlBuilder.build())
                    .build());
        }
        else if ("POST".equalsIgnoreCase(task.getMethod())) {
            FormBody.Builder form = new FormBody.Builder();
            if (params.isPresent()) {
                Map<String, String> pairs = params.get();
                for (Map.Entry<String, String> param : pairs.entrySet()) {
                    form.add(param.getKey(), param.getValue());
                }
            }
            // Build request
            result.setOutput(new Request.Builder()
                    .url(task.getUrl())
                    .post(form.build())
                    .build());
        }
        else {
            // TODO: support other methods?
            throw new ConfigException("Unsupported method: " + task.getMethod());
        }

        result.setStatus(SUCCESS);
        return result;
    }
}
