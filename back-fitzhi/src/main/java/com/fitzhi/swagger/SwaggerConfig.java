package com.fitzhi.swagger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * <p>
 * Springfox-swagger configuration.
 * </p>
 * @see http://localhost:8080/swagger-ui/index.html
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	public static final Contact DEFAULT_CONTACT = new Contact("Frederic VIDAL", "http://fitzhi.com",
			"frederic.vidal@fitzhi.com");

	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("Fitzhì API", "Below stands the first Fitzhì API Description",
			"1.0", "fitzhi.com", DEFAULT_CONTACT, "GNU Affero General Public License v3.0",
			"https://www.gnu.org/licenses/agpl-3.0.en.html", Collections.emptyList());

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<String>(
			Arrays.asList("application/json", "application/xml"));

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_API_INFO).produces(DEFAULT_PRODUCES_AND_CONSUMES)
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES);
	}
}