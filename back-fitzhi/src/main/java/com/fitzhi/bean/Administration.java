/**
 * 
 */
package com.fitzhi.bean;

import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;

/**
 * Interface in charge of the administrative task.
 * @author Fr&eacute;d&eacute;ric VIDAL
 * 
 */
public interface Administration {

	/**
	 * Test if this is the very first access into Webkac.
	 * @return <code>true</code> if we are in the very first access.
	 */
	boolean isVeryFirstConnection();
	
	
	/**
	 * Keep the footprint of the very first access in Fitzhi.
	 * This method is invoked after the creation of the administrative user.
	 * @throws ApplicationException thrown if an IO problem occurs during the operation.
	 */
	void saveVeryFirstConnection() throws ApplicationException;

	/**
	 * Create an <u>empty</u> staff member just with a login and a password.
	 * @param login the new <u>unique</u> login.
	 * @param password the associated encrypted password.
	 * @return the newly created staff member.
	 * @throws ApplicationException if any problem occurs during the creation.
	 */
	Staff createNewUser(String login, String password) throws ApplicationException;
	
}
