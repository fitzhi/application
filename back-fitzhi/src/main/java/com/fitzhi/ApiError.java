/**
 * 
 */
package com.fitzhi;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * <p>
 * This class is producing the default response content in case of error.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class ApiError {

	/**
	 * This flag is there to allow the Angular application to detect this object.
	 * Angular front-end will make a test like error.hasOwnProperty('flagApiError')
	 */
	private int flagApiError = 1;

	private HttpStatus status;

	/**
	 * Timestamp of error.
	 */
	private String timestamp;

	/*
	 * End-user code message.
	 */
	private int code;

	/*
	 * End-user error message.
	 */
	private String message;

	/**
	 * Developer error message
	 */
	private String debugMessage;

	private ApiError() {
		timestamp =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status HTTP status
	 */
	public ApiError(HttpStatus status) {
		this();
		this.status = status;
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status  HTTP status
	 * @param message error message
	 * @param ex      exception thrown
	 */
	public ApiError(HttpStatus status, Throwable ex) {
		this(status, "Unexpected error", ex);
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status  HTTP status
	 * @param message error message
	 * @param ex      exception thrown
	 */
	public ApiError(HttpStatus status, String message, Throwable ex) {
		this(status, 0, message, ex);
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status  HTTP status
	 * @param code    error code
	 * @param message error message
	 * @param ex      exception thrown
	 */
	public ApiError(HttpStatus status, int code, String message, Throwable ex) {
		this();
		this.code = code;
		this.status = status;
		this.message = message;
		this.debugMessage = this.extrapolateStackTrace(ex);
	}

	private String extrapolateStackTrace(Throwable ex) {
	    Throwable e = ex;
	    StringBuilder trace =  new StringBuilder(e.toString()).append("\n");
	    for (StackTraceElement e1 : e.getStackTrace()) {
	        trace.append("\t at ").append(e1.toString()).append("\n");
	    }
	    while (e.getCause() != null) {
	        e = e.getCause();
	        trace.append("Cause by: ").append(e.toString()).append("\n");
	        for (StackTraceElement e1 : e.getStackTrace()) {
	            trace.append("\t at ").append(e1.toString()).append("\n");
	        }
	    }
	    return trace.toString();
	}	
}