/**
 * 
 */
package fr.skiller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.exception.SkillerException;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public class SavingBackendService {
	
	/*
	 * Project handler 
	 */
	@Autowired
	ProjectHandler projectHandler;
	
	/*
	 * Service in charge of saving/loading data
	 */
	@Autowired
	DataSaver dataSaver;
	
	@Scheduled(fixedRateString="${dataSaver.timeDelay}")
    public void work() {
		try {
			dataSaver.save(projectHandler.getProjects());
		} catch (final SkillerException e) {
			throw new RuntimeException(e);
		}
    }
}
