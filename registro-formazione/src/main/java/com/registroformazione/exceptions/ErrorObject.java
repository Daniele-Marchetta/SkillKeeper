package com.registroformazione.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorObject {

	private Integer statusCode;
	
	private long timestamp;

	private String message;
}
