package com.fitzhi.data.internal;

import lombok.Data;

public @Data class Settings {
	
	/**
	 * URL of the instance of SONAR
	 */
	private final String[] urlSonar;
	
	/**
	 * Settings builder.
	 * @param declaredUrlSonar URL of the Sonar server declared in the application
	 */
	public Settings (String declaredUrlSonar) {
		this.urlSonar = declaredUrlSonar.split(",");
	}
}
