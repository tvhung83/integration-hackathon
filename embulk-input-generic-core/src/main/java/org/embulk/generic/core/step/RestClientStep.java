package org.embulk.generic.core.step;

import okhttp3.*;
import okhttp3.internal.http.HttpMethod;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.springframework.expression.EvaluationContext;

import java.io.IOException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class RestClientStep implements Step<Map<String, String>, Map<String, String>>
{

    private OkHttpClient okHttpClient;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public interface Configuration extends Task
    {

        @Config("url")
        String getUrl();

        @Config("header")
        Optional<Map<String, String>> getHeaderMap();

        @Config("param")
        Optional<Map<String, String>> getParamMap();

        @Config("method")
        Optional<String> getMethod();

        @Config("body")
        Optional<String> getBodyTemplate();
    }

    public RestClientStep(OkHttpClient okHttpClient)
    {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public StepExecutionResult<Map<String, String>> run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(ELParser.getInstance().eval(configuration.getUrl(), executionContext, input, String.class)).newBuilder();
        configuration.getHeaderMap().map(map -> buildHeader(map, executionContext, input)).orElse(new HashMap<>()).forEach(requestBuilder::addHeader);
        configuration.getParamMap().map(map -> buildParams(map, executionContext, input)).orElse(new HashMap<>()).forEach(urlBuilder::addQueryParameter);
        Request request = requestBuilder.url(urlBuilder.build())
                .method(configuration.getMethod().orElse("GET"), configuration.getBodyTemplate().map(body -> RequestBody.create(JSON, ELParser.getInstance().eval(body, executionContext, input, String.class))).get())
                .build();
        StepExecutionResult<Map<String, String>> stepExecutionResult = new StepExecutionResult<>();
        stepExecutionResult.setStatus(StepExecutionResult.Status.SUCCESS);
        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();
            Map<String, String> output = new HashMap<>();
            output.put("response", responseString);
            stepExecutionResult.setOutput(output);
        } catch (IOException e) {
            stepExecutionResult.setStatus(StepExecutionResult.Status.SUCCESS.ERROR);
        }
        return stepExecutionResult;
    }

    private Map<String, String> buildHeader(Map<String, String> headerConfiguration, ExecutionContext executionContext, Map<String, String> input)
    {
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : headerConfiguration.entrySet()) {
            headerMap.put(entry.getKey(), ELParser.getInstance().eval(entry.getValue(), executionContext, input, String.class));
        }
        return headerMap;
    }

    private Map<String, String> buildParams(Map<String, String> parameterMap, ExecutionContext executionContext, Map<String, String> input)
    {
        Map<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            headerMap.put(entry.getKey(), ELParser.getInstance().eval(entry.getValue(), executionContext, input, String.class));
        }
        return headerMap;
    }
}

