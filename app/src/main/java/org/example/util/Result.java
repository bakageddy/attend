package org.example.util;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Result<T, E> {
	enum Variant {
		Ok,
		Err,
	}

	private Variant type;
	private T data;
	private E err;

	private Result(Variant v) {
		this.type = v;
	}

	public T unwrap() throws NoSuchElementException {
		if (type == Variant.Err) {
			throw new NoSuchElementException();
		}
		return data;
	}

	public E err_msg() throws NoSuchElementException {
		if (type == Variant.Ok) {
			throw new NoSuchElementException();
		}
		return err;
	}

	public boolean isOk() {
		return type == Variant.Ok;
	}

	public boolean isErr() {
		return type == Variant.Err;
	}

	public Optional<T> asOption() {
		return type == Variant.Ok ? Optional.of(this.data) : Optional.empty();
	}

	public static <T, E> Result<T, E> ok(T data) {
		Result<T, E> result = new Result<>(Variant.Ok);
		result.data = data;
		return result;
	}

	public static <T, E> Result<T, E> err(E err) {
		Result<T, E> result = new Result<>(Variant.Err);
		result.err = err;
		return result;
	}

}
