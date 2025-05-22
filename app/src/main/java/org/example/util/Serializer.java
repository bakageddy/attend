package org.example.util;

import java.io.Writer;

import org.example.types.Err;
import org.example.types.ErrKind;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class Serializer<T> {
	private static Gson serializer = null;
	public static<T> Result<String, Err> serialize(T data) {
		try {
			if (serializer == null) {
				serializer = new Gson();
			}
			String payload = serializer.toJson(data);
			// System.out.println(payload);
			return Result.ok(payload);
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.JsonSerializeError,
				"Failed to serialize object: " + e.getMessage()
			));
		}
	}

	public static<T> Result<Void, Err> serialize_to(T src, Writer dst) {
		try {
			if (serializer == null) {
				serializer = new Gson();
			}
			String data = serializer.toJson(src);
			dst.write(data);
			dst.flush();
			return Result.ok(null);
		} catch (JsonIOException e) {
			return Result.err(new Err(
				ErrKind.IOError,
				"Failed to serialize object to writer: " + e.getMessage()
			));
		} catch (Exception e) {
			return Result.err(new Err(
				ErrKind.Unreachable,
				e.getMessage()
			));
		}
	}
}
