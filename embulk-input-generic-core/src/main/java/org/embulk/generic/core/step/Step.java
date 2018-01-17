package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public interface Step
{
    StepExecutionResult run(
            ExecutionContext executionContext,
            ConfigSource config,
        Map<String,Object> input
    );
}
