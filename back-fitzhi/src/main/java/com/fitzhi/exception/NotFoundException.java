package com.fitzhi.exception;

public class NotFoundException extends SkillerException {

	/**
	 * For Serialization purpose.
	 */
	private static final long serialVersionUID = 2615145855180005274L;

	/**
	 * <p
	 * Initialization of an exception thrown if an entity is not found.
	 * </p>
	 * The controller is supposed to return a 404 status code.
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param args array of additional parameters.
	 */
	public NotFoundException(int errorCode, String errorMessage, Object... args) {
		super(errorCode, errorMessage, args);
	}	
}
