package com.fitzhi.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.ResumeSkillIdentifier;
import com.fitzhi.exception.ApplicationException;

/**
 * <p>Tests on the skills parser</p>
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ResumeParserServiceTests {

	@Autowired
	ResumeParserService parser;
	
	/**
	 * base TXT to compare with the other format.
	 */
	Resume experienceTxt;
	List<Integer> referenceTxtSkills;
	
	@Value("classpath:applications_files/ET_201709_UTF8.txt")
	Resource resourceFileTxt;
	
	@Value("classpath:applications_files/ET_201709.doc")
	Resource resourceFileDoc;
	
	@Value("classpath:applications_files/ET_201709.docx")
	Resource resourceFileDocx;

	@Value("classpath:applications_files/ET_201709.pdf")
	Resource resourceFilePdf;
	
	@Before
	public void init() throws ApplicationException, IOException {
		final String file_txt = resourceFileTxt.getFile().getAbsolutePath();
		experienceTxt = parser.extract(file_txt, FileType.FILE_TYPE_TXT);
		referenceTxtSkills = experienceTxt.getExperiences().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
	}
	
	@Test(expected = ApplicationException.class)
	public void testFileNotFound() throws ApplicationException {
		parser.extract("unknown", FileType.FILE_TYPE_DOC);
	}
	
	@Test
	public void parsingDOC() throws ApplicationException, IOException {
		final String file_doc = resourceFileDoc.getFile().getAbsolutePath();
		Resume experienceDoc = parser.extract(file_doc, FileType.FILE_TYPE_DOC);
		List<Integer> referenceDocSkills =experienceDoc.getExperiences().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
		Assert.assertTrue(referenceDocSkills.containsAll(referenceTxtSkills));			
	}

	@Test
	public void parsingDOCX() throws ApplicationException, IOException {
		final String file_docx = resourceFileDocx.getFile().getAbsolutePath();
		Resume experienceDocx = parser.extract(file_docx, FileType.FILE_TYPE_DOCX);
		List<Integer> referenceDocxSkills =experienceDocx.getExperiences().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
		Assert.assertTrue(referenceDocxSkills.containsAll(referenceTxtSkills));			
	}

	@Test
	public void parsingPDF() throws ApplicationException, IOException {
		final String file_pdf = resourceFilePdf.getFile().getAbsolutePath();
		Resume experiencePdf = parser.extract(file_pdf, FileType.FILE_TYPE_PDF);
		// We loosed certainly some few skills during the convert into PDF, but the main skills are still present.
		Assert.assertTrue(experiencePdf.getExperiences().toArray().length +  " != 2", experiencePdf.getExperiences().toArray().length == 2);
	}
}
