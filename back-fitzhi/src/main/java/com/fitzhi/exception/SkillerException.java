/**
 * 
 */
package com.fitzhi.exception;

import java.text.MessageFormat;

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
	
	public SkillerException() {
		this(0,"");
	}
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
	 * @param args array of additional parameters.
	 */
	public SkillerException(int errorCode, String errorMessage, Object... args) {
		super (errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = MessageFormat.format(errorMessage, args);
	}
	
	/**
	 * Initialization of this application specific exception.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param cause original exception
	 */
	public SkillerException(int errorCode, String errorMessage, Exception cause) {
		super (errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Initialization of this application specific exception.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param cause original exception
	 * @param args array of additional parameters.
	 */
	public SkillerException(int errorCode, String errorMessage, Exception cause, Object... args) {
		super (errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = MessageFormat.format(errorMessage, args);
	}
	
}
