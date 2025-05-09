package org.example.util;

import com.google.gson.Gson;

public class Serializer<T> {
	private static Gson serializer = null;
	public static<T> Result<String, String> serialize(T data) {
		try {
			if (serializer == null) {
				serializer = new Gson();
			}
			return Result.ok(serializer.toJson(data));
		} catch (Exception e) {
			return Result.err(e.getMessage());
		}
	}
}
