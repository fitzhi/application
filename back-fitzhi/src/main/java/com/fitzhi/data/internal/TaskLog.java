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
	 * The error code thrown by the treatment, or 0 if there is no error.
	 */
	int code;

	/**
	 * The message.
	 */
	String message;
	
	/**
	 * The progression of the treatment in percentage.
	 */
	int progressionPercentage;

	/**
	 * Public build with a logTime set to <code> System.currentTimeMillis()</code> and an error code equal to 0.
	 * @param message the log message to be stored.
	 * @param progressionPercentage The progression of the treatment in percentage
	 */
	public TaskLog(String message, int progressionPercentage) {
		this(0, message, 0);
	}
	
	/**
	 * Public build with a logTime set to <code> System.currentTimeMillis()</code>
	 * @param code optional code associated to this message. <i>This code will be mainly used to keep the error code.</i>
	 * @param message the log message to be stored.
	 * @param progressionPercentage The progression of the treatment in percentage
	 */
	public TaskLog(int code, String message, int progressionPercentage) {
		this(code, message, progressionPercentage, System.currentTimeMillis());
	}
	
	/**
	 * Public build with a logTime set to <code> System.currentTimeMillis()</code>
	 * @param code optional code associated to this message. <i>This code will be mainly used to keep the error code.</i>
	 * @param message the log message to be stored.
	 * @param progressionPercentage The progression of the treatment in percentage
	 * @param logTime the time when the event occurs
	 */
	public TaskLog(int code, String message, int progressionPercentage, long logTime) {
		this.message = message;
		this.code = code;
		this.progressionPercentage = progressionPercentage;
		this.logTime = logTime;
	}

}
