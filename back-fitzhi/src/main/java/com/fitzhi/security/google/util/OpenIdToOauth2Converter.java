package com.fitzhi.security.google.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * Simple OAuth2Request builder.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class OpenIdToOauth2Converter implements OAuth2AccessToken {

	final String idToken;

	final GoogleIdToken googleIdToken;

	public OpenIdToOauth2Converter(String idToken, GoogleIdToken googleIdToken) {
		this.idToken = idToken;
		this.googleIdToken = googleIdToken;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return null;
	}

	@Override
	public Set<String> getScope() {
		return Set.of("read", "write", "trust");
	}

	@Override
	public OAuth2RefreshToken getRefreshToken() {
		return null;
	}

	@Override
	public String getTokenType() {
		return "OpenID";
	}

	@Override
	public boolean isExpired() {
		Calendar calendar = Calendar.getInstance();
		return (calendar.getTime().after(getExpiration()));
	}

	@Override
	public Date getExpiration() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(googleIdToken.getPayload().getExpirationTimeSeconds() * 1000);
		return calendar.getTime();
	}

	@Override
	public int getExpiresIn() {
		Calendar calendar = Calendar.getInstance();
		return (int) (calendar.getTime().getTime() - getExpiration().getTime()) / 1000;
	}

	@Override
	public String getValue() {
		// We use the ID token as an ACCESS token. 
		return idToken;
	}
 


}
