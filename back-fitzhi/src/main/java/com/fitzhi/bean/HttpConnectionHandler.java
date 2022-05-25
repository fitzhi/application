package com.fitzhi.bean;

import com.fitzhi.data.internal.Token;
import com.fitzhi.exception.ApplicationException;

import org.apache.http.client.HttpClient;

/**
 * Interface in charge of the slave-authentication to the Fitzhi server.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public interface HttpConnectionHandler {
	
	/**
	 * Try to authentication to the server with the login/password pair. 
	 * 
	 * @param login the <b>login</b> to be used for the authentication
	 * @param pass the <b>password</b> to be used for the authentication
	 * @throws ApplicationException thrown if any authentication of network error occurs.
	 */
	void connection(String login, String pass) throws ApplicationException;

	/**
	 * @return the authentication token.
	 */
	Token getToken();

	/**
	 * This method exists only <u>for testing purpose</u>, in order to inject a mock a HttpClient.
	 * @param httpClient the HTTP client to be used.
	 */
	void setHttpClient(HttpClient httpClient);

}
