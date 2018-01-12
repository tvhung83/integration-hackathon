package org.embulk.generic.core.step.auth.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.generic.core.step.auth.Authenticator;
import org.embulk.spi.DataException;

import java.io.IOException;

public class OAuth2Authenticator extends Authenticator
{
    public interface OAuth2Task extends AuthTask
    {
        @NotEmpty
        @Config("token_url")
        String getTokenUrl();

        @NotEmpty
        @Config("client_id")
        String getClientId();

        @NotEmpty
        @Config("client_secret")
        String getClientSecret();

        @Config("access_token")
        @ConfigDefault("null")
        Optional<String> getAccessToken();

        @NotEmpty
        @Config("refresh_token")
        String getRefreshToken();
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected String buildAuthHeader(ConfigSource config)
    {
        OAuth2Task task = config.loadConfig(OAuth2Task.class);
        // Build POST params
        RequestBody form = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("client_id", task.getClientId())
                .add("client_secret", task.getClientSecret())
                .add("refresh_token", task.getRefreshToken())
                .build();
        // Build request
        Request request = new Request.Builder()
                .url(task.getTokenUrl())
                .post(form)
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response == null || response.body() == null) {
                throw new IllegalArgumentException("FATAL - Failed to refresh token, response is null");
            }
            String body = response.body().string();
            JsonNode result = mapper.readTree(body);
            // Check expected `access_token` node
            if (!result.hasNonNull("access_token")) {
                throw new DataException("Unexpected response: \n" + body);
            }
            return "Bearer " + result.get("access_token").asText();
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
