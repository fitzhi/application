package com.tixhi;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class SkillerRuntimeException extends  RuntimeException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -9153762874243559751L;

	/**
	 * Main constructor based on an exception.
	 * @param e the passed exception
	 */
	public SkillerRuntimeException (Exception e) {
		super(e);
	}
	
	/**
	 * Main constructor based on an exception.
	 * @param e the passed exception
	 */
	public SkillerRuntimeException (String errorMessage) {
		super(errorMessage);
	}
}
