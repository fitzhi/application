package com.fitzhi.security.google.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External received from an external openID authentication server.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@NoArgsConstructor
public @Data class ExternalToken {
	private String idToken;
	private String authentifier;
}
