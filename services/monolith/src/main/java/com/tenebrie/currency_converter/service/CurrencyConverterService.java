package com.tenebrie.currency_converter.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Service;

import com.tenebrie.currency_converter.utils.CurrencyValueValidationResult;
import com.tenebrie.currency_converter.utils.CurrencyConversionResult;
import com.tenebrie.currency_converter.utils.exceptions.RestApiException;

@Service
public class CurrencyConverterService {
	private static final Logger logger = Logger.getLogger(ClassName.class.getName());

	@Autowired
	SwopService swopService;

	public CurrencyValueValidationResult validateAndCastCurrencyValue(String value) {
		try {
			var parsedValue = Double.parseDouble(value);
			return new CurrencyValueValidationResult(true, parsedValue);
		} catch (NumberFormatException ex) {
			logger.log(Level.SEVERE, "Unable to parse input number", ex);
			return new CurrencyValueValidationResult(false, 0);
		}
	}

	public boolean isCurrencyValid(String currency) {
		return swopService.getValidCurrencies().stream()
				.anyMatch(validCurrency -> validCurrency.equalsIgnoreCase(currency));
	}

	public CurrencyConversionResult convert(double monetaryAmount, String sourceCurrency, String targetCurrency) {
		var rates = swopService.getConversionRates();

		var rateToEur = rates.get(sourceCurrency.toUpperCase());
		var rateToTarget = rates.get(targetCurrency.toUpperCase());

		if (rateToEur == null || rateToTarget == null) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "Unsupported currency conversion");
		}

		// Convert value to EUR first, then to target currency
		var exchangeRate = (1 / rateToEur) * rateToTarget;
		var convertedValue = monetaryAmount * exchangeRate;

		var result = new CurrencyConversionResult();
		result.sourceCurrency = sourceCurrency.toUpperCase();
		result.targetCurrency = targetCurrency.toUpperCase();
		result.exchangeRate = Math.round(exchangeRate * 10000.0) / 10000.0;
		result.originalValue = Math.round(monetaryAmount * 100.0) / 100.0;
		result.convertedValue = Math.round(convertedValue * 100.0) / 100.0;
		result.operationDate = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

		return result;
	}
}
