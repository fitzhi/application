package com.fitzhi.security.google.util;

import java.util.HashMap;
import java.util.Set;

import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * Simple OAuth2Request builder.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class OAuth2RequestBuilder {
    
    public static OAuth2Request getInstance(String clientId, Set<String> scope) {
        return new OAuth2Request(
            new HashMap<String, String>(), 
            clientId,
            null, 
            true, 
            scope,
            null, 
            "redirectUri", 
            null,
            null);
    }

}
