package com.registroformazione.exceptions;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("Not valid method argument !! {}", () -> HttpStatus.BAD_REQUEST.value());
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());

		List<String> errors = ex.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();

		body.put("errors", errors);
		return new ResponseEntity<>(body, headers, status);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler()
	public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
		log.error("Resource not Found !! {}", () -> HttpStatus.NOT_FOUND.value());
		ErrorObject e = new ErrorObject();
		e.setStatusCode(HttpStatus.NOT_FOUND.value());
		e.setMessage(ex.getMessage());
		e.setTimestamp(System.currentTimeMillis());

		return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);

	}

    @ResponseStatus(HttpStatus.NO_CONTENT)
	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleNoDataFoundException(NoDataFoundException ex) {
		log.warn("No data Found !! {}", () -> HttpStatus.NO_CONTENT.value());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleDatabaseException(DatabaseException ex) {
		log.error("Database exception , Conflict !! {}", () -> HttpStatus.CONFLICT.value());

		ErrorObject e = new ErrorObject();
		e.setStatusCode(HttpStatus.CONFLICT.value());
		e.setMessage(ex.getMessage());
		e.setTimestamp(System.currentTimeMillis());

		return new ResponseEntity<>(e, HttpStatus.CONFLICT);

	}
	
	   @ResponseStatus(HttpStatus.BAD_REQUEST)
	    @ExceptionHandler
	    public ResponseEntity<ErrorObject> handleFilterException(FilterException ex) {
	        log.error("FilterException: {}", () -> HttpStatus.BAD_REQUEST.value());
	        ErrorObject e = new ErrorObject();
	        e.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        e.setMessage(ex.getMessage());
	        e.setTimestamp(System.currentTimeMillis());

	        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);

	    }
	
}
