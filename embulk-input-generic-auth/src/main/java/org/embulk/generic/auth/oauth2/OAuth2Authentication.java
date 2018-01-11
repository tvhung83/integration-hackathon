package org.embulk.generic.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.bval.constraints.NotEmpty;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.generic.auth.Authentication;

import java.io.IOException;

public class OAuth2Authentication extends Authentication
{
    public interface OAuth2Task extends Authentication.Task
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
        // Build URL and GET params
        HttpUrl.Builder urlBuider = HttpUrl.parse(task.getTokenUrl()).newBuilder();
        urlBuider.addQueryParameter("grant_type", "refresh_token");
        urlBuider.addQueryParameter("client_id", task.getClientId());
        urlBuider.addQueryParameter("client_secret", task.getClientSecret());
        urlBuider.addQueryParameter("refresh_token", task.getRefreshToken());
        // Build request
        Request request = new Request.Builder()
                .url(urlBuider.build())
                .build();

        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response != null && response.body() != null) {
                return "Bearer " + mapper.readTree(response.body().string()).get("access_token").asText();
            }
            throw new IllegalArgumentException("FATAL - Failed to refresh token, response is null");
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
