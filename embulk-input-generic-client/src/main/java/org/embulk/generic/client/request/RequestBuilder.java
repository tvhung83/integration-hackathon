package org.embulk.generic.client.request;

import okhttp3.Request;
import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.Task;
import org.embulk.generic.core.step.Step;

/**
 * Output is request, ready to be sent
 */
public interface RequestBuilder extends Step<Object, Request>
{
    interface RequestTask extends Task
    {
        @NotEmpty
        @Config("url")
        String getUrl();

        @Config("method")
        @ConfigDefault("GET")
        String getMethod();
    }
}
