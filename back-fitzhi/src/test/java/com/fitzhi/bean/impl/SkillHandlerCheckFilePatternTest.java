package com.fitzhi.bean.impl;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.exception.ApplicationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link SkillHandler#isSkillDetectedWithFilename(Skill, String)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkillHandlerCheckFilePatternTest {
 
	@Autowired
	SkillHandler skillHandler;

	@Test
	public void checkSpringCoreIsPresentInMyPomXml() throws ApplicationException {
		boolean res = skillHandler.checkFilePattern("pom.xml", "./", "pom.xml", "<artifactId>spring-core</artifactId>");
		Assert.assertTrue("Should detect the PRESENCE of spring-core in the pom.xml of fitzhi", res);
	}

	@Test
	public void checkSpringWhaoooIsAbsentInMyPomXml() throws ApplicationException {
		boolean res = skillHandler.checkFilePattern("pom.xml", "./", "pom.xml", "<artifactId>spring-whaooo</artifactId>");
		Assert.assertFalse("Should detect the ABSENCE of spring-whaooo in the pom.xml of fitzhi", res);
	}

	@Test
	public void checkUnknownFile() throws ApplicationException {
		boolean res = skillHandler.checkFilePattern("my-pom.xml", "./", "pom.xml", "<artifactId>spring-core</artifactId>");
		Assert.assertFalse("Should be FALSE if the file does not exist", res);
	}
	
}
