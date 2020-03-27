/**
 * 
 */
package com.fitzhi.data.external;

import com.fitzhi.data.internal.Task;
import com.fitzhi.data.internal.TaskLog;

import lombok.Data;

/**
 * <p>
 * This class is regularly passed to the front-end in order to inform of the progression of the treatment.
 * </p> 
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public @Data class ActivityLog  {

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
	 * 
	 * @param code the 
	 * @param message
	 * @param logTime
	 * @param complete
	 * @param completeOnError
	 */
	public ActivityLog(int code, String message, long logTime, boolean complete, boolean completeOnError) {
		super();
		this.code = code;
		this.message = message;
		this.logTime = logTime;
		this.complete = complete;
		this.completeOnError = completeOnError;
	}

	/**
	 * @param taskLog the log message inside a {@link Task}
	 * @param complete {@code true} if the task is completed, {@code false} otherwise
	 */
	public ActivityLog(TaskLog taskLog, boolean complete) {
		super();
		this.code = taskLog.getCode();
		this.message = taskLog.getMessage();
		this.logTime = taskLog.getLogTime();
		this.complete = complete;
	}
	
	@Override
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
		if (logTime != other.logTime)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + (int) (logTime ^ (logTime >>> 32));
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ActivityLog [code=" + code + ", message=" + message + ", logTime=" + logTime + ", complete=" + complete
				+ ", completeOnError=" + completeOnError + "]";
	}

	
}

