/**
 * 
 */
package com.fitzhi;

import java.time.LocalDateTime;

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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

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
		timestamp = LocalDateTime.now();
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status HTTP status
	 */
	ApiError(HttpStatus status) {
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
	ApiError(HttpStatus status, Throwable ex) {
		this(status, "Unexpected error", ex);
	}

	/**
	 * Public construction of an ApiError.
	 * 
	 * @param status  HTTP status
	 * @param message error message
	 * @param ex      exception thrown
	 */
	ApiError(HttpStatus status, String message, Throwable ex) {
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
	ApiError(HttpStatus status, int code, String message, Throwable ex) {
		this();
		this.code = code;
		this.status = status;
		this.message = message;
		this.debugMessage = this.extrapolateStackTrace(ex);
	}

	private String extrapolateStackTrace(Throwable ex) {
	    Throwable e = ex;
	    String trace = e.toString() + "\n";
	    for (StackTraceElement e1 : e.getStackTrace()) {
	        trace += "\t at " + e1.toString() + "\n";
	    }
	    while (e.getCause() != null) {
	        e = e.getCause();
	        trace += "Cause by: " + e.toString() + "\n";
	        for (StackTraceElement e1 : e.getStackTrace()) {
	            trace += "\t at " + e1.toString() + "\n";
	        }
	    }
	    return trace;
	}	
}