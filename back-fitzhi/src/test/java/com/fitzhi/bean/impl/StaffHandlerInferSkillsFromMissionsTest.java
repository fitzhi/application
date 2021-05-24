/**
 * 
 */
package com.fitzhi.bean.impl;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Experience;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffActivitySkill;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>
 * Testing the method {@link StaffHandler#inferSkillsFromMissions(int)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StaffHandlerInferSkillsFromMissionsTest {

	final int ID_1 = 1001;
	final int ID_2 = 1002;

	final int ID_SKILL_JAVA = 1;
	final int ID_SKILL_TS = 2;
	
	@Autowired
	private StaffHandler staffHandler;
	
	@Before
	public void before() throws ApplicationException {
		Staff s1 = new Staff(ID_1, "firstName 1", "lastName 1", "nickName 1", "login 1", "email 1", "ICD 1");
		s1.getExperiences().add(new Experience (3,  1));
		Mission m = new Mission(ID_1, 1789, "Revolution");
		s1.addMission(m);
		m.getStaffActivitySkill()
			.put(ID_SKILL_JAVA, new StaffActivitySkill(ID_SKILL_JAVA, ID_1, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 24), 1000));
		m = new Mission(ID_1, 1805, "Austerlitz");		
		s1.addMission(m);
		m.getStaffActivitySkill()
			.put(ID_SKILL_TS, new StaffActivitySkill(ID_SKILL_TS, ID_1, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 24), 2000));
		staffHandler.getStaff().put(ID_1, s1);
		
		Staff s2 = new Staff(ID_2, "firstName 2", "lastName 2", "nickName 2", "login 2", "email 2", "ICD 2");
		s2.getExperiences().add(new Experience (ID_SKILL_JAVA, 5));
		m = new Mission(ID_2, 18789, "Revolution");
		s2.addMission(m);
		m.getStaffActivitySkill()
			.put(ID_SKILL_JAVA, new StaffActivitySkill(ID_SKILL_JAVA, ID_2, LocalDate.of(2019, 7, 14), LocalDate.of(2019, 7, 15), 3000));
		m.getStaffActivitySkill()
			.put(ID_SKILL_TS, new StaffActivitySkill(ID_SKILL_TS, ID_2, LocalDate.of(2019, 7, 14), LocalDate.of(2019, 8, 15), 4000));
		staffHandler.getStaff().put(ID_2, s2);
	}
	
	@Test
	public void testICD_1() throws ApplicationException {
		staffHandler.inferSkillsFromMissions(ID_1);
		Staff staff = staffHandler.lookup(ID_1);
		Assert.assertEquals(3, staff.getExperiences().size());
		Assert.assertNotNull(staff.getExperience(ID_SKILL_JAVA));
		Assert.assertEquals(1, staff.getExperience(ID_SKILL_JAVA).getLevel());
		Assert.assertNotNull(staff.getExperience(ID_SKILL_TS));
		Assert.assertEquals(1, staff.getExperience(ID_SKILL_TS).getLevel());
	}

	@Test
	public void testICD_2() throws ApplicationException {
		staffHandler.inferSkillsFromMissions(ID_2);
		Staff staff = staffHandler.lookup(ID_2);
		Assert.assertEquals(2, staff.getExperiences().size());
		Assert.assertNotNull(staff.getExperience(ID_SKILL_JAVA));
		Assert.assertEquals(5, staff.getExperience(ID_SKILL_JAVA).getLevel());
		Assert.assertNotNull(staff.getExperience(ID_SKILL_TS));
		Assert.assertEquals(1, staff.getExperience(ID_SKILL_TS).getLevel());
	}
	
	@After
	public void after() {
		staffHandler.getStaff().remove(ID_1);
		staffHandler.getStaff().remove(ID_2);
	}
}