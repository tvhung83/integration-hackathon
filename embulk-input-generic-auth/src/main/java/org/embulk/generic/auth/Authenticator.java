package org.embulk.generic.auth;

import com.google.common.collect.ImmutableMap;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.auth.basic.BasicAuthenticator;
import org.embulk.generic.auth.oauth2.OAuth2Authenticator;
import org.embulk.generic.auth.token.TokenAuthenticator;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.embulk.generic.core.model.StepExecutionResult.ERROR;
import static org.embulk.generic.core.model.StepExecutionResult.SUCCESS;

public abstract class Authenticator implements Step
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public interface Task
    {
        @Config("type")
        String getType();
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        StepExecutionResult result = new StepExecutionResult();
        try {
            result.setOutput(ImmutableMap.of("Authorization", buildAuthHeader(config)));
            result.setStatus(SUCCESS);
        }
        catch (Exception e) {
            logger.error("Failed to write authentication value", e);
            result.setStatus(ERROR);
        }
        return result;
    }

    protected abstract String buildAuthHeader(ConfigSource config);

    public static Authenticator getInstance(final String type)
    {
        switch (type) {
            case "basic":
                return new BasicAuthenticator();
            case "token":
                return new TokenAuthenticator();
            case "oauth2":
                return new OAuth2Authenticator();
            default:
                throw new IllegalArgumentException("Unknown auth type: " + type);
        }
    }
}
