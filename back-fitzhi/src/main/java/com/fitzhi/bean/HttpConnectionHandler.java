package com.fitzhi.bean;

import com.fitzhi.exception.ApplicationException;

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

}
