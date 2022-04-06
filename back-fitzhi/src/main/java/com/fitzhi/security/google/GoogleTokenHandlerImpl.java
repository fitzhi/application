package com.fitzhi.security.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.data.internal.OpenIdServer;
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

import static com.fitzhi.Error.CODE_GOOGLE_TOKEN_ERROR;
import static com.fitzhi.Error.MESSAGE_GOOGLE_TOKEN_ERROR;

@Service
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

	private final static String googleServerId = "GOOGLE";

	private String clientId = null;

	@PostConstruct
	private void init() {
		try {
			List<OpenIdServer> servers = referentialHandlerOpenIdServer.loadReferential
				(openIdServersFilename, new TypeToken<List<OpenIdServer>>() {});
				Optional<OpenIdServer> oOpenIdServer = servers.stream()
					.filter(server -> googleServerId.equals(server.getClientId()))
					.findFirst();
				if (oOpenIdServer.isPresent()) {
					clientId = oOpenIdServer.get().getClientId();
				}
		} catch (ApplicationException e) {
			applicationException = e;
		}
	}

	@Override
	public boolean isDeclared() {
		return (clientId != null);
	}

	@Override
	public void takeInAccountToken(String idTokenString, HttpTransport transport, JsonFactory jsonFactory) throws ApplicationException {

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

				// Print user identifier
				String userId = payload.getSubject();
				System.out.println("User ID: " + userId);

				// Get profile information from payload
				String email = payload.getEmail();
				System.out.println(email);
				boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
				System.out.println(emailVerified);
				String name = (String) payload.get("name");
				System.out.println(name);
				String pictureUrl = (String) payload.get("picture");
				System.out.println(pictureUrl);
				String locale = (String) payload.get("locale");
				System.out.println(locale);
				String familyName = (String) payload.get("family_name");
				System.out.println(familyName);
				String givenName = (String) payload.get("given_name");
				System.out.println(givenName);
				
				Calendar calendar = Calendar.getInstance();
				System.out.println("getExpirationTimeSeconds() : " + payload.getExpirationTimeSeconds());
				calendar.setTimeInMillis(payload.getExpirationTimeSeconds() * 1000);
				final Date until = calendar.getTime();

				calendar.setTimeInMillis(payload.getIssuedAtTimeSeconds() * 1000);
				final Date from = calendar.getTime();

				System.out.println("from " + from + " until " + until);
				
				tokenStore.storeAccessToken(
					new OpenIdToOauth2Converter(idTokenString, idToken), 
					OAuth2AuthenticationBuilder.getInstance(
						OAuth2RequestBuilder.getInstance(userId, Set.of("read", "write", "trust")), 
						new GoogleAuthentication(userId, name)));

			} else {
				System.out.println("Invalid ID token.");
			}
		} catch (final GeneralSecurityException | IOException e) {
			throw new ApplicationException(CODE_GOOGLE_TOKEN_ERROR, MESSAGE_GOOGLE_TOKEN_ERROR, e);
		}
	}

}