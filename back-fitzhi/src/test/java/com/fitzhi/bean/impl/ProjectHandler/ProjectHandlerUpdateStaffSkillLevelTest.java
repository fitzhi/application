package com.fitzhi.bean.impl.ProjectHandler;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.fitzhi.bean.ProjectHandler;
import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffExperienceTemplate;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of method {@link ProjectHandler#updateProjectStaffSkillLevel(java.util.Map)}
 * 
 * @author Frederic VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode=ClassMode.BEFORE_CLASS)
public class ProjectHandlerUpdateStaffSkillLevelTest {

	@Autowired
	ProjectHandler projectHandler;

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;

	@MockBean
	StaffHandler staffHandler;

	@Test
	public void valueTooSmall() throws ApplicationException {
		Map<StaffExperienceTemplate, Integer> staffExpData = new HashMap<>();
		staffExpData.put(StaffExperienceTemplate.of(2, 1), 30);
		projectHandler.updateProjectStaffSkillLevel(staffExpData);
		verify(staffHandler, never()).updateSkillSystemLevel(1, 1, 1);
	}

	@Test
	public void firstLevel() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff(1, "frvidal", ""));
		doNothing().when(staffHandler).updateSkillSystemLevel(1, 1, 1);
		Map<StaffExperienceTemplate, Integer> staffExpData = new HashMap<>();
		staffExpData.put(StaffExperienceTemplate.of(2, 1), 3000);
		projectHandler.updateProjectStaffSkillLevel(staffExpData);
		verify(staffHandler, times(1)).updateSkillSystemLevel(1, 1, 1);
		verify(staffHandler, times(1)).lookup(1);
	}

	@Test
	public void secondLevel() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff(1, "frvidal", ""));
		doNothing().when(staffHandler).updateSkillSystemLevel(1, 1, 3);
		Map<StaffExperienceTemplate, Integer> staffExpData = new HashMap<>();
		staffExpData.put(StaffExperienceTemplate.of(2, 1), 13000);
		projectHandler.updateProjectStaffSkillLevel(staffExpData);
		verify(staffHandler, times(1)).updateSkillSystemLevel(1, 1, 3);
		verify(staffHandler, times(1)).lookup(1);
	}

	@Test (expected = ApplicationException.class)
	public void ko() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(new Staff(1, "frvidal", ""));
		doThrow(new ApplicationException()).when(staffHandler).updateSkillSystemLevel(1, 1, 3);
		Map<StaffExperienceTemplate, Integer> staffExpData = new HashMap<>();
		staffExpData.put(StaffExperienceTemplate.of(2, 1), 13000);
		projectHandler.updateProjectStaffSkillLevel(staffExpData);
		verify(staffHandler, times(1)).lookup(1);
		verify(staffHandler, times(1)).updateSkillSystemLevel(1, 1, 3);
	}

	@Test
	public void skipUnknowStaffMember() throws ApplicationException {
		when(staffHandler.lookup(1)).thenReturn(null);
		Map<StaffExperienceTemplate, Integer> staffExpData = new HashMap<>();
		staffExpData.put(StaffExperienceTemplate.of(2, 1789), 7000);
		projectHandler.updateProjectStaffSkillLevel(staffExpData);
		verify(staffHandler, never()).updateSkillSystemLevel(anyInt(), anyInt(), anyInt());
		verify(staffHandler, times(1)).lookup(1789);
	}

}
