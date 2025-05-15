package org.example.util;

import java.util.Map;
import org.example.types.Err;
import org.example.types.ErrKind;;

public interface Extractor<T> {
	public static<T> Result<T, Err> extract(Map<String, String[]> map) {
		return Result.err(new Err(ErrKind.Unreachable, "Do not use this"));
	}
}
