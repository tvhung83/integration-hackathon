package org.embulk.input.generic;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import okhttp3.OkHttpClient;
import org.embulk.config.*;
import org.embulk.generic.core.model.ExecutionContext;
import org.embulk.generic.core.model.Flow;
import org.embulk.generic.core.model.FlowExecutionResult;
import org.embulk.generic.core.model.SimpleExecutionContext;
import org.embulk.generic.core.processor.FlowExecutor;
import org.embulk.generic.core.processor.StepExecutor;
import org.embulk.generic.core.step.*;
import org.embulk.generic.core.step.auth.basic.BasicAuthenticator;
import org.embulk.generic.core.step.auth.oauth2.OAuth2Authenticator;
import org.embulk.generic.core.step.auth.token.TokenAuthenticator;
import org.embulk.spi.*;
import org.embulk.spi.time.TimestampParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tai.khuu on 1/14/18.
 */
public class GenericRestPlugin implements InputPlugin
{
    public interface Configuration extends Task
    {

        @Config("execution_context")
        @ConfigDefault("null")
        Optional<Map<String, String>> getExecutionContext();


        @Config("flow_description")
        Flow getFlowDescription();

    }
    @Override
    public ConfigDiff transaction(ConfigSource config, Control control)
    {
        Configuration configuration = config.loadConfig(Configuration.class);
        Flow flow = configuration.getFlowDescription();
        ConfigDiff configDiff = Exec.newConfigDiff();
        control.run(configuration.dump(), buildSchema(flow), 1);
        return configDiff;
    }

    @Override
    public ConfigDiff resume(TaskSource taskSource, Schema schema, int taskCount, Control control)
    {
        List<TaskReport> run = control.run(taskSource, schema, taskCount);
        ConfigDiff configDiff = Exec.newConfigDiff();
        return configDiff;
    }

    @Override
    public void cleanup(TaskSource taskSource, Schema schema, int taskCount, List<TaskReport> successTaskReports)
    {

    }

    @Override
    public TaskReport run(TaskSource taskSource, Schema schema, int taskIndex, PageOutput output)
    {
        PageBuilder pageBuilder = new PageBuilder(Exec.getBufferAllocator(), schema, output);
        FlowExecutor flowExecutor = buildFlowExcecutor(pageBuilder);
        Configuration configuration = taskSource.loadTask(Configuration.class);
        Flow flowDescription = configuration.getFlowDescription();
        ExecutionContext executionContext = new SimpleExecutionContext();
        executionContext.putAll(configuration.getExecutionContext().or(new HashMap<>()));
        executionContext.put("pageBuilder", pageBuilder);
        executionContext.put("schema", schema);
        FlowExecutionResult flowExecutionResult = flowExecutor.execute(flowDescription, executionContext);
        TaskReport taskReport = Exec.newTaskReport();
        taskReport.set("executionResult", flowExecutionResult);

        pageBuilder.finish();
        return taskReport;
    }

    @Override
    public ConfigDiff guess(ConfigSource config)
    {
        return null;
    }

    private Schema buildSchema(Flow flow)
    {
        //TODO implement this
        return flow.getSchema();
    }
    private FlowExecutor buildFlowExcecutor(PageBuilder pageBuilder)
    {
        Map<String, Step> stepFactory = new HashMap<>();
        StepExecutor stepExecutor = new StepExecutor(stepFactory);
        FlowExecutor flowExecutor = new FlowExecutor(stepExecutor);
        Injector childInjector = Exec.getInjector().createChildInjector(new AbstractModule()
        {
            @Override
            protected void configure()
            {
                bind(FlowExecutor.class).toInstance(flowExecutor);
                bind(PageBuilder.class).toInstance(pageBuilder);
                bind(StepExecutor.class).toInstance(stepExecutor);
            }
        });
        stepFactory.put("groovyRunner", new GroovyValueExtractor());
        stepFactory.put("importerStep", new ImporterStep());
        stepFactory.put("jsonPathExtractor", new JsonPathExtractor());
        stepFactory.put("restClientStep", new RestClientStep(new OkHttpClient()));
        stepFactory.put("basicAuth", new BasicAuthenticator());
        stepFactory.put("oauth2", new OAuth2Authenticator());
        stepFactory.put("tokenAuth", new TokenAuthenticator());
        stepFactory.put("condition", new ConditionStep());
        stepFactory.put("jsonPathRecordExtractor", new RecordJsonExtractor());
        stepFactory.put("loop", new LoopStep());
        for (Step step : stepFactory.values()) {
            childInjector.injectMembers(step);
        }
        return flowExecutor;
    }
}
