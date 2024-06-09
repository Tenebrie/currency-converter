package com.tenebrie.currency_converter;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tenebrie.currency_converter.service.SecretsService;
import com.tenebrie.currency_converter.service.SwopService;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyConverterApplicationTests {
	Gson gson = new GsonBuilder().create();

	@Autowired
	MockMvc mockMvc;

	@MockBean
	SwopService swopService;
	@MockBean
	SecretsService secretsService;

	@Test
	void currencies_providesCurrencies() throws Exception {
		var mockValue = new HashSet<String>();
		mockValue.add("EUR");
		mockValue.add("USD");
		when(swopService.getValidCurrencies()).thenReturn(mockValue);

		var request = get("/currencies");
		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(gson.toJson(mockValue)));
	}

	@Test
	void convert_providesConvertedValue_eurToUsd() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "EUR")
				.param("target", "USD")
				.param("value", "1");

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.sourceCurrency").value("EUR"))
				.andExpect(jsonPath("$.targetCurrency").value("USD"))
				.andExpect(jsonPath("$.convertedValue").value("1.2"))
				.andExpect(jsonPath("$.exchangeRate").value("1.2"));
	}

	@Test
	void convert_providesConvertedValue_usdToEur() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "USD")
				.param("target", "EUR")
				.param("value", "1");

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.convertedValue").value("0.83"));
	}

	@Test
	void convert_providesConvertedValue_usdToGbp() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "USD")
				.param("target", "GBP")
				.param("value", "1");

		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.convertedValue").value("0.67"));
	}

	@Test
	void convert_doesNotAcceptInvalidSourceCurrency() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "ASD")
				.param("target", "GBP")
				.param("value", "1");

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Unsupported source currency"));
	}

	@Test
	void convert_doesNotAcceptInvalidTargetCurrency() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "USD")
				.param("target", "ASD")
				.param("value", "1");

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Unsupported target currency"));
	}

	@Test
	void convert_doesNotAcceptInvalidValue() throws Exception {
		var mockValue = new HashMap<String, Double>();
		mockValue.put("EUR", 1.0);
		mockValue.put("USD", 1.2);
		mockValue.put("GBP", 0.8);
		when(swopService.getConversionRates()).thenReturn(mockValue);
		when(swopService.getValidCurrencies()).thenReturn(mockValue.keySet());

		var request = get("/convert")
				.param("source", "USD")
				.param("target", "GBP")
				.param("value", "not-a-number");

		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid currency value"));
	}
}
