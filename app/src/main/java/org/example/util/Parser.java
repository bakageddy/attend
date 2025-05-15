package org.example.util;

import org.example.types.Err;
import org.example.types.ErrKind;

// TODO: Add Documentation
public class Parser {
	// TODO: Add Documentation
	public static Result<Integer, Err> parse_int(String input) {
		try {
			if (input == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"Cannot parse integer from null"
				));
			}
			Integer res = Integer.valueOf(input);
			return Result.ok(res);
		} catch(NumberFormatException e) {
			return Result.err(new Err(
				ErrKind.NumberFormat,
				"String is not in the format of Integer"
			));
		}
	}

	// TODO: Add Documentation
	public static Result<Long, Err> parse_long(String input) {
		try {
			if (input == null) {
				return Result.err(new Err(
					ErrKind.Null,
					"Cannot parse long from null"
				));
			}
			Long res = Long.valueOf(input);
			return Result.ok(res);
		} catch(NumberFormatException e) {
			return Result.err(new Err(
				ErrKind.NumberFormat,
				"String is not in the format of Integer"
			));
		}
	}

	// TODO: Add Documentation
	public static Result<long[], Err> parse_long(String input[]) {
		try {
			long[] result = new long[input.length];
			for (int i = 0; i < input.length; i++) {
				Result<Long, Err> parsed_elem = parse_long(input[i]);
				if (parsed_elem.isErr()) {
					return Result.err(parsed_elem.err_msg());
				} else {
					result[i] = parsed_elem.unwrap();
				}
			}
			return Result.ok(result);
		} catch (OutOfMemoryError e) {
			return Result.err(new Err(
				ErrKind.OutOfMemory,
				"JVM Out of Memory"
			));
		}
	}
}
