package com.fitzhi.security.google;

import static com.fitzhi.Error.CODE_GOOGLE_TOKEN_ERROR;
import static com.fitzhi.Error.MESSAGE_GOOGLE_TOKEN_ERROR;
import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.data.internal.OpenIdServer;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.google.util.GoogleAuthentication;
import com.fitzhi.security.google.util.OAuth2AuthenticationBuilder;
import com.fitzhi.security.google.util.OAuth2RequestBuilder;
import com.fitzhi.security.google.util.OpenIdToOauth2Converter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service("GOOGLE")
@Slf4j
public class GoogleTokenHandlerImpl implements TokenHandler {

	@Autowired
	private ReferentialHandler<OpenIdServer> referentialHandlerOpenIdServer; 

	@Autowired
	private TokenStore tokenStore;

	/**
	 * The exception catched at startup.
	 */
	ApplicationException applicationException = null;

	/**
	 * The name of the file containing the declared OpenId authentication servers. 
	 */
	private final static String openIdServersFilename = "openid-servers.json";

	private String clientId = null;

	@PostConstruct
	private void init() {
		try {
			List<OpenIdServer> servers = referentialHandlerOpenIdServer.loadReferential
				(openIdServersFilename, new TypeToken<List<OpenIdServer>>() {});
			Optional<OpenIdServer> oOpenIdServer = servers.stream()
				.filter(server -> GOOGLE_OPENID_SERVER.equals(server.getServerId()))
				.findFirst();
			if (oOpenIdServer.isPresent()) {
				clientId = oOpenIdServer.get().getClientId();
			}
		} catch (ApplicationException e) {
			log.error(e.getMessage(), e);
			applicationException = e;
		}
	}

	@Override
	public boolean isDeclared() {
		return (clientId != null);
	}

	@Override
	public OpenIdToken takeInAccountToken(String idTokenString, HttpTransport transport, JsonFactory jsonFactory) throws ApplicationException {

		// If an error occurs at startup.
		if (applicationException != null) {
			throw applicationException;
		}

		// If GOOGLE is not declared for this server.
		if (!isDeclared()) {
			throw new ApplicationRuntimeException("SHOULD NOT PASS HERE");
		}

		// "690807651852-sqjienqot7ui0pufj4ie4n320pss5ipc.apps.googleusercontent.com"
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
			.setAudience(Arrays.asList(this.clientId)) // Specify the list of CLIENT_IDs that accesses the backend:
			.build();

		// (Receive idTokenString by HTTPS POST)
		try {
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();

				OpenIdToken oit = OpenIdToken.of();

				oit.setServerId(GOOGLE_OPENID_SERVER);
				oit.setInError(false);
				oit.setUserId(payload.getSubject());
				oit.setEmail(payload.getEmail());
				oit.setEmailVerified(Boolean.valueOf(payload.getEmailVerified()));
				oit.setName((String) payload.get("name"));
				oit.setLocale((String) payload.get("locale"));
				oit.setFamilyName((String) payload.get("family_name"));
				oit.setGivenName((String) payload.get("given_name"));

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(payload.getExpirationTimeSeconds() * 1000);
				oit.setExpirationDate(calendar.getTime());

				calendar.setTimeInMillis(payload.getIssuedAtTimeSeconds() * 1000);
				oit.setIssuedDate(calendar.getTime());

				if (log.isDebugEnabled()) {
					log.debug("Token validation for %s %s %d", oit.getUserId(), oit.getName(), oit.getExpirationDate());
				}

				tokenStore.storeAccessToken(
					new OpenIdToOauth2Converter(idTokenString, idToken), 
					OAuth2AuthenticationBuilder.getInstance(
						OAuth2RequestBuilder.getInstance(oit.getUserId(), Set.of("read", "write", "trust")), 
						new GoogleAuthentication(oit.getUserId(), oit.getName())));

				return oit;

			} else {
				return OpenIdToken.error();
			}
		} catch (final GeneralSecurityException | IOException e) {
			throw new ApplicationException(CODE_GOOGLE_TOKEN_ERROR, MESSAGE_GOOGLE_TOKEN_ERROR, e);
		}
	}

	@Override
	public String getClientId() {
		return clientId;
	}

}