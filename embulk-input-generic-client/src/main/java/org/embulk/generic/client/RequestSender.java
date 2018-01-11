package org.embulk.generic.client;

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RequestSender implements Step
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
     * `input` is client, ready to be sent
     */
    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        StepExecutionResult result = new StepExecutionResult();

        SenderTask task = config.loadConfig(SenderTask.class);
        // TODO: cache client to reduce overhead
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(task.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(task.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        // TODO: get request from input
        // TODO: output as Response
        try {
            Response resp = client.newCall(new Request.Builder().url("dummy").build()).execute();
            result.setOutput(ImmutableMap.of("response", resp.body().string()));
            result.setStatus(StepExecutionResult.SUCCESS);
        } catch (IOException e) {
            log.warn("Failed to send request: {}", e.getMessage(), e);
            result.setStatus(StepExecutionResult.ERROR);
        }
        return result;
    }
}
