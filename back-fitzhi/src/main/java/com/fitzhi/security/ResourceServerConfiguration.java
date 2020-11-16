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
	 * cache directory for intermediate files representing the repositories.
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
			http.authorizeRequests().antMatchers(
				"/api/staff/**", 
				"/api/skill/**", 
				
				// Server side event streaming is allowed
				"/api/project/tasks/stream/**",
				"/api/project/audit/**", 
				"/api/project/sonar/**", 
				"/api/project/**", 
				
				"/api/admin/settings",	

				// We allow the the springfox-swagger url to be accessible
				"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**", 
				

				"/api/admin/isVeryFirstConnection", 
				"/api/admin/saveVeryFirstConnection", 
				"/api/admin/veryFirstUser",
				"/api/admin/register",
				"/api/referential/**").permitAll()
				.antMatchers("/**").access("hasRole('USER')")
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		} else {
			http.authorizeRequests().antMatchers(
				// We allow the the springfox-swagger url to be accessible
				"/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**",

				// Server side event streaming is allowed
				"/api/project/tasks/stream/**",

				/* DO NOT KNOW IF IT SHOULD STAY HERE */
				"/api/project/tasks/**",

				"/api/admin/isVeryFirstConnection", 
				"/api/admin/saveVeryFirstConnection", 
				"/api/admin/veryFirstUser",
				"/api/admin/register",
				"/api/test/ping",
				"/api/project/all", 
				"/api/skill/all", 
				"/api/referential/**").permitAll()
				.antMatchers("/**/**").access("hasRole('USER')")
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
		}
	}
	
}