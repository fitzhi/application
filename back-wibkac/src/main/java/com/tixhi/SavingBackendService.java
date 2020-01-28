/**
 * 
 */
package com.tixhi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.tixhi.bean.DataHandler;
import com.tixhi.bean.ProjectHandler;
import com.tixhi.bean.SkillHandler;
import com.tixhi.bean.StaffHandler;
import com.tixhi.exception.SkillerException;

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
		
		synchronized (projectHandler.getLocker()) {
			try {
				if (projectHandler.isDataUpdated()) {
					dataSaver.saveProjects(projectHandler.getProjects());
					projectHandler.dataAreSaved();
				}
			} catch (final SkillerException e) {
				throw new SkillerRuntimeException(e);
			}
		}
		
		synchronized (staffHandler.getLocker()) {
			try {
				if (staffHandler.isDataUpdated()) {
					dataSaver.saveStaff(staffHandler.getStaff());
					staffHandler.dataAreSaved();
				}
			} catch (final SkillerException e) {
				throw new SkillerRuntimeException(e);
			}
		}
		
		synchronized (skillHandler.getLocker()) {
			try {
				if (skillHandler.isDataUpdated()) {
					dataSaver.saveSkills(skillHandler.getSkills());
					skillHandler.dataAreSaved();
				}
			} catch (final SkillerException e) {
				throw new SkillerRuntimeException(e);
			}
		}

    }
}
