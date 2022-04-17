package com.fitzhi.security.token.github;

import static com.fitzhi.Error.CODE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Error.MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Global.GITHUB_OPENID_SERVER;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.data.internal.GithubToken;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdServer;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.fitzhi.security.token.TokenUtility;
import com.fitzhi.security.token.google.util.GoogleAuthentication;
import com.fitzhi.security.token.google.util.OAuth2AuthenticationBuilder;
import com.fitzhi.security.token.google.util.OAuth2RequestBuilder;
import com.fitzhi.security.token.google.util.OpenIdToOauth2Converter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * This class is the implementation for <strong>Github</strong> of the {@link TokenHandler} interface. 
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("GITHUB")
@Slf4j
public class GithubTokenHandlerImpl implements TokenHandler {

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
				.filter(server -> GITHUB_OPENID_SERVER.equals(server.getServerId()))
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

		// If GITHUB is not declared for this server.
		if (!isDeclared()) {
			throw new ApplicationRuntimeException("SHOULD NOT PASS HERE");
		}

		GithubToken token = new TokenUtility<GithubToken>().httpLoadToken("url", GithubToken.class);

		return null;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public void storeStaffToken(Staff staff, OpenIdToken openIdToken) throws ApplicationException {

		Optional<OpenId> oOpenId = staff.getOpenIds()
			.stream()
			.filter(openId -> GITHUB_OPENID_SERVER.equals(openId.getServerId()))
			.findFirst();
		if (oOpenId.isEmpty()) {
			throw new ApplicationRuntimeException("DATA INCONSISTENCY!");
		}

		OpenId openId = oOpenId.get();

		if (openIdToken.getOrigin() instanceof GoogleIdToken) {
			GoogleIdToken googleIdToken = (GoogleIdToken) openIdToken.getOrigin();
			tokenStore.storeAccessToken(
				new OpenIdToOauth2Converter(googleIdToken), 
				OAuth2AuthenticationBuilder.getInstance(
					OAuth2RequestBuilder.getInstance(openId.getUserId(), Set.of("read", "write", "trust")), 
					new GoogleAuthentication(staff)));
		} else {
			throw new ApplicationException(
				CODE_INCONSISTENCY_ERROR_OPENID_SERVER, 
				MessageFormat.format(MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER, GITHUB_OPENID_SERVER, staff.getIdStaff(), staff.getFirstName(), staff.getLastName()));
		}

    }
}