package org.example.util;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

import org.example.types.Err;
import org.example.types.ErrKind;

import jakarta.servlet.http.HttpServletResponse;

public class Response {
	private static int err_to_status(Err e) {
		switch (e.kind) {
			case ElementNotFound:
				return HttpServletResponse.SC_NO_CONTENT;
			case OutOfMemory:
			case ClassNotFound:
			case IllegalState:
			case DBTimeout:
			case IOError:
			case JsonSerializeError:
				return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			default:
				return HttpServletResponse.SC_BAD_REQUEST;
		}
	}

	public static Result<Void, Err> send(String payload, Writer dst) {
		try {
			dst.write(payload);
			dst.flush();
			payload = null;
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

	public static<T> Result<T, Void> send_err(HttpServletResponse resp, Err e) {
		try {
			resp.sendError(err_to_status(e), e.toString());
			resp.flushBuffer();
			return Result.ok(null);
		} catch (IOException ex) {
			return Result.err(null);
		}
	}

	public static<T> Result<T, Void> send_err(HttpServletResponse resp, Err e, Function<Err, Integer> f) {
		try {
			resp.sendError(f.apply(e), e.toString());
			resp.flushBuffer();
			return Result.ok(null);
		} catch (IOException ex) {
			return Result.err(null);
		}
	}
}
