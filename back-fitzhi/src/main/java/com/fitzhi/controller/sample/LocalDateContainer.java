/**
 * 
 */
package com.fitzhi.controller.sample;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * <p>
 * This class is hosting a {@link LocalDate}. It is created to test the JSON serialization of LocalDate.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public @Data class LocalDateContainer implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -8322238659420033377L;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate localDate;
	
	/**
	 * Simple constructor.
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 */
	public LocalDateContainer(int year, int month, int dayOfMonth) {
		this.localDate = LocalDate.of(year, month, dayOfMonth);
	}
	
	
}
