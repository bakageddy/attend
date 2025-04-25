package org.example.util;

import java.util.Optional;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

public class Validator {
	public static Optional<String> validate_sql(String input) {
		if (input.contains("DROP") ||
				input.contains("SELECT") ||
				input.contains("UPDATE") ||
				input.contains("INSERT") ||
				input.contains(";")) {
			return Optional.empty();
		} else {
			return Optional.of(input);
		}
	}

	public static boolean validate_period(String input) {
		return (
			input.equals("I")	||
			input.equals("II")	||
			input.equals("III") ||
			input.equals("IV")	||
			input.equals("V")	||
			input.equals("VI")	||
			input.equals("VII") ||
			input.equals("VIII")
		);
	}

	public static Optional<String> validate_date(String input) {
		try {
			DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Date _ignore = fmt.parse(input);
			if (_ignore == null) return Optional.empty();
			return Optional.of(input);
		} catch (Exception e) {
			return Optional.empty();
		}
	} 
}
