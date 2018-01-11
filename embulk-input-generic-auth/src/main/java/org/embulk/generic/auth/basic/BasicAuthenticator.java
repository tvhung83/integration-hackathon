package org.embulk.generic.auth.basic;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.auth.Authentication;

public class BasicAuthentication extends Authentication
{
    public interface BasicTask extends Task
    {
        @NotEmpty
        @Config("username")
        String getUsername();

        @NotEmpty
        @Config("password")
        String getPassword();
    }

    @Override
    protected String buildAuthHeader(ConfigSource config)
    {
        BasicTask task = config.loadConfig(BasicTask.class);
        return "Basic " + BaseEncoding.base64().encode((task.getUsername() + ":" + task.getPassword()).getBytes(Charsets.UTF_8));
    }
}
