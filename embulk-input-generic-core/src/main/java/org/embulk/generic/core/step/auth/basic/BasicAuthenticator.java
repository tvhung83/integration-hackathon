package org.embulk.generic.core.step.auth.basic;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.step.auth.Authenticator;

public class BasicAuthenticator extends Authenticator
{
    public interface BasicAuthTask extends AuthTask
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
        BasicAuthTask task = config.loadConfig(BasicAuthTask.class);
        return "Basic " + BaseEncoding.base64().encode((task.getUsername() + ":" + task.getPassword()).getBytes(Charsets.UTF_8));
    }
}
