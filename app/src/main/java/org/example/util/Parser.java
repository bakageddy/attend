package org.example.util;

import java.util.Optional;

public class Parser {
	public static Optional<Long> parse_long(String input) {
		try {
			return Optional.of(Long.parseLong(input));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	public static Optional<long[]> parse_long(String input[]) {
		try {
			long[] result = new long[input.length];
			for (int i = 0; i < input.length; i++) {
				Optional<Long> parsed_elem = parse_long(input[i]);
				if (parsed_elem.isEmpty()) {
					throw new NumberFormatException();
				} else {
					result[i] = parsed_elem.get();
				}
			}
			return Optional.of(result);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}
}
