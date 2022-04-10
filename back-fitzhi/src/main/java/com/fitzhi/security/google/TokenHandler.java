package com.fitzhi.security.google;

import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
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
     * get the <b>client Identifier</b> for this  authentication server <em>(if any)</em>.
     */
    String getClientId();

    /**
     * Take in account the given token.
     * @param idTokenString the Java Web Token received from this server
     * @param transport the type of transport
     * @param jsonFactory The JSON factory to decode the JSON string
     * @return the resulting {@link OpenIdToken}. This token might be in error.
     * @throws ApplicationException thrown if any technical problem occurs
     */
    OpenIdToken takeInAccountToken(String idTokenString, HttpTransport transport, JsonFactory jsonFactory) throws ApplicationException;

    /**
     * Store the authenticated user in the token store.
     * 
     * @param staff the authenticated staff member
     * @param openIdToken the decoded JWT
     * @throws ApplicationException thrown if any problem occurs
     */
    void storeStaffToken (Staff staff, OpenIdToken openIdToken) throws ApplicationException;
}
