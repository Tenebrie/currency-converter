package com.tenebrie.currency_converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenebrie.currency_converter.service.CurrencyConverterService;
import com.tenebrie.currency_converter.service.JsonService;
import com.tenebrie.currency_converter.service.SwopService;

@RestController
public class CurrencyConverterController {
	
	@Autowired
	CurrencyConverterService currencyConverterService;
	@Autowired
	SwopService swopService;
	@Autowired
	JsonService json;

	@GetMapping(value = "/currencies", produces = "application/json")
	public ResponseEntity<String> currencies() {
		var knownCurrencies = swopService.getValidCurrencies();
		return ResponseEntity.status(HttpStatus.OK)
				.body(json.object(knownCurrencies));
	}

	@GetMapping(value = "/convert", produces = "application/json")
	public ResponseEntity<String> convert(
			@RequestParam(value = "source") String sourceCurrency,
			@RequestParam(value = "target") String targetCurrency,
			@RequestParam(value = "value") String valueToConvert) {

		var valueValidationResult = currencyConverterService.validateAndCastCurrencyValue(valueToConvert);
		if (!valueValidationResult.isValid) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(json.error("Invalid currency value"));
		}

		boolean isSourceCurrencyValid = currencyConverterService.isCurrencyValid(sourceCurrency);
		if (!isSourceCurrencyValid) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(json.error("Unsupported source currency"));
		}
		boolean isTargetCurrencyValid = currencyConverterService.isCurrencyValid(targetCurrency);
		if (!isTargetCurrencyValid) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(json.error("Unsupported target currency"));
		}

		var conversionResult = currencyConverterService.convert(valueValidationResult.value, sourceCurrency,
				targetCurrency);
		return ResponseEntity.status(HttpStatus.OK).body(json.object(conversionResult));
	}
}
