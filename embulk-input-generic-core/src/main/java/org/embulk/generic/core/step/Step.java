package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
import org.embulk.generic.core.ELParser;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.StepExecutionResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tai.khuu on 1/11/18.
 */
public interface Step<I, O>
{
    StepExecutionResult<O> run(
            ExecutionContext executionContext,
            ConfigSource config,
            I input
    );

    //Evalue value expressions
    default <T> T evalWithScope(String exp, ExecutionContext executionContext, Map<String, String> input,Class<T> klass)
    {
        ELParser.getInstance().parseString(exp, executionContext, input, klass);
    }
}
