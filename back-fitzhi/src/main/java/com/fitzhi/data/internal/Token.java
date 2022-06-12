package com.fitzhi.data.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Connection token. 
 */
@NoArgsConstructor
@AllArgsConstructor
public @Data class Token {
	// (Sonar) The token is declared here as it is returned by the authentication process.
	private String access_token; //NOSONAR
	private String refresh_token; //NOSONAR
	private String token_type; //NOSONAR
	private int expires_in; //NOSONAR
	private String scope;
}

