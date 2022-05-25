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
	private String access_token;
	private String refresh_token;
	private String token_type;
	private int expires_in;
	private String scope;
}

