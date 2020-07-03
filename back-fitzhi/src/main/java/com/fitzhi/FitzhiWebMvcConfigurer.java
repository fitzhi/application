/**
 * 
 */
package com.fitzhi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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

	
	/**
	 * <p>
	 * In order to resolve <code>${...}</code> placeholders in <bean> definitions or <code>@Value</code> annotations using properties from a PropertySource, 
	 * one must register a <code>PropertySourcesPlaceholderConfigurer</code>.<br/> 
	 * This happens automatically when using <context:property-placeholder> in XML, but must be explicitly registered using a static code>@Bean</code> method 
	 * when using <code>@Configuration</code> classes. 
	 * </p>
	 * @return an instance of PropertySourcesPlaceholderConfigurer
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
	    return new PropertySourcesPlaceholderConfigurer();
	}
	
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
			.allowedOrigins(allowedOrigins);
	}
	
}
