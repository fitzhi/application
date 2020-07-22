/**
 * 
 */
package com.fitzhi.data.external;

import javax.annotation.Generated;

import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;

import lombok.Data;

/**
 * <p>
 * This class is regularly passed to the front-end in order to inform of the progression of the treatment.
 * </p> 
 * <p>
 * <font color="chocolate">
 * Be aware that the {@code equals} function has been overridden. The member variable {@code logTime} has been evicted from the comparison. <br/>
 * This tweak has been implemented for testing reason... :-(
 * </font>
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
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
	 * {@code true} if the task has been completed <font color="red">WITH AN EXCEPTION</font>
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
	 * @param logTime the data/time when this log has been recorded
	 * @param complete {@code true} if the treatment is completed.
	 * @param completeOnError{@code false} if the treatment is completed.
	 */
	public ActivityLog(int id, int code, String message, long logTime, boolean complete, boolean completeOnError) {
		super();
		this.id = id;
		this.code = code;
		this.message = message;
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
	}


	@Override
	public String toString() {
		return "ActivityLog [id=" + id + ", code=" + code + ", message=" + message + ", logTime=" + logTime
				+ ", complete=" + complete + ", completeOnError=" + completeOnError + "]";
	}

	@Override
	@Generated ("eclipse")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityLog other = (ActivityLog) obj;
		if (code != other.code)
			return false;
		if (complete != other.complete)
			return false;
		if (completeOnError != other.completeOnError)
			return false;
		if (id != other.id)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	@Generated ("eclipse")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + (completeOnError ? 1231 : 1237);
		result = prime * result + id;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}
	
	
}

