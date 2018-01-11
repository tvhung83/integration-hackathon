package org.embulk.generic.auth;

import org.embulk.generic.auth.basic.BasicAuthentication;
import org.embulk.generic.auth.oauth2.OAuth2Authentication;
import org.embulk.generic.auth.token.TokenAuthentication;

public class AuthenticationFactory
{
    private AuthenticationFactory()
    {
    }

    public static Authentication getInstance(final String type)
    {
        switch (type) {
            case "basic":
                return new BasicAuthentication();
            case "token":
                return new TokenAuthentication();
            case "oauth2":
                return new OAuth2Authentication();
            default:
                throw new IllegalArgumentException("Unknown auth type: " + type);
        }
    }
}
