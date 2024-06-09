package com.tenebrie.currency_converter.utils.exceptions;

import org.springframework.http.HttpStatus;

public class RestApiException extends RuntimeException {
	public HttpStatus status;
	public String errorMessage;

	public RestApiException(HttpStatus status, String errorMessage) {
		this.status = status;
		this.errorMessage = errorMessage;
	}
}
