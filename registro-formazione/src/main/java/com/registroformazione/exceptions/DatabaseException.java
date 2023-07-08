package com.registroformazione.exceptions;

@SuppressWarnings("serial")
public class DatabaseException extends RuntimeException {

	public DatabaseException(String message) {
		super(message);
	}

}
