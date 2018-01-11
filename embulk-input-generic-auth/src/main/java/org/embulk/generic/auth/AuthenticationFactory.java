package org.embulk.generic.auth;

import org.embulk.generic.auth.basic.BasicAuthentication;
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
            default:
                throw new IllegalArgumentException("Unknown auth type: " + type);
        }
    }
}
