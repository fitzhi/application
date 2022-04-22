package com.fitzhi.security.token.github.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.fitzhi.data.internal.github.GithubToken;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

/**
 * Simple OAuth2AccessToken instance based on the Github token.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
public class GithubIdTokenToOauth2Converter implements OAuth2AccessToken {

	final GithubToken githubToken;

	final String userId;

	/**
	 * The GITHUB token is supposed to have an 8 hours lifetime.
	 */
	final Calendar expiration;

	/**
	 * Creation of an instance of {@link OAuth2AccessToken} based on a Github token.
	 * @param githubIdToken the Github  token
	 */
	public GithubIdTokenToOauth2Converter(GithubToken githubToken, String userId) {
		this.githubToken = githubToken;
		this.userId = userId;
		expiration = Calendar.getInstance();
		expiration.add(Calendar.HOUR_OF_DAY, 8);
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
		return githubToken.getToken_type();
	}

	@Override
	public boolean isExpired() {
		Calendar calendar = Calendar.getInstance();
		return (calendar.getTime().after(getExpiration()));
	}

	@Override
	public Date getExpiration() {
		return expiration.getTime();
	}

	@Override
	public int getExpiresIn() {
		Calendar calendar = Calendar.getInstance();
		return (int) (calendar.getTime().getTime() - getExpiration().getTime()) / 1000;
	}

	@Override
	public String getValue() {
		return userId;
	}
 
}
