package com.fitzhi.data.internal;

import lombok.Data;

/**
 * <p>
 * A Log message inside a {@link Task}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class TaskLog {
	
	/**
	 * an identifier.
	 */
	long logTime;

	/**
	 * The error code.
	 */
	int code;

	/**
	 * The message.
	 */
	String message;
	
	/**
	 * Public build with a logTime set to <code> System.currentTimeMillis()</code> and an error code equal to -1.
	 * @param message the log message to be stored.
	 */
	public TaskLog(String message) {
		this(0, message);
	}
	
	/**
	 * Public build with a logTime set to <code> System.currentTimeMillis()</code>
	 * @param code optional code associated to this message. <i>This code will be mainly used to keep the error code.</i>
	 * @param message the log message to be stored.
	 */
	public TaskLog(int code, String message) {
		this.message = message;
		this.code = code;
		this.logTime = System.currentTimeMillis();
	}
}
