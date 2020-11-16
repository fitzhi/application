/**
 * 
 */
package com.fitzhi;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fitzhi.exception.NotFoundException;
import com.fitzhi.exception.SkillerException;

/**
 * <p>
 * This class is handling the SkillerException thrown by the application.
 * </p>
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(SkillerException.class)
	protected ResponseEntity<Object> handleSkillerException(SkillerException ex) {
		return new ResponseEntity<>(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.errorCode,  ex.errorMessage, ex),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
		return new ResponseEntity<>(new ApiError(HttpStatus.NOT_FOUND, ex.errorCode,  ex.errorMessage, ex),
				HttpStatus.NOT_FOUND);
	}
}
