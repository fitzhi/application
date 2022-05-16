package com.fitzhi.data.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a token retrieved from an OpenId authentication server.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public @Data class OpenIdCredentials {
	private String openIdServer;
	private String idToken;
}
