package com.fitzhi.security.token.google;

import static com.fitzhi.Error.CODE_GOOGLE_TOKEN_ERROR;
import static com.fitzhi.Error.CODE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Error.MESSAGE_GOOGLE_TOKEN_ERROR;
import static com.fitzhi.Error.MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Global.GOOGLE_OPENID_SERVER;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdServer;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.fitzhi.security.token.google.util.GoogleIdTokenToOauth2Converter;
import com.fitzhi.security.token.util.OAuth2AuthenticationBuilder;
import com.fitzhi.security.token.util.OAuth2RequestBuilder;
import com.fitzhi.security.token.util.StaffAuthentication;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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
	public OpenIdToken takeInAccountToken(String idTokenString) throws ApplicationException {

		// If an error occurs at startup.
		if (applicationException != null) {
			throw applicationException;
		}

		// If GOOGLE is not declared for this server.
		if (!isDeclared()) {
			throw new ApplicationRuntimeException("SHOULD NOT PASS HERE");
		}

		HttpTransport httpTransport = new NetHttpTransport();
		final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
			.setAudience(Arrays.asList(this.clientId)) // Specify the list of CLIENT_IDs that accesses the backend:
			.build();

		// (Receive idTokenString by HTTPS POST)
		try {
			GoogleIdToken googleToken = verifier.verify(idTokenString);
			if (googleToken != null) {
				Payload payload = googleToken.getPayload();

				OpenIdToken oit = OpenIdToken.of();

				oit.setServerId(GOOGLE_OPENID_SERVER);
				oit.setInError(false);
				oit.setUserId(payload.getSubject());
				oit.setEmail(payload.getEmail());
				oit.setEmailVerified(payload.getEmailVerified());
				oit.setName((String) payload.get("name"));
				oit.setLogin("");
				oit.setLocale((String) payload.get("locale"));
				oit.setFamilyName((String) payload.get("family_name"));
				oit.setGivenName((String) payload.get("given_name"));

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(payload.getExpirationTimeSeconds() * 1000);
				oit.setExpirationDate(calendar.getTime());

				calendar.setTimeInMillis(payload.getIssuedAtTimeSeconds() * 1000);
				oit.setIssuedDate(calendar.getTime());

				oit.setOrigin(googleToken);

				if (log.isDebugEnabled()) {
					log.debug("Token validation for %s %s %d", oit.getUserId(), oit.getName(), oit.getExpirationDate());
				}

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

	@Override
	public void storeStaffToken(Staff staff, OpenIdToken openIdToken) throws ApplicationException {

		Optional<OpenId> oOpenId = staff.getOpenIds()
			.stream()
			.filter(openId -> GOOGLE_OPENID_SERVER.equals(openId.getServerId()))
			.findFirst();
		if (oOpenId.isEmpty()) {
			throw new ApplicationRuntimeException("DATA INCONSISTENCY!");
		}

		OpenId openId = oOpenId.get();

		if (openIdToken.getOrigin() instanceof GoogleIdToken) {
			GoogleIdToken googleIdToken = (GoogleIdToken) openIdToken.getOrigin();
			tokenStore.storeAccessToken(
				new GoogleIdTokenToOauth2Converter(googleIdToken), 
				OAuth2AuthenticationBuilder.getInstance(
					OAuth2RequestBuilder.getInstance(openId.getUserId(), Set.of("read", "write", "trust")), 
					new StaffAuthentication(staff)));
		} else {
			throw new ApplicationException(
				CODE_INCONSISTENCY_ERROR_OPENID_SERVER, 
				MessageFormat.format(MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER, GOOGLE_OPENID_SERVER, staff.getIdStaff(), staff.getFirstName(), staff.getLastName()));
		}

    }
}