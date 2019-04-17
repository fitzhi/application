/**
 * 
 */
package fr.skiller.bean;

import fr.skiller.data.internal.Staff;
import fr.skiller.exception.SkillerException;

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
	 * Keep the footprint of the very first access in Wibkac.</br>
	 * This method is invoked after the creation of the administrative user.
	 * @throws SkillerException thrown if an IO problem occurs during the operation.
	 */
	void saveVeryFirstConnection() throws SkillerException;

	/**
	 * Create an <u>empty</u> staff member just with a login and a password.
	 * @param login the new <u>unique</u> login.
	 * @param password the associated encrypted password.
	 * @return the newly created staff member.
	 * @throws SkillerException if any problem occurs during the creation.
	 */
	Staff createNewUser(String login, String password) throws SkillerException;
}
