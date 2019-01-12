/**
 * 
 */
package fr.skiller;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import fr.skiller.service.StorageService;
import fr.skiller.service.impl.storageservice.StorageProperties;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Starting class for the application
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		LoggerFactory.getLogger("back-end Skiller").info("Starting");
		SpringApplication.run(Application.class, args);
	}

	@Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
        	LoggerFactory.getLogger("back-end Skiller").info("StorageService initialization");
            storageService.init();
        };
    }
	
    @Bean
    public SavingBackendService task() {
        return new SavingBackendService();
    }
}
