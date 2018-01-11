package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

/**
 * Import step
 * Created by tai.khuu on 1/11/18.
 */
public class ImporterStep extends Step
{

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, String> input)
    {
        return null;
    }
}
