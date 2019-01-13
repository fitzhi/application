/**
 * 
 */
package fr.skiller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import fr.skiller.bean.DataSaver;
import fr.skiller.bean.ProjectHandler;
import fr.skiller.bean.StaffHandler;
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
	 * Staff handler 
	 */
	@Autowired
	StaffHandler staffHandler;
	
	/*
	 * Service in charge of saving/loading data
	 */
	@Autowired
	DataSaver dataSaver;
	
	@Scheduled(fixedRateString="${dataSaver.timeDelay}")
    public void work() {
		
		synchronized (projectHandler.getLocker()) {
			try {
				if (projectHandler.isDataUpdated()) {
					dataSaver.saveProjects(projectHandler.getProjects());
					projectHandler.dataAreSaved();
				}
			} catch (final SkillerException e) {
				throw new RuntimeException(e);
			}
		}
		
		synchronized (staffHandler.getLocker()) {
			try {
				if (staffHandler.isDataUpdated()) {
					dataSaver.saveStaff(staffHandler.getStaff());
					staffHandler.dataAreSaved();
				}
			} catch (final SkillerException e) {
				throw new RuntimeException(e);
			}
		}

    }
}
