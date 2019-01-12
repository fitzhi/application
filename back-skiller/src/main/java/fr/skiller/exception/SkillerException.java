/**
 * 
 */
package fr.skiller.exception;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Generic <b><u>Skiller</u></b> exception thrown within the back end. 
 */
public class SkillerException extends Exception {

	/**
	 * serialVersionUID from serialization purpose.
	 */
	private static final long serialVersionUID = -3215035508614048457L;

	/**
	 * Error code
	 */
	public final int errorCode;
	
	/**
	 * Error message
	 */
	public final String errorMessage;
	
	/**
	 * Initialization of this application specific exception.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public SkillerException(int errorCode, String errorMessage) {
		super (errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Initialization of this application specific exception.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public SkillerException(int errorCode, String errorMessage, Exception cause) {
		super (errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
}
