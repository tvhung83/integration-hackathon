package org.embulk.generic.core.step.auth;

import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.step.auth.basic.BasicAuthenticator;
import org.embulk.generic.core.step.auth.oauth2.OAuth2Authenticator;
import org.embulk.generic.core.step.auth.token.TokenAuthenticator;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;
import org.embulk.generic.core.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.embulk.generic.core.model.Status.ERROR;
import static org.embulk.generic.core.model.Status.SUCCESS;

/**
 * Authenticator doesn't need input, it reads from config file
 */
public abstract class Authenticator implements Step
{
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public interface AuthTask extends Task
    {
        @Config("type")
        String getType();
    }

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String,Object> input)
    {
        StepExecutionResult result = new StepExecutionResult();
        try {
            Map<String, Object> output = new HashMap<>();
            output.put("access_header", buildAuthHeader(config));
            result.setOutput(output);
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
