package org.embulk.generic.auth.token;

import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigSource;
import org.embulk.generic.auth.Authentication;

/**
 * This authentication make use of a permanent access token
 * It uses request header "Authorization: Bearer access_token"
 */
public class TokenAuthentication extends Authentication
{
    public interface TokenTask extends Task
    {
        @NotEmpty
        @Config("access_token")
        String getAccessToken();
    }

    @Override
    protected String buildAuthHeader(ConfigSource config)
    {
        TokenTask task = config.loadConfig(TokenTask.class);
        return "Bearer " + task.getAccessToken();
    }
}
