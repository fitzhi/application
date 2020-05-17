package com.fitzhi.bean.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.StaffHandler;
import com.fitzhi.data.internal.Mission;
import com.fitzhi.data.internal.Staff;
import com.fitzhi.data.internal.StaffActivitySkill;

/**
 * Class in charge of testing the method {@link StaffHandler#updateActiveState}.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "staffHandler.inactivity.delay=10" })
public class StaffHandlerProcessActiveStatusTest {

	@Autowired
	private StaffHandler staffHandler;

	Staff staff;

	final int ID_STAFF = 1789;

	final int ID_SKILL_JAVA = 1;
	final int ID_SKILL_TS = 2;
	
	final int NUMBER_OF_DAYS = 5;

	@Before
	public void before() {
		staff = new Staff(1789, "firstName", "lastName", "nickName", "login", "email", "level");
		Mission m = new Mission(ID_STAFF, 1789, "Revolution");
		staff.addMission(m);
		m.getStaffActivitySkill()
			.put(ID_SKILL_JAVA, new StaffActivitySkill(ID_SKILL_JAVA, ID_STAFF, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 24), 1000));
		
		m = new Mission(ID_STAFF, 1805, "Austerlitz");		
		staff.addMission(m);
		m.getStaffActivitySkill()
			.put(ID_SKILL_TS, new StaffActivitySkill(ID_SKILL_TS, ID_STAFF, LocalDate.of(2019, 1, 1), LocalDate.of(2019, 12, 24), 2000));
		m.getStaffActivitySkill()
			.put(ID_SKILL_JAVA, new StaffActivitySkill(ID_SKILL_JAVA, ID_STAFF, LocalDate.of(2019, 1, 1), LocalDate.now().minusDays(NUMBER_OF_DAYS), 2000));
		
	}
	
	@Test
	public void testActivateStaff() {
		staffHandler.processActiveStatus(staff);
		Assert.assertTrue("Staff is active", staff.isActive());
	
	}

	@Test
	public void testReactivateStaff() {
		staff.setActive(false);
		staff.setDateInactive(LocalDate.of(2020, 01, 01));
		staffHandler.processActiveStatus(staff);
		Assert.assertTrue("Staff is active", staff.isActive());
		Assert.assertNull("Starting date of inactivity", staff.getDateInactive());
	
	}

	@Test
	public void testDeactivateStaff() {
		
		// We force the last date of commit to be older that staffHandler.inactivity.delay
		Optional<Mission> oMission = staff.getMissions().stream().filter(mission -> mission.getIdProject() == 1805).findFirst();
		if (oMission.isPresent()) {
			oMission.get().getStaffActivitySkill().get(ID_SKILL_JAVA).setLastCommit(LocalDate.now().minusDays(NUMBER_OF_DAYS+10));
		}
		
		staff.setActive(true);
		staffHandler.processActiveStatus(staff);
		Assert.assertFalse("Staff is active", staff.isActive());
		Assert.assertEquals("Starting date of inactivity", LocalDate.now().minusDays(NUMBER_OF_DAYS+10), staff.getDateInactive());
	}

	@Test
	public void testDeactivationIsDisableIfForceeActiveStateIsSetToTrueStaff() {
		
		// We force the last date of commit to be older that staffHandler.inactivity.delay
		Optional<Mission> oMission = staff.getMissions().stream().filter(mission -> mission.getIdProject() == 1805).findFirst();
		if (oMission.isPresent()) {
			oMission.get().getStaffActivitySkill().get(ID_SKILL_JAVA).setLastCommit(LocalDate.now().minusDays(NUMBER_OF_DAYS+10));
		}
		
		staff.setActive(true);
		staff.setForceActiveState(true);
		staffHandler.processActiveStatus(staff);

		Assert.assertTrue("Staff is active", staff.isActive());
		Assert.assertNull("Starting date of inactivity", staff.getDateInactive());
	}
	
}
