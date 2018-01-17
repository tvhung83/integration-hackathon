package org.embulk.generic.core.step;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Status;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by tai.khuu on 1/16/18.
 */
public class RecordJsonExtractor implements Step
{

    private com.jayway.jsonpath.Configuration jsonPathConfiguration = com.jayway.jsonpath.Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL);

    public interface Configuration extends Task
    {

        @Config("record_path")
        String getRecordPath();

        @Config("record_key_value_map")
        Map<String, String> getRecordKeyValueMap();

    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        String recordPath = configuration.getRecordPath();
        Map<String, String> recordKeyValueMap = configuration.getRecordKeyValueMap();
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        Map<String, Object> output = new HashMap();
        DocumentContext documentContext = JsonPath.using(jsonPathConfiguration).parse(input.get("json_value").toString());
        Object recordsNode = documentContext.read(recordPath);
        if (recordsNode instanceof JSONArray) {
            JSONArray records = (JSONArray) recordsNode;
            List<Map<String, Object>> result = records.stream().map(record -> {
                DocumentContext recordNode = JsonPath.using(jsonPathConfiguration).parse(record);
                return recordKeyValueMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> getJsonValue(recordNode, e.getValue()).orElse(ELParser.getInstance().eval(e.getValue(), executionContext, input, Object.class))));
            }).collect(Collectors.toList());
            output.put("records", result);
        } else {
            Map<String, Object> result = recordKeyValueMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> getJsonValue(documentContext, e.getValue()).orElse((ELParser.getInstance().eval(e.getValue(), executionContext, input, Object.class)))));
            output.put("records", result);
        }
        stepExecutionResult.setOutput(output);
        stepExecutionResult.setStatus(Status.SUCCESS);
        return stepExecutionResult;
    }

    private Optional<Object> getJsonValue(DocumentContext recordNode, String value)
    {
        return Optional.ofNullable(recordNode.read(value));
    }
}
