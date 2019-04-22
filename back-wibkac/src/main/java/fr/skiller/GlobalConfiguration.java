package fr.skiller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalConfiguration {

	private final Logger logger = LoggerFactory.getLogger(GlobalConfiguration.class.getCanonicalName());

	/**
	 * Origins allowed to access the server<br/>
	 * By default, for testing purpose, <b>*</b> is stored in the application properties file 
	 */
	@Value("${allowedOrigins}")
	private String allowedOrigins;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Allowed origins to access this server %s", allowedOrigins));
				}
				if (logger.isWarnEnabled() && ("*".equals(allowedOrigins))) {
					logger.warn("This server is open for all origins");
					logger.warn("-----------------------------------");
				}
				registry.addMapping("/**").allowedOrigins(allowedOrigins);
			}
		};
	}
}

