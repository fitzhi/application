package com.fitzhi.source.crawler.impl;

import java.util.Map;

import com.fitzhi.data.internal.ExperienceDetectionTemplate;
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
	public void loadNominal() throws ApplicationException {
		Map<Integer, ExperienceDetectionTemplate> result = ecosystemAnalyzer.loadExperienceDetectionTemplates();
		Assert.assertEquals(1, result.size());
		Assert.assertTrue(result.containsKey(0));
		Assert.assertEquals(0, result.get(0).getIdEDT());
		Assert.assertEquals(3, result.get(0).getIdSkill());
		Assert.assertEquals(TypeCode.SpringAnnotation, result.get(0).getTypeCode());
		Assert.assertEquals("/@Service$", result.get(0).getCodePattern());
		Assert.assertEquals(".java$", result.get(0).getFilePattern());
	}
}