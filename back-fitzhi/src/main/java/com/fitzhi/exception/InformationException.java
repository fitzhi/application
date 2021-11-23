package com.fitzhi.exception;

import java.text.MessageFormat;

/**
 * <p>
 * Generic <b><u>Information</u></b> exception thrown by the backend.
 * </p>
 * <p>
 * This exception is thrown only by the applicaiton REST controllers to inform a client about its process status.
 * </p>
 * <p>Status codes 1xx : https://httpstatuses.com/102
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class InformationException extends Exception {

	/**
	 * serialVersionUID from serialization purpose.
	 */
	private static final long serialVersionUID = -3215089908614348457L;

	/**
	 * Information code
	 */
	public final int informationCode;
	
	/**
	 * Information message
	 */
	public final String informationMessage;
		
	/**
	 * Initialization of this application specific exception.
	 * @param informationCode the information code
	 * @param informationMessage the information message
	 */
	public InformationException(int informationCode, String informationMessage) {
		super (informationMessage);
		this.informationCode = informationCode;
		this.informationMessage = informationMessage;
	}

	/**
	 * Initialization of this Information specific exception.
	 * @param informationCode the information code
	 * @param informationMessage the information message
	 * @param args array of additional parameters.
	 */
	public InformationException(int informationCode, String informationMessage, Object... args) {
		super (informationMessage);
		this.informationCode = informationCode;
		this.informationMessage = MessageFormat.format(informationMessage, args);
	}
	
	/**
	 * Initialization of this Information specific exception.
	 * @param informationCode the information code
	 * @param informationMessage the information message
	 * @param cause original exception
	 */
	public InformationException(int informationCode, String informationMessage, Exception cause) {
		super (informationMessage, cause);
		this.informationCode = informationCode;
		this.informationMessage = informationMessage;
	}

	/**
	 * Initialization of this Information specific exception.
     * 
	 * @param informationCode the information code
	 * @param informationMessage the information message
	 * @param cause original exception
	 * @param args array of additional parameters.
	 */
	public InformationException(int informationCode, String informationMessage, Exception cause, Object... args) {
		super (informationMessage, cause);
		this.informationCode = informationCode;
		this.informationMessage = MessageFormat.format(informationMessage, args);
	}
	
}
