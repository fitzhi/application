package com.fitzhi.bean.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fitzhi.SavingBackendService;
import com.fitzhi.bean.DataHandler;
import com.fitzhi.bean.ProjectAuditHandler;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext (classMode = ClassMode.BEFORE_CLASS)
public class SavingBackendServiceTest {
	
	@Autowired
	SavingBackendService savingBackendService;

	@MockBean
	DataHandler dataSaver;
	
	@MockBean
	ProjectHandler projectHandler;
	
	@MockBean
	ProjectAuditHandler projectAuditHandler;

	@MockBean
	SkillHandler skillHandler;
	
	@MockBean
	StaffHandler staffHandler;

	@Before
	public void before() {
		when(projectHandler.getLocker()).thenReturn(new Object());
		when(staffHandler.getLocker()).thenReturn(new Object());
		when(skillHandler.getLocker()).thenReturn(new Object());
		when(dataSaver.isLocal()).thenReturn(true);
	}

	@Test
	public void saveProjects1() throws ApplicationException {
		when(projectHandler.isDataUpdated()).thenReturn(true);
		when(projectAuditHandler.isDataUpdated()).thenReturn(false);
		doNothing().when(dataSaver).saveProjects(any());
		
		savingBackendService.work();

		verify(dataSaver, times(1)).saveProjects(anyMap());
	}

	@Test
	public void saveProjects2() throws ApplicationException {
		when(projectHandler.isDataUpdated()).thenReturn(false);
		when(projectAuditHandler.isDataUpdated()).thenReturn(true);
		doNothing().when(dataSaver).saveProjects(any());
		
		savingBackendService.work();

		verify(dataSaver,times(1)).saveProjects(anyMap());
	}

	@Test
	public void doNotSaveProjects() throws ApplicationException {
		when(projectHandler.isDataUpdated()).thenReturn(false);
		when(projectAuditHandler.isDataUpdated()).thenReturn(false);
		doNothing().when(dataSaver).saveProjects(any());
		
		savingBackendService.work();

		verify(dataSaver, never()).saveProjects(anyMap());
	}

	@Test
	public void saveStaff() throws ApplicationException {
		when(staffHandler.isDataUpdated()).thenReturn(true);
		doNothing().when(dataSaver).saveStaff(any());
		
		savingBackendService.work();

		verify(dataSaver, times(1)).saveStaff(anyMap());
	}

	@Test
	public void saveSkill() throws ApplicationException {
		when(skillHandler.isDataUpdated()).thenReturn(true);
		doNothing().when(dataSaver).saveSkills(any());
		
		savingBackendService.work();

		verify(dataSaver, times(1)).saveSkills(anyMap());
	}

	@Test
	public void doNotSaveSkillsOnRemoteMode() throws ApplicationException {
		when(skillHandler.isDataUpdated()).thenReturn(true);
		doNothing().when(dataSaver).saveSkills(any());
		// We are on remote MODE, most probably inside a slave
		when(dataSaver.isLocal()).thenReturn(false);

		savingBackendService.work();

		verify(dataSaver, never()).saveSkills(anyMap());
	}
}
