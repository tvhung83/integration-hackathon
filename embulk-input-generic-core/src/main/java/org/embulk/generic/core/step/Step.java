package org.embulk.generic.core.step;

import org.embulk.config.ConfigSource;
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

    // Evaluate value expressions
    default String evalWithScope(String exp, ExecutionContext executionContext, Map<String, String> input)
    {
        if (!exp.contains("{") || !exp.contains("}")) {
            return exp;
        }
        String[] exps = exp.split(".");
        String inputVaiableName = exps[0];
        Object currentValue = Optional.ofNullable(executionContext.get(inputVaiableName)).orElse(input.get(inputVaiableName));
        if (currentValue == null) {
            return null;
        }
        int i = 1;
        while (i < exps.length) {
            String variableName = exps[i];
            try {
                Method getter = currentValue.getClass().getMethod("get" + variableName.substring(0, 1).toUpperCase() + variableName.substring(1));
                currentValue = getter.invoke(currentValue);
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                return null;
            }
        }
        return currentValue.toString();
    }
}
