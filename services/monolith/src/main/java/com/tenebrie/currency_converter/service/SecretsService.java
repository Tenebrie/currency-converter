package com.tenebrie.currency_converter.service;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpStatus;
import org.springframework.javapoet.ClassName;
import org.springframework.stereotype.Service;

import com.tenebrie.currency_converter.utils.exceptions.RestApiException;

@Service
public class SecretsService {
	private static final Logger logger = Logger.getLogger(ClassName.class.getName());
	Properties properties = null;

	void loadProperties() {
		var resource = new ClassPathResource("secret.properties");
		try {
			properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Unable to load secret properties", ex);
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
