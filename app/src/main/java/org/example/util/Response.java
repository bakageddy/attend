package org.example.util;

import java.io.IOException;
import java.io.Writer;

import org.example.types.Err;
import org.example.types.ErrKind;

public class Response {
	public static Result<Void, Err> send(String payload, Writer dst) {
		try {
			dst.write(payload);
			return Result.ok(null);
		} catch (IOException e) {
			return Result.err(new Err(
				ErrKind.IOError,
				"Cannot send payload: " + e.getMessage()
			));
		}
	}

	public static Result<Void, Err> send(long payload, Writer dst) {
		try {
			dst.write(String.valueOf(payload));
			return Result.ok(null);
		} catch (IOException e) {
			return Result.err(new Err(
				ErrKind.IOError,
				"Cannot send payload: " + e.getMessage()
			));
		}
	}
}
