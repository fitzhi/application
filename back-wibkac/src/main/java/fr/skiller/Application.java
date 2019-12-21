/**
 * 
 */
package fr.skiller;

import java.util.concurrent.Executor;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import fr.skiller.service.StorageService;
import fr.skiller.service.impl.storageservice.StorageProperties;
import fr.skiller.source.crawler.git.GitCrawler;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Starting class for the application
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
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
	
	private final String BACKEND_TECHXHI = "Backend techxhi";
	
	public static void main(String[] args) {
		LoggerFactory.getLogger("back-end Wibkac").info("Starting");
		SpringApplication.run(Application.class, args);
	}

	@Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
        	LoggerFactory.getLogger(BACKEND_TECHXHI).info("StorageService initialization");
            storageService.init();
          	
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("Source code crawling settings : ");
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("--------------------------------");
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("\tFiles pattern embarked in the evaluation : ");
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("\t" + patternsInclusion);
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("\tExternal directories excluded from the evaluation : ");
	        LoggerFactory.getLogger(BACKEND_TECHXHI).info("\t" + dependenciesMarker);
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("\t" + ((collapseEmptyDirectory) ? "Directories should be collapsed" : "Directories should NOT be collaped"));
            LoggerFactory.getLogger(BACKEND_TECHXHI).info("\t" + ((prefilterEligibility) ? "File eligibility is PREfiltered" : "File eligibility id POSTfiltered"));
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
