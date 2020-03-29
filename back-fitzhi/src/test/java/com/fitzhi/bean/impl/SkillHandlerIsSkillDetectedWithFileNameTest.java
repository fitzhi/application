/**
 * 
 */
package com.fitzhi.bean.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.SkillDetectionTemplate;
import com.fitzhi.data.internal.SkillDetectorType;

/**
 * <p>
 * Test of the method {@link SkillHandler#isSkillDetectedWithFilename(Skill, String)}
 * </p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkillHandlerIsSkillDetectedWithFileNameTest {

	@Autowired
	private SkillHandler skillHandler;
	
	private Skill austerlitz, waterloo, notapplicable;
	
	@Before
	public void before() {
		austerlitz = new Skill(1789, "Austerlitz", new SkillDetectionTemplate(SkillDetectorType.FILENAME_DETECTOR_TYPE, ".yes$"));
		waterloo = new Skill(1789, "Waterloo", new SkillDetectionTemplate(SkillDetectorType.FILENAME_DETECTOR_TYPE, ".no$"));
		notapplicable = new Skill(1789, "Not applicable", new SkillDetectionTemplate(SkillDetectorType.POM_XML_DETECTOR_TYPE, "test"));
	}
	
	@Test
	public void countAllStaffGroupBySkillLevelAusterlitz() {
		Assert.assertTrue("'test.yes' is OK", this.skillHandler.isSkillDetectedWithFilename(austerlitz, "test.yes"));
		Assert.assertFalse("'test.yesyop' is NOT OK", this.skillHandler.isSkillDetectedWithFilename(austerlitz, "test.yesyop"));
	}
	
	@Test
	public void countAllStaffGroupBySkillLevelWaterloo() {
		Assert.assertFalse("'test.yes' is NOT OK", this.skillHandler.isSkillDetectedWithFilename(waterloo, "test.yes"));
	}
	
	@Test
	public void countAllStaffGroupBySkillLevelNotApplicable() {
		Assert.assertFalse("a.yes is NOT OK for ", this.skillHandler.isSkillDetectedWithFilename(notapplicable, "a.yes"));
	}
	
}