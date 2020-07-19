/**
 * 
 */
package com.fitzhi;

/**
 * <p>
 * Should not pass here exception
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ShouldNotPassHereRuntimeException extends SkillerRuntimeException {

	/**
	 * serialVersionUID as usual.
	 */
	private static final long serialVersionUID = 6239866080989386317L;

	public ShouldNotPassHereRuntimeException() {
		super("Should not pass here");
	}

}
