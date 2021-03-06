/**
 * 
 */
package com.fitzhi.data.external;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Base class DTO.
 */
public abstract class BaseDTO {
	/**
	 * Back-end code error
	 */
	protected int code = 0;
	
	/**
	 * Back-end message
	 */
	protected String message = "";

	/**
	 * @param code error code
	 * @param message error message
	 */
	public BaseDTO(final int code, final String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	/**
	 * Empty constructor.
	 */
	public BaseDTO() {
		// Empty constructor.
	}
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
}
