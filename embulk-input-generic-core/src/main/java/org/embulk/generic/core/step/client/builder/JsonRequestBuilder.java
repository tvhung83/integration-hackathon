package org.embulk.generic.core.step.client.builder;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import static org.embulk.generic.core.model.StepExecutionResult.Status.SUCCESS;

public class JsonRequestBuilder implements RequestBuilder
{
    public interface JsonBodyTask extends RequestTask
    {
        @Config("body")
        String getBody();
    }

    @Override
    public StepExecutionResult<Request> run(ExecutionContext executionContext, ConfigSource config, Object input)
    {
        StepExecutionResult<Request> result = new StepExecutionResult<>();
        JsonBodyTask task = config.loadConfig(JsonBodyTask.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), task.getBody());
        result.setOutput(new Request.Builder()
                .url(task.getUrl())
                .post(body)
                .build());
        result.setStatus(SUCCESS);
        return result;
    }
}
