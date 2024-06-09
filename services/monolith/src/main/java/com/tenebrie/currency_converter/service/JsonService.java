package com.tenebrie.currency_converter.service;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tenebrie.currency_converter.utils.json.ErrorJson;

@Service
public class JsonService {
	Gson gson = new GsonBuilder().create();

	public String object(Object message) {
		return gson.toJson(message);
	}

	public String message(String message) {
		return gson.toJson(new ErrorJson(message));
	}

	public String error(String message) {
		return gson.toJson(new ErrorJson(message));
	}
}
