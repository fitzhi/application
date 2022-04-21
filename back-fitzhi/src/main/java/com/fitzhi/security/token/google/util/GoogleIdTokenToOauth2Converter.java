package com.fitzhi.security.token.google.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * Simple OAuth2AccessToken builder.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class GoogleIdTokenToOauth2Converter implements OAuth2AccessToken {

	final GoogleIdToken googleIdToken;

	/**
	 * Creation of an instance of {@link OAuth2AccessToken} based on a Google token.
	 * @param googleIdToken the Goggle Identifer token decoded from the JWT
	 */
	public GoogleIdTokenToOauth2Converter(GoogleIdToken googleIdToken) {
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
		return  googleIdToken.getHeader().getType();
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
		return googleIdToken.getPayload().getSubject();
	}
 
}
