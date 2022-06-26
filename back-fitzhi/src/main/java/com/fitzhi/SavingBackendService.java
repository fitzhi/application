package com.fitzhi;

import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Slf4j
public class SavingBackendService {
	
	/*
	 * Project handler 
	 */
	@Autowired
	ProjectHandler projectHandler;
	
	/*
	 * Project audit handler 
	 */
	@Autowired
	ProjectAuditHandler projectAuditHandler;
	
	/*
	 * Staff handler 
	 */
	@Autowired
	StaffHandler staffHandler;
	
	/*
	 * Skill handler 
	 */
	@Autowired
	SkillHandler skillHandler;
	
	/*
	 * Service in charge of saving/loading data
	 */
	@Autowired
	DataHandler dataSaver;
	
	@Scheduled(fixedRateString="${dataSaver.timeDelay}")
    public void work() {
		
		// We do not launch asynchronous tasks if the class has not been fully filled by the Spring container.
		if ((projectHandler.getLocker() == null) || (staffHandler.getLocker() == null) || (skillHandler.getLocker() == null)) {
			if (log.isDebugEnabled()) {
				log.debug(
					"In development mode, to avoid a useless 'NullPointerException', " +
					"because either ProjectHandlerImpl, or StaffHandlerImpl, or SkillHandlerImpl " +
					"might not been have been completly created");
			}
			return;
		}

		// We do not save periodicaly the data if this data is not 
		if (!dataSaver.isLocal()) {
			return;
		}

		synchronized (projectHandler.getLocker()) {
			try {
				if (projectHandler.isDataUpdated() || projectAuditHandler.isDataUpdated() ) {
					dataSaver.saveProjects(projectHandler.getProjects());
					projectHandler.dataAreSaved();
				}
			} catch (final ApplicationException e) {
				throw new ApplicationRuntimeException(e);
			}
		}
		
		synchronized (staffHandler.getLocker()) {
			try {
				if (staffHandler.isDataUpdated()) {
					dataSaver.saveStaff(staffHandler.getStaff());
					staffHandler.dataAreSaved();
				}
			} catch (final ApplicationException e) {
				throw new ApplicationRuntimeException(e);
			}
		}
		
		synchronized (skillHandler.getLocker()) {
			try {
				if (skillHandler.isDataUpdated()) {
					dataSaver.saveSkills(skillHandler.getSkills());
					skillHandler.dataAreSaved();
				}
			} catch (final ApplicationException e) {
				throw new ApplicationRuntimeException(e);
			}
		}

    }
}
