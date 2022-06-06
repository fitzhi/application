package com.fitzhi.security;

import static com.fitzhi.Global.ROLE_TRUSTED_USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	private static String REALM="fitzhiOauthRealm";
	
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Duration of the access Token
	 */
	@Value("${accessTokenDuration}")
	private int accessTokenDuration;

	/**
	 * Duration of the access Token
	 */
	@Value("${refreshTokenDuration}")
	private int refreshTokenDuration;

	public static final String TRUSTED_CLIENT_USERNAME = "fitzhi-trusted-client";
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		// There is a //NOSONAR comment on this line, 
		// because the Security check from Sonar falsely detects a credential issue with the couple password/refresh_token.
		clients.inMemory()
			.withClient(TRUSTED_CLIENT_USERNAME)
			.authorizedGrantTypes("password", "refresh_token") //NOSONAR
			.authorities(ROLE_TRUSTED_USER)
			.scopes("read", "write", "trust")
			.secret(passwordEncoder.encode("secret"))
			.accessTokenValiditySeconds(accessTokenDuration) //Access token is only valid for 2 minutes.
			.refreshTokenValiditySeconds(refreshTokenDuration); //Refresh token is only valid for 1 hour.
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore)
				.userApprovalHandler(userApprovalHandler)
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.realm(REALM+"/client");
	}

}