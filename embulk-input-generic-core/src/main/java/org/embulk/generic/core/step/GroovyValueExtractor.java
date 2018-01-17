package org.embulk.generic.core.step;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Status;
import org.embulk.generic.core.model.StepExecutionResult;

import java.util.Map;

/**
 * Created by tai.khuu on 1/15/18.
 */
public class GroovyValueExtractor implements Step
{

    public interface Configuration extends Task
    {

        @Config("script")
        String getExtractorScript();

        @Config("method_name")
        String getExtractorMethod();
    }

    private GroovyShell groovyShell = new GroovyShell();

    @Override
    public StepExecutionResult run(ExecutionContext executionContext, ConfigSource config, Map<String, Object> input)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        Script script = groovyShell.parse(configuration.getExtractorScript());
        Object[] arguments = new Object[]{executionContext, input};
        Object object = script.invokeMethod(configuration.getExtractorMethod(), arguments);
        StepExecutionResult mapStepExecutionResult = new StepExecutionResult();
        mapStepExecutionResult.setOutput((Map) object);
        mapStepExecutionResult.setStatus(Status.SUCCESS);
        return mapStepExecutionResult;
    }

}
