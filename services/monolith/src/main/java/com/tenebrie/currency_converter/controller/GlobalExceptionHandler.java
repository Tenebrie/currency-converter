package com.tenebrie.currency_converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tenebrie.currency_converter.service.JsonService;
import com.tenebrie.currency_converter.utils.exceptions.RestApiException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	JsonService json;

	@ExceptionHandler(RestApiException.class)
	public ResponseEntity<String> handleRestApiException(HttpServletRequest req, RestApiException ex) {
		return ResponseEntity.status(ex.status).body(json.error(ex.errorMessage));
	}
}
