package com.fitzhi.security.token;

import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

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
	 * @param idTokenString the token sent by the server. It might be :
	 * <ul>
	 * <li>a Java Web Token sent by the {@code Google} identity server</li>
	 * <li>a temporary code sent by the {@code Github} oauth server</li>
	 * </ul>
	 * @return the resulting {@link OpenIdToken}. This token might be flagged as error.
	 * @throws ApplicationException thrown if any technical problem occurs
	 */
	OpenIdToken takeInAccountToken(String idTokenString) throws ApplicationException;

	/**
	 * Store the authenticated user in the token store.
	 * 
	 * @param staff the authenticated staff member
	 * @param openIdToken the decoded JWT
	 * @throws ApplicationException thrown if any problem occurs
	 */
	void storeStaffToken (Staff staff, OpenIdToken openIdToken) throws ApplicationException;
}
