package com.fitzhi.security.token.github;

import static com.fitzhi.Error.CODE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Error.MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER;
import static com.fitzhi.Global.GITHUB_OPENID_SERVER;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ReferentialHandler;
import com.fitzhi.data.internal.OpenId;
import com.fitzhi.data.internal.OpenIdServer;
import com.fitzhi.data.internal.OpenIdToken;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.github.GithubIdentity;
import com.fitzhi.data.internal.github.GithubToken;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.security.token.TokenHandler;
import com.fitzhi.security.token.github.util.GithubIdTokenToOauth2Converter;
import com.fitzhi.security.token.util.OAuth2AuthenticationBuilder;
import com.fitzhi.security.token.util.OAuth2RequestBuilder;
import com.fitzhi.security.token.util.StaffAuthentication;
import com.fitzhi.security.token.util.TokenUtility;
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

		final Map<String, String> headers = new HashMap<>();
		headers.put("Accept", "application/json");
		headers.put("Content-type", "application/json");

		String url = MessageFormat.format(
			"https://github.com/login/oauth/access_token?code={0}&client_id={1}&client_secret={2}",
			idTokenString,
			"37e824ec4f90dcafe68e",
			"2f7c46c2c9fde5b1c7713f19e787f9b1243c6f6e");
		if (log.isDebugEnabled()) {
			log.debug("Accessing the url " + url);
		}

		GithubToken token = new TokenUtility<GithubToken>().httpLoadToken(POST, url, GithubToken.class, headers);
	
		url = "https://api.github.com/user";
		headers.put("Authorization", "token " + token.getAccess_token());
		GithubIdentity identity = new TokenUtility<GithubIdentity>().httpLoadToken(GET, url, GithubIdentity.class, headers);

		OpenIdToken openIdToken = OpenIdToken.of();
		openIdToken.setServerId(GITHUB_OPENID_SERVER);
		openIdToken.setUserId(identity.getId());
		openIdToken.setEmail(identity.getEmail());
		openIdToken.setName(identity.getName());
		openIdToken.setFamilyName(identity.getName());

		openIdToken.setOrigin(token);

		return openIdToken;
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

		if (openIdToken.getOrigin() instanceof GithubToken) {
			GithubToken googleIdToken = (GithubToken) openIdToken.getOrigin();
			tokenStore.storeAccessToken(
				new GithubIdTokenToOauth2Converter(googleIdToken, openId.getUserId()), 
				OAuth2AuthenticationBuilder.getInstance(
					OAuth2RequestBuilder.getInstance(openId.getUserId(), Set.of("read", "write", "trust")), 
					new StaffAuthentication(staff)));
		} else {
			throw new ApplicationException(
				CODE_INCONSISTENCY_ERROR_OPENID_SERVER, 
				MessageFormat.format(MESSAGE_INCONSISTENCY_ERROR_OPENID_SERVER, GITHUB_OPENID_SERVER, staff.getIdStaff(), staff.getFirstName(), staff.getLastName()));
		}

    }
}