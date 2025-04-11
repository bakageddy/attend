package org.example.util;

import java.util.NoSuchElementException;

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

	public E err_msg() throws Exception {
		if (type == Variant.Ok) {
			throw new NoSuchElementException();
		}
		return err;
	}

	public boolean isOk() {
		switch (type) {
			case Variant.Ok:
				return true;
			default:
				return false;
		}
	}

	public boolean isErr() {
		switch (type) {
			case Variant.Err:
				return true;
			default:
				return false;
		}
	}

	public static<T, E> Result<T, E> ok(T data) {
		Result result = new Result(Variant.Ok);
		result.data = data;
		return result;
	}
	public static<T, E> Result<T, E> err(E err) {
		Result result = new Result(Variant.Err);
		result.err = err;
		return result;
	}
}
