package com.tenebrie.currency_converter.service;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tenebrie.currency_converter.utils.exceptions.RestApiException;

@Service
public class SecretsService {
	Properties properties = null;

	void loadProperties() {
		var resource = new ClassPathResource("secret.properties");
		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			System.out.println(ex);
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to load secret.properties file.");
		}
	}

	void ensurePropertiesAreLoaded() {
		if (properties == null)
			loadProperties();
	}

	public String getSwopApiKey() {
		ensurePropertiesAreLoaded();
		return properties.getProperty("secrets.swop.apikey");
	}
}
