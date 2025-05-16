package org.example.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

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

	public <R> Result<R, E> map(Function<T, R> f) {
		if (this.isErr()) {
			return Result.err(this.err_msg());
		} else {
			R result = f.apply(this.unwrap());
			return Result.ok(result);
		}
	}

	public <F> Result<T, F> map_err(Function<E, F> f) {
		if (this.isOk()) {
			return Result.ok(this.unwrap());
		} else {
			F result = f.apply(this.err_msg());
			return Result.err(result);
		}
	}

	public <R> Result<R, E> and_then(Function<T, Result<R, E>> f) {
		if (this.isErr()) {
			return Result.err(this.err_msg());
		} else {
			Result<R, E> result = f.apply(this.unwrap());
			return result;
		}
	}

	public <F> Result<T, F> or_else(Function<E, Result<T, F>> f) {
		if (this.isOk()) {
			return Result.ok(this.unwrap());
		} else {
			Result<T, F> result = f.apply(this.err_msg());
			return result;
		}
	}

	public Optional<T> asOption() {
		return type == Variant.Ok ? Optional.of(this.data) : Optional.empty();
	}

	public static <T, E> Result<T, E> fromOption(Optional<T> o) {
		if (o.isPresent()) {
			return Result.ok(o.get());
		} else {
			return Result.err(null);
		}
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
