package org.embulk.generic.core.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.embulk.spi.Schema;

import java.util.Map;

/**
 * Created by tai.khuu on 1/11/18.
 */
public class Flow
{
    private Map<String, StepConfig> steps;

    private Schema schema;

    public Map<String, StepConfig> getSteps()
    {
        return steps;
    }

    public StepConfig getStep(String nextStep)
    {
        return steps.get(nextStep);
    }
    public void setSteps(Map<String, StepConfig> steps)
    {
        this.steps = steps;
    }

    @JsonIgnore
    public String getFirstStep()
    {
        for (String stepName : steps.keySet()) {
            return stepName;
        }
        return null;
    }

    public Schema getSchema()
    {
        return schema;
    }

    public void setSchema(Schema schema)
    {
        this.schema = schema;
    }
}
