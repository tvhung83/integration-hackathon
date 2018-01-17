package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Status;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.spi.Column;
import org.embulk.spi.PageBuilder;
import org.embulk.spi.Schema;
import org.embulk.spi.json.JsonParser;
import org.embulk.spi.time.TimestampParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Import step
 * Created by tai.khuu on 1/11/18.
 */
public class ImporterStep implements Step
{

    private static final JsonParser JSON_PARSER = new JsonParser();


    public interface Configuration extends TimestampParser.Task, TimestampParser.TimestampColumnOption, Task
    {

    }

    @SuppressWarnings("unchecked")
    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        PageBuilder pageBuilder = executionContext.get("pageBuilder", PageBuilder.class);

        Schema schema = executionContext.get("schema", Schema.class);
        List<Column> columns = schema.getColumns();
        Object records = input.get("records");

        //TODO add initialization and move this to initialization phase
        Configuration configuration = config.loadConfig(Configuration.class);
        TimestampParser timestampParser = new TimestampParser(configuration, configuration);

        if (records instanceof List) {
            ((List<Map<String, Object>>) records).stream().forEach(record -> {
                columns.stream().forEach(
                        column -> importColumn(pageBuilder, record, column, timestampParser)
                );
                pageBuilder.addRecord();
            });
        }
        if (records instanceof Map) {
            Map<String, Object> record = (Map<String, Object>) records;
            columns.stream().forEach(column -> importColumn(pageBuilder, record, column, timestampParser));
            pageBuilder.addRecord();
        }
        StepExecutionResult stepExecutionResult = new StepExecutionResult();
        stepExecutionResult.setStatus(Status.SUCCESS);
        Map<String, Object> output = new HashMap<>();
        output.put("imported", 1);
        stepExecutionResult.setOutput(output);
        return stepExecutionResult;
    }

    private void importColumn(PageBuilder pageBuilder, Map<String, Object> record, Column column, TimestampParser timestampParser)
    {

        Object obj = record.get(column.getName());
        if (obj == null) {
            pageBuilder.setNull(column);
            return;
        }
        switch (column.getType().getName()) {
            case "boolean":
                pageBuilder.setBoolean(column, Boolean.parseBoolean(String.valueOf(obj)));
                break;
            case "long":
                pageBuilder.setLong(column, Long.valueOf(String.valueOf(obj)));
                break;
            case "double":
                pageBuilder.setDouble(column, Double.valueOf(String.valueOf(obj)));
                break;
            case "string":
                pageBuilder.setString(column, String.valueOf(obj));
                break;
            case "timestamp":
                pageBuilder.setTimestamp(column, timestampParser.parse(String.valueOf(obj)));
                break;
            case "json":
                pageBuilder.setJson(column, JSON_PARSER.parse(String.valueOf(obj)));
                break;
            default:
                throw new RuntimeException("Unsupport columnType");
        }
    }
}

