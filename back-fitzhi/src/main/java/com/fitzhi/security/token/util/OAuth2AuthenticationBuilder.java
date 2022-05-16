package com.fitzhi.security.token.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * Simple OAuth2Authentication builder.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class OAuth2AuthenticationBuilder {

    public static OAuth2Authentication getInstance(OAuth2Request request, Authentication userAuthentication) {
        return new OAuth2Authentication (request, userAuthentication);
    }

}
