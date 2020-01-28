package com.tixhi.data.external;

public class BooleanDTO extends BaseDTO {

	private boolean result;

	/**
	 * Empty constructor
	 */
	public BooleanDTO() {
		this.setResult(true);
	}
	
	/**
	 * @param code error code
	 * @param message error message
	 */
	public BooleanDTO(int code, String message) {
		this.setResult(false);		
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

}
