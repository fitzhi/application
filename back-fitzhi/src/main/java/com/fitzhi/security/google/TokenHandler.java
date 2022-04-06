package com.fitzhi.security.google;

import com.fitzhi.exception.ApplicationException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * Interface in charge of handling the 
 */
public interface TokenHandler {
     
    /**
     * @return {@code true} if this OpenId authentication server has been declared in Fitzhi.
     */
    boolean isDeclared();

    /**
     * Take in account the given token.
     * @param idTokenString the Java Web Token received from this server
     * @param transport the type of transport
     * @param jsonFactory The JSON factory to decode the JSON string
     * @throws ApplicationException thrown if any problem occurs
     */
    void takeInAccountToken(String idTokenString, HttpTransport transport, JsonFactory jsonFactory) throws ApplicationException;
}
