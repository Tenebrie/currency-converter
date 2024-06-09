package com.tenebrie.currency_converter.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tenebrie.currency_converter.utils.SwopCurrencyEntry;
import com.tenebrie.currency_converter.utils.exceptions.RestApiException;

@Service
public class SwopService {
	private static final Logger logger = Logger.getLogger(ClassName.class.getName());

	@Autowired
	SecretsService secretsService;

	HttpClient client = HttpClient.newHttpClient();
	long cacheUpdatedAt = -1;
	CompletableFuture<Void> cacheUpdateRequestFuture;

	Map<String, Double> conversionRates;

	Gson gson = new GsonBuilder().create();

	List<SwopCurrencyEntry> readValueGson(String content) {
		return Arrays.asList(gson.fromJson(content, SwopCurrencyEntry[].class));
	}

	void fetchData(boolean forceSync) {
		try {
			CompletableFuture<Void> responseFuture;

			if (cacheUpdateRequestFuture == null) {
				var request = HttpRequest.newBuilder(
						URI.create("https://swop.cx/rest/rates?base_currency=EUR"))
						.header("accept", "application/json")
						.header("Authorization", "ApiKey " + secretsService.getSwopApiKey())
						.build();

				responseFuture = client.sendAsync(request, BodyHandlers.ofString())
						.thenAccept(res -> {
							if (res.statusCode() == HttpStatus.OK.value()) {
								var entries = readValueGson(res.body());
								cacheUpdatedAt = System.currentTimeMillis();
								logger.log(Level.INFO,
										String.format("Fetched %d entries from swop.cx", entries.size()));

								var rates = new HashMap<String, Double>();
								rates.put("EUR", 1.0);
								entries.forEach(entry -> rates.put(entry.quote_currency, entry.quote));
								conversionRates = rates;
							} else {
								logger.log(Level.SEVERE, "Swop.cx returned status " + res.statusCode());
								logger.log(Level.SEVERE, res.body());
								throw new RuntimeException("Swop.cx connection error");
							}
						});

				cacheUpdateRequestFuture = responseFuture;

			} else {
				responseFuture = cacheUpdateRequestFuture;
			}

			if (forceSync)
				responseFuture.get();

		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.toString(), ex);
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load currency conversion quotes.");
		}
	}

	public void checkCacheValidity() {
		var time = System.currentTimeMillis();

		if (time - cacheUpdatedAt >= 600000) {
			fetchData(true);
		}
		if (time - cacheUpdatedAt >= 120000) {
			fetchData(false);
		}
	}

	public Set<String> getValidCurrencies() {
		checkCacheValidity();
		return conversionRates.keySet();
	}

	public Map<String, Double> getConversionRates() {
		checkCacheValidity();
		return conversionRates;
	}
}
