package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.ExperienceAbacus;
import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.Project;
import com.fitzhi.data.internal.Staff;
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

/**
 * This class tests the method {@link ProjectHandler#updateStaffSkillSystemLevel(int, int, int, java.util.Map, java.util.List))}
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ProjectHandlerUpdateStaffSkillSystemLevelTest {

	Project project;
	
	@Autowired
	ProjectHandler projectHandler;
	
	@MockBean
	StaffHandler staffHandler;
	
	@Before
	public void before() throws ApplicationException {
		project = new Project(1789, "the French revolution");
	}
	
	@Test
	public void doNotAcceptNegativeValue() throws ApplicationException {
		projectHandler.updateStaffSkillSystemLevel(1, 1, -1, null, null);
		verify(staffHandler, never()).updateSkillSystemLevel(any(int.class), any(int.class), any(int.class));
	}

	@Test (expected = ApplicationRuntimeException.class)
	public void staffNotFound() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff());
		projectHandler.updateStaffSkillSystemLevel(1, 1, 1, new HashMap<>(), null);
	}

	@Test
	public void abacusEmpty() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff());

		Map<Integer, ExperienceDetectionTemplate> templates = new HashMap<>();
		ExperienceDetectionTemplate edt = new ExperienceDetectionTemplate();
		edt.setIdSkill(2);
		templates.put(1, edt);
		List<ExperienceAbacus> abacus = new ArrayList<>();
		projectHandler.updateStaffSkillSystemLevel(1, 1, 1, templates, abacus);
		verify(staffHandler, never()).updateSkillSystemLevel(any(int.class), any(int.class), any(int.class));
	}

	@Test
	public void updateSkillSystemLevel() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff());

		Map<Integer, ExperienceDetectionTemplate> templates = new HashMap<>();
		ExperienceDetectionTemplate edt = new ExperienceDetectionTemplate();
		edt.setIdSkill(2);
		templates.put(1, edt);
		
		List<ExperienceAbacus> abacus = new ArrayList<>();
		ExperienceAbacus ea = new ExperienceAbacus(1, 1);
		abacus.add(ea);

		projectHandler.updateStaffSkillSystemLevel(1, 1, 1, templates, abacus);
		verify(staffHandler, times(1)).updateSkillSystemLevel(any(int.class), any(int.class), any(int.class));
	}

}
