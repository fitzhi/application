package com.fitzhi;

import java.util.Locale;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.service.StorageService;
import com.fitzhi.service.impl.storageservice.ApplicationStorageProperties;
import com.fitzhi.service.impl.storageservice.AuditAttachmentStorageProperties;
import com.fitzhi.source.crawler.git.GitCrawler;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Starting class for the application
 */
@SpringBootApplication
@EnableConfigurationProperties({ApplicationStorageProperties.class, AuditAttachmentStorageProperties.class})
@EnableScheduling
@EnableAsync
public class Application {

	
	/**
	 * @see {@link GitCrawler#patternsInclusion}
	 */
	@Value("${patternsInclusion}")
	private String patternsInclusion;

	/**
	 * @see {@link GitCrawler#dependenciesMarker}
	 */
	@Value("${dependenciesMarker}")
	private String dependenciesMarker;
	
	
	/**
	 * @see {@link GitCrawler#collapseEmptyDirectory}
	 */
	@Value("${collapseEmptyDirectory}")
	private boolean collapseEmptyDirectory;
	
	/**
	 * @see {@link GitCrawler#prefilterEligibility}
	 */
	@Value("${prefilterEligibility}")
	private boolean prefilterEligibility;
	
	/**
	 * Number of days of inactivity before inactivation of a staff member.
	 * @see StaffHandler#processActiveStatus
	 */
	@Value("${staffHandler.inactivity.delay}")
	private int inactivityDelay;	
	
	/**
	 * <p>
	 * <i>Optional</i> repositories location.
	 * </p>
	 */
	@Value("${gitcrawler.repositories.location:#{null}}")
	private String reposDir;

	
	public static void main(String[] args) {
		LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Starting Backend 质 Fitzhì");
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(
			@Qualifier("Application") StorageService storageServiceApplication,
			@Qualifier("Attachment") StorageService storageServiceAttachment) {
		return args -> {
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("StorageService initialization");
			storageServiceApplication.init();
			storageServiceAttachment.init();
		  	
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Source code crawling settings : ");
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("--------------------------------");
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Most of the settings below are configured inside the file 'applications.properties', which is just aside of Fitzhì.jar.");
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tFiles pattern on-boarded in the evaluation : ");
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t {}", patternsInclusion);
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t Inactivity delay : {}", this.inactivityDelay);
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tExternal directories which are excluded from the evaluation : ");
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t {}", dependenciesMarker);
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info(((collapseEmptyDirectory) ? "\tDirectories should be collapsed" : "\tDirectories should NOT be collaped"));
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info(((prefilterEligibility) ? "\tFile eligibility is PREfiltered" : "\tFile eligibility id POSTfiltered"));
			if (reposDir == null) {
				LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tLocal repositories are hosted in a temporary destination") ;
			} else {
				LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tLocal repositories are hosted in {}", reposDir);
			}
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tLocale : {}", Locale.getDefault());
			
		};
	}
	
	@Bean
	public SavingBackendService task() {
		return new SavingBackendService();
	}

	/**
	 * Create and return a filter in charge of the generation for etags.
	 * <code>
	 * https://www.baeldung.com/etags-for-rest-with-spring
	 * </code>
	 * @return the entity tag filter.
	 */
	@Bean
	public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
		return new ShallowEtagHeaderFilter();
	} 
}
