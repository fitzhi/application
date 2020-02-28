/**
 * 
 */
package com.fitzhi;

import java.util.concurrent.Executor;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fitzhi.service.StorageService;
import com.fitzhi.service.impl.storageservice.ApplicationStorageProperties;
import com.fitzhi.service.impl.storageservice.AuditAttachmentStorageProperties;
import com.fitzhi.source.crawler.git.GitCrawler;

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
	
	public static void main(String[] args) {
		LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Starting Backend 质 Fitzhì");
		SpringApplication.run(Application.class, args);
	}

	@Bean
    CommandLineRunner init(
    		@Qualifier("Application") StorageService storageServiceApplication,
    		@Qualifier("Attachment") StorageService storageServiceAttachment) {
        return (args) -> {
        	LoggerFactory.getLogger(Application.class.getCanonicalName()).info("StorageService initialization");
        	storageServiceApplication.init();
        	storageServiceAttachment.init();
          	
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Source code crawling settings : ");
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("--------------------------------");
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("Most of the settings below are configured inside the file 'applications.properties', which is just aside of tixhì.jar.");
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tFiles pattern on-boarded in the evaluation : ");
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t" + patternsInclusion);
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\tExternal directories which are excluded from the evaluation : ");
	        LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t" + dependenciesMarker);
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t" + ((collapseEmptyDirectory) ? "Directories should be collapsed" : "Directories should NOT be collaped"));
            LoggerFactory.getLogger(Application.class.getCanonicalName()).info("\t" + ((prefilterEligibility) ? "File eligibility is PREfiltered" : "File eligibility id POSTfiltered"));
        };
    }
	
    @Bean
    public SavingBackendService task() {
        return new SavingBackendService();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("SCMParser-");
        executor.initialize();
        return executor;
    }}