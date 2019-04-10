/**
 * 
 */
package fr.skiller.bean.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.skiller.bean.StaffHandler;
import fr.skiller.data.internal.Experience;
import fr.skiller.data.internal.PeopleCountExperienceMap;
import fr.skiller.data.internal.Staff;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StaffHandlerSkillTest {

	private static final String SK_10000_5 = "10000-5";

	private static final String SK_10000_3 = "10000-3";

	private static final String SK_10000_1 = "10000-1";

	@Autowired
	private StaffHandler staffHandler;
	
	@Test
	public void countAllStaffGroupBySkillLevel() {
		
		Staff s1 = new Staff(10001, "firstName 1", "lastName 1", "nickName 1", "login 1", "email 1", "ICD 1");
		s1.getExperiences().add(new Experience (10000,  3));
		
		Staff s2 = new Staff(10002, "firstName 2", "lastName 2", "nickName 2", "login 2", "email 2", "ICD 2");
		s2.getExperiences().add(new Experience (10000, 3));

		Staff s3 = new Staff(10003, "firstName 3", "lastName 3", "nickName 3", "login 3", "email 3", "ICD 3");
		s3.getExperiences().add(new Experience (10000, 5));
		
		staffHandler.put(10001, s1);
		staffHandler.put(10002, s2);
		staffHandler.put(10003, s3);
		
		PeopleCountExperienceMap results = staffHandler.countAllStaff_GroupBy_Skill_Level(true);
		Assert.assertTrue(results.get(SK_10000_1) == null);
		Assert.assertTrue(results.get(SK_10000_3) == 2);
		Assert.assertTrue(results.get(SK_10000_5) == 1);
		
		s2.setActive(false);

		results = staffHandler.countAllStaff_GroupBy_Skill_Level(true);
		Assert.assertTrue(results.get(SK_10000_1) == null);
		Assert.assertTrue(results.get(SK_10000_3) == 1);
		Assert.assertTrue(results.get(SK_10000_5) == 1);
		
		results = staffHandler.countAllStaff_GroupBy_Skill_Level(false);
		Assert.assertTrue(results.get(SK_10000_1) == null);
		Assert.assertTrue(results.get(SK_10000_3) == 2);
		Assert.assertTrue(results.get(SK_10000_5) == 1);
		
		staffHandler.init();
	}
	
}