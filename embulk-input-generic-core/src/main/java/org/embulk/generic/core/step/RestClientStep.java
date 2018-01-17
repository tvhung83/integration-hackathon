package org.embulk.generic.core.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import okhttp3.*;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Status;
import org.embulk.generic.core.model.StepExecutionResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class RestClientStep implements Step
{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private OkHttpClient okHttpClient;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public interface Configuration extends Task
    {

        @Config("url")
        String getUrl();

        @Config("header")
        @ConfigDefault("null")
        Optional<Map<String, String>> getHeaderMap();

        @Config("param")
        @ConfigDefault("null")
        Optional<Map<String, String>> getParamMap();

        @Config("method")
        @ConfigDefault("null")
        Optional<String> getMethod();

        @Config("body")
        @ConfigDefault("null")
        Optional<String> getBodyTemplate();
    }

    public RestClientStep(OkHttpClient okHttpClient)
    {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ELParser.getInstance().eval(configuration.getUrl(), executionContext, input, String.class)).newBuilder();
        configuration.getHeaderMap().transform(map -> buildHeader(map, executionContext, input)).or(new HashMap<>()).forEach(requestBuilder::addHeader);
        configuration.getParamMap().transform(map -> buildParams(map, executionContext, input)).or(new HashMap<>()).forEach(urlBuilder::addQueryParameter);
        Request request = requestBuilder.url(urlBuilder.build())
                .method(configuration.getMethod().or("GET"), configuration.getBodyTemplate().transform(body -> RequestBody.create(JSON, ELParser.getInstance().eval(body, executionContext, input, String.class))).orNull())
                .build();
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        stepExecutionResult.setStatus(Status.SUCCESS);
        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            Map<String, Object> output = new HashMap<>();
            output.put("response", responseString);
            output.put("json_value", OBJECT_MAPPER.readTree(responseString));
            stepExecutionResult.setOutput(output);
        } catch (IOException e) {
            stepExecutionResult.setStatus(Status.ERROR);
        }
        return stepExecutionResult;
    }

    private Map<String, String> buildHeader(Map<String, String> headerConfiguration, ExecutionContext executionContext, Map<String, Object> input)
    {
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : headerConfiguration.entrySet()) {
            String eval = ELParser.getInstance().eval(entry.getValue(), executionContext, input, String.class);
            if (eval != null) {
                headerMap.put(entry.getKey(), eval);
            }
        }
        return headerMap;
    }

    private Map<String, String> buildParams(Map<String, String> parameterMap, ExecutionContext executionContext, Map<String, Object> input)
    {
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            String eval = ELParser.getInstance().eval(entry.getValue(), executionContext, input, String.class);
            if (eval != null) {
                headerMap.put(entry.getKey(), eval);
            }
        }
        return headerMap;
    }
}

