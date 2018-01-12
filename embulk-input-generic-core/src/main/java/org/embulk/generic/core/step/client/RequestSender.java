package org.embulk.generic.core.step.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.embulk.generic.core.model.StepExecutionResult.Status.ERROR;
import static org.embulk.generic.core.model.StepExecutionResult.Status.SUCCESS;

public class RequestSender implements Step<Request, Response>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public interface SenderTask extends Task
    {
        @Config("connect_timeout")
        @ConfigDefault("60")
        int getConnectTimeout();

        @Config("read_timeout")
        @ConfigDefault("300")
        int getReadTimeout();
    }

    /*
     * `input` is built request, ready to be sent
     */
    @Override
    public StepExecutionResult<Response> run(ExecutionContext executionContext, ConfigSource config, Request input)
    {
        StepExecutionResult<Response> result = new StepExecutionResult<>();

        SenderTask task = config.loadConfig(SenderTask.class);
        // TODO: cache client to reduce overhead
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(task.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(task.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        try {
            result.setOutput(client.newCall(input).execute());
            result.setStatus(SUCCESS);
        }
        catch (IOException e) {
            log.warn("Failed to send request: {}", e.getMessage(), e);
            result.setStatus(ERROR);
        }
        return result;
    }
}
