package com.tenebrie.currency_converter.utils;

public class CurrencyValueValidationResult {
	public boolean isValid;
	public double value;

	public CurrencyValueValidationResult(boolean isValid, double value) {
		this.isValid = isValid;
		this.value = value;
	}
}
