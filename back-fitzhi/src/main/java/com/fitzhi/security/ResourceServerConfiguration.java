package com.fitzhi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "my_rest_api";

	/**
	 * For development purpose, the security is unplugged for development purpose if this settings is equal to 1.
	 */
	@Value("${development.unplugged.security}")
	private String developmentUnpluggedSecurity;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).stateless(false);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		if ("1".equals(developmentUnpluggedSecurity)) {
			http.authorizeRequests().antMatchers("/**").permitAll()
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		} else {
			http.authorizeRequests().antMatchers(
				// We allow the the springfox-swagger url to be accessible
				"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**",

				// Server side event streaming is allowed
				"/api/project/tasks/stream/**",

				// DO NOT KNOW IF IT SHOULD STAY HERE...
				"/api/project/tasks/**",

				"/api/admin/isVeryFirstConnection", 
				"/api/admin/saveVeryFirstConnection", 
				"/api/admin/veryFirstUser",
				"/api/admin/register",
				"/api/test/ping",

				// All projets are broadcasted ? (it's a question. I do not know why the complete list of projects has to to be broadcast)
				"/api/project", 

				// All skills are broadcasted !
				"/api/skill", 

				"/api/referential/**")
			.permitAll()
			.antMatchers("/**").hasRole("TRUSTED_USER")
			.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		}
	}
	
}