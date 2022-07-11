package com.fitzhi;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.service.StorageService;
import com.fitzhi.service.impl.storageservice.ApplicationStorageProperties;
import com.fitzhi.service.impl.storageservice.AuditAttachmentStorageProperties;
import com.fitzhi.source.crawler.git.GitCrawler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Starting class for the application
 */
@SpringBootApplication
@EnableConfigurationProperties({ApplicationStorageProperties.class, AuditAttachmentStorageProperties.class})
@EnableScheduling
@EnableAsync
@Slf4j
public class Application {

	@Autowired
	private Environment env;
	
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

	/** 
	 * This settings is used by the scheduler launched on the Main application to execute the experiences detection task.
	 * It is disable on the slave instance.
	 */
	@Value("${cron.experiences.detection}")
	private String cronExperencesDetection;
		
	@Autowired
	DataHandler dataHandler;

	/**
	 * Context obtained at startup.
	 */
	static ApplicationContext fitzhiContext;

	public static void main(String[] args) {
		log.info("Starting Backend 质 Fitzhì");
		SpringApplicationBuilder app = new SpringApplicationBuilder(Application.class);
	  	app.build().addListeners(new ApplicationPidFileWriter("./pid.file"));
	  	fitzhiContext = app.run(args);
	}
	
	@Bean
	CommandLineRunner init(
			@Qualifier("Application") StorageService storageServiceApplication,
			@Qualifier("Attachment") StorageService storageServiceAttachment) {
		return args -> {
			LoggerFactory.getLogger(Application.class.getCanonicalName()).info("StorageService initialization");
			storageServiceApplication.init();
			storageServiceAttachment.init();
		  	
			// Logger log = LoggerFactory.getLogger(Application.class.getCanonicalName());
			log.info("Source code crawling settings : ");
			log.info("--------------------------------");
			log.info("Most of the settings below are configured inside the file 'applications.properties', which is just aside of Fitzhì.jar.");
			log.info("\tFiles pattern on-boarded in the evaluation : ");
			log.info("\t {}", patternsInclusion);
			log.info("\t Inactivity delay : {}", this.inactivityDelay);
			log.info("\tExternal directories which are excluded from the evaluation : ");
			log.info("\t {}", dependenciesMarker);
			log.info(((collapseEmptyDirectory) ? "\tDirectories should be collapsed" : "\tDirectories should NOT be collaped"));
			log.info(((prefilterEligibility) ? "\tFile eligibility is PREfiltered" : "\tFile eligibility id POSTfiltered"));
			log.info( dataHandler.isLocal() ? "This is the main application" : "This is a slave");
			log.info( "\tCRON settings for the detection of experiences is \"{}\"", cronExperencesDetection);
			if (dataHandler.isLocal()) {
				log.info( (Boolean.valueOf(env.getProperty("autoProjectCreation"))) ? "Slave will create unexisting project." : "Slave will reject unexisting project.");
			}
			
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

	/**
	 * Schedule the termination of the server after a given delay in second
	 * @param exitCode the exit code that the server should return back to the OS after termination.
	 */
	public static void scheduleEnd(final int exitCode, final int delay) {
		// We gracefully halt the server after a very short delay.
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				System.exit(Application.end(exitCode));				
			} 
		}, delay, TimeUnit.SECONDS);
		if (log.isInfoEnabled()) {
			log.info("Termination of server is scheduled.");
		}
	}

	/**
	 * End the whole application instance.
	 * This method is supposed to be invoked only from slaves.
	 * @param exitCode the exit code to return
	 * @return the exit code to be sent back to os
	 */
	public static int end(final int exitCode) {
		SpringApplication.exit(fitzhiContext, new ExitCodeGenerator() {
			@Override
			public int getExitCode() {
				return exitCode;
			}
		});
		return exitCode;
	}
}
