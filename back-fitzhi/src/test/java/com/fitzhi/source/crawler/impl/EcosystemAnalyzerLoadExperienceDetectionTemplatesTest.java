package com.fitzhi.source.crawler.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fitzhi.data.internal.ExperienceDetectionTemplate;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.data.internal.TypeCode;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.source.crawler.EcosystemAnalyzer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p>
 * Test of the method {@link EcosystemAnalyzer#loadExperienceDetectionTemplates()}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EcosystemAnalyzerLoadExperienceDetectionTemplatesTest {

	@Autowired
	EcosystemAnalyzer ecosystemAnalyzer;
	
	@Test
	public void loadOk() throws ApplicationException {
		Map<Integer, ExperienceDetectionTemplate> result = ecosystemAnalyzer.loadExperienceDetectionTemplates();
		Assert.assertEquals(4, result.size());
		Assert.assertTrue(result.containsKey(0));
		Assert.assertEquals(0, result.get(0).getIdEDT());
		Assert.assertEquals(3, result.get(0).getIdSkill());
		Assert.assertEquals(TypeCode.Annotation, result.get(0).getTypeCode());
		Assert.assertEquals("^Service$|^Component$", result.get(0).getCodePattern());
		Assert.assertEquals("^org.springframework.stereotype.Service$|^org.springframework.stereotype.Component$", result.get(0).getImportPattern());
		Assert.assertEquals(".java$", result.get(0).getFilePattern());
	}

	@Test
	public void TypeCodeFilteredLoad() throws ApplicationException {
		Map<Integer, ExperienceDetectionTemplate> result = ecosystemAnalyzer.loadExperienceDetectionTemplates(TypeCode.NumberOfLines, null);
		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.containsKey(2));
		Assert.assertTrue(result.containsKey(3));
	}

	@Test
	public void SkillAndTypeCodeFilteredLoad() throws ApplicationException {
		Skill[] skills = { new Skill(1, "one") };
		List<Skill> list = Arrays.asList(skills);
		Map<Integer, ExperienceDetectionTemplate> result = ecosystemAnalyzer.loadExperienceDetectionTemplates(TypeCode.NumberOfLines, list);
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.containsKey(2));
	}

}