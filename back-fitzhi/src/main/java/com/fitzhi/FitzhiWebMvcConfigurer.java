/**
 * 
 */
package com.fitzhi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * This class is configuring the application.
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */


@Configuration
@Slf4j
public class FitzhiWebMvcConfigurer implements WebMvcConfigurer {

	/**
	 * Origins allowed to access the server<br/>
	 * By default, for testing purpose, <b>*</b> is stored in the application properties file 
	 */
	@Value("${allowedOrigins}")
	private String allowedOrigins;

	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if (log.isInfoEnabled()) {
			log.info(String.format("Allowed origins to access this server %s", allowedOrigins));
		}
		if (log.isWarnEnabled() && ("*".equals(allowedOrigins))) {
			log.warn("This server is open for all origins");
			log.warn("-----------------------------------");
		}
		registry.addMapping("/**")
			.allowedOrigins(allowedOrigins)
			.exposedHeaders(Global.BACKEND_RETURN_CODE, Global.BACKEND_RETURN_MESSAGE);
	}
	
}
