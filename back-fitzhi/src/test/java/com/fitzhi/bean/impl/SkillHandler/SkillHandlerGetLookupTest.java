package com.fitzhi.bean.impl.SkillHandler;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.exception.NotFoundException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the methods {@link SkillHandler#getSkill(int)} and {@link SkillHandler#lookup(String)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkillHandlerGetLookupTest {
 
	@Autowired
	SkillHandler skillHandler;

	@Test
	public void getSkillOk() throws ApplicationException {
		Skill skill = skillHandler.getSkill(1);
		Assert.assertNotNull(skill);
		Assert.assertEquals(1, skill.getId());
		Assert.assertEquals("Java", skill.getTitle());
	}

	/**
	 * getSkill() throws a NotFoundException if the skill ID does not exist
	 */
	@Test(expected = NotFoundException.class)
	public void getSkillKO() throws ApplicationException {
		skillHandler.getSkill(1789);
	}


	@Test
	public void lookupOk() throws ApplicationException {
		Skill skill = skillHandler.lookup(1);
		Assert.assertNotNull(skill);
		Assert.assertEquals(1, skill.getId());
		Assert.assertEquals("Java", skill.getTitle());
	}

	/**
	 * lookup() returns {@code NULL} if the skill ID does not exist
	 */
	@Test
	public void lookupKO() throws ApplicationException {
		Skill skill = skillHandler.lookup(1789);
		Assert.assertNull(skill);
	}	
}
