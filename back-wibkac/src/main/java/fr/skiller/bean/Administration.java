/**
 * 
 */
package fr.skiller.bean;

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

}
