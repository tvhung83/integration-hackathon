package org.embulk.generic.auth;

import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.embulk.generic.core.model.StepExecutionResult.ERROR;
import static org.embulk.generic.core.model.StepExecutionResult.SUCCESS;

public abstract class Authentication implements Step
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public interface Task
    {
        @Config("type")
        String getType();

        String getAuthHeader();

        void setAuthHeader(String authHeader);
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        StepExecutionResult result = new StepExecutionResult();
        try {
            Task task = config.loadConfig(Task.class);
            task.setAuthHeader(buildAuthHeader(config));
            result.setStatus(SUCCESS);
        }
        catch (Exception e) {
            logger.error("Failed to write authentication value", e);
            result.setStatus(ERROR);
        }
        return result;
    }

    protected abstract String buildAuthHeader(ConfigSource config);
}
