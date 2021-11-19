package com.fitzhi.data.external;

import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * This class is regularly passed to the front-end in order to inform of the progression of the treatment.
 * </p> 
 * <p>
 * Be aware that the {@code equals} function has been overridden. The member variable {@code logTime} has been evicted from the comparison. <br/>
 * This tweak has been implemented for testing reason... :-(
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@EqualsAndHashCode
public @Data class ActivityLog  {

	/**
	 * Entity identifier, most probably the project identifier. 
	 */
	int id;
	
	/**
	 * the time-stamp of the event.
	 */
	private long logTime;

	/**
	 * The progression of the treatment (in percentage).
	 */
	private int progressionPercentage;

	/**
	 * The error code thrown by the treatment, or 0 if there is no error.
	 */
	private int code;

	/**
	 * The message corresponding to the progression.
	 */
	private String message;
	
	/**
	 * {@code true} if the task has been successfully completed, {@code false} otherwise.
	 */
	boolean complete;

	/**
	 * <ul>
	 * <li>
	 * {@code true} if the task has been completed WITH AN EXCEPTION
	 * </li>
	 * <li>
	 * {@code false} otherwise.
	 * </li>
	 * </ul>
	 */
	boolean completeOnError = false;
	
	/**
	 * @param id an entity identifier (in this first release, it's a project identifier)
	 * @param code the code of error associated to this message
	 * @param message the message
     * @param progressionPercentage the percentage of progression of the treatment
	 * @param logTime the data/time when this log has been recorded
	 * @param complete {@code true} if the treatment is completed.
	 * @param completeOnError{@code false} if the treatment is completed.
	 */
	public ActivityLog(int id, int code, String message, int progressionPercentage, long logTime, boolean complete, boolean completeOnError) {
		super();
		this.id = id;
		this.code = code;
		this.message = message;
		this.progressionPercentage = progressionPercentage;
		this.logTime = logTime;
		this.complete = complete;
		this.completeOnError = completeOnError;
	}

	/**
	 * @param id an entity identifier (in this first release, it's a project identifier)
	 * @param taskLog the log message inside a {@link Task}
	 * @param complete {@code true} if the task is completed, {@code false} otherwise
	 */
	public ActivityLog(int id, TaskLog taskLog, boolean complete) {
		super();
		this.id = id;
		this.code = taskLog.getCode();
		this.message = taskLog.getMessage();
		this.logTime = taskLog.getLogTime();
		this.complete = complete;
		if (this.complete) {
			this.progressionPercentage = 100;
		}
	}


	@Override
	public String toString() {
		return "ActivityLog [id=" + id + ", code=" + code + ", message=" + message+ ", progressionPercentage=" + progressionPercentage + ", logTime=" + logTime
				+ ", complete=" + complete + ", completeOnError=" + completeOnError + "]";
	}

}

