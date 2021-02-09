package com.fitzhi;

/**
 * <p>
 * This class is the mother of all internal RuntimeException. 
 * If the application falls into a invalid critic state, then, the application should halt immediatly
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class ApplicationRuntimeException extends RuntimeException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -9153762874243559751L;

	/**
	 * Main constructor based on an exception.
	 * @param e the passed exception
	 */
	public ApplicationRuntimeException (Exception e) {
		super(e);
	}
	
	/**
	 * Main constructor based on an exception.
	 * @param e the passed exception
	 */
	public ApplicationRuntimeException (String errorMessage) {
		super(errorMessage);
	}
}
