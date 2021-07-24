package com.fitzhi.exception;

import java.text.MessageFormat;

/**
 * <p>
 * Generic <b><u>Application</u></b> exception thrown by the backend.
 * This is <em>the exception to rule them all</em>
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ApplicationException extends Exception {

	/**
	 * serialVersionUID from serialization purpose.
	 */
	private static final long serialVersionUID = -3115089903614398457L;

	/**
	 * Error code
	 */
	public final int errorCode;
	
	/**
	 * Error message
	 */
	public final String errorMessage;
	
	public ApplicationException() {
		this(0,"");
	}
	/**
	 * Initialization of this application specific exception.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public ApplicationException(int errorCode, String errorMessage) {
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
	public ApplicationException(int errorCode, String errorMessage, Object... args) {
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
	public ApplicationException(int errorCode, String errorMessage, Exception cause) {
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
	public ApplicationException(int errorCode, String errorMessage, Exception cause, Object... args) {
		super (errorMessage, cause);
		this.errorCode = errorCode;
		this.errorMessage = MessageFormat.format(errorMessage, args);
	}
	
}
