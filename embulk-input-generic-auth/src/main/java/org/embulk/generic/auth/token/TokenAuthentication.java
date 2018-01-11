package org.embulk.generic.auth.token;

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
        @Config("access_token")
        String getAccessToken();
    }

    @Override
    protected String buildCredential(ConfigSource config)
    {
        TokenTask task = config.loadConfig(TokenTask.class);
        return "Bearer " + task.getAccessToken();
    }
}
