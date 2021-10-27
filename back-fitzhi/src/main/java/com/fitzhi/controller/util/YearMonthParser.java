package com.fitzhi.controller.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * This class is in charge of the control 
 * that a couple (month, year) is valid to create a date.
 */
public class YearMonthParser {
 
	public static boolean isValid(int year, int month) {

		boolean valid = false;

		try {

			// ResolverStyle.STRICT for 30, 31 days checking, and also leap year.
			LocalDate.parse(String.format("%d-%d-1", year, month),
				DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT)
			);

			valid = true;

		} catch (DateTimeParseException e) {
			valid = false;
		}

		return valid;
	}
}
