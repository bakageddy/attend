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
}
