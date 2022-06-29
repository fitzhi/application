/**
 * 
 */
package com.fitzhi;

import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fitzhi.exception.NotFoundException;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.InformationException;

/**
 * <p>
 * This class intercepts all {@link ApplicationException} or {@link InformationException}  thrown by the backend server 
 * and convert them to a valid HTTP response  .
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Profile("slave")
public class RestSlaveExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ApplicationException.class)
	protected ResponseEntity<Object> handleApplicationException(ApplicationException ex) {
		// We schedule the termination of the application.
		Application.scheduleEnd(ex.errorCode, 10);
		return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.errorCode,  ex.errorMessage, ex),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
		// We schedule the termination of the application.
		Application.scheduleEnd(ex.errorCode, 10);
		return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.errorCode,  ex.errorMessage, ex),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InformationException.class)
	protected ResponseEntity<Object> handleNotFoundException(InformationException ie) {
		throw new ApplicationRuntimeException("Should not pass here : InformationException is out of purpose for a slave instance.");
	}

}
