package fr.skiller.service;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.ResumeSkillIdentifier;
import fr.skiller.exception.SkillerException;

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
	
	@Before
	public void init() throws SkillerException {
		final String file_txt = getClass().getResource("/applications_files/ET_201709_UTF8.txt").getFile();
		experienceTxt = parser.extract(file_txt, FileType.FILE_TYPE_TXT);
		referenceTxtSkills = experienceTxt.data().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
	}
	
	@Test(expected = SkillerException.class)
	public void testFileNotFound() throws SkillerException {
		parser.extract("unknown", FileType.FILE_TYPE_DOC);
	}
	
	@Test
	public void parsingDOC() throws SkillerException {
		final String file_doc = getClass().getResource("/applications_files/ET_201709.doc").getFile();
		Resume experienceDoc = parser.extract(file_doc, FileType.FILE_TYPE_DOC);
		List<Integer> referenceDocSkills =experienceDoc.data().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
		Assert.assertTrue(referenceDocSkills.containsAll(referenceTxtSkills));			
	}

	@Test
	public void parsingDOCX() throws SkillerException {
		final String file_docx = getClass().getResource("/applications_files/ET_201709.docx").getFile();
		Resume experienceDocx = parser.extract(file_docx, FileType.FILE_TYPE_DOCX);
		List<Integer> referenceDocxSkills =experienceDocx.data().stream()
				.map(ResumeSkillIdentifier::getIdSkill).collect(Collectors.toList());
		Assert.assertTrue(referenceDocxSkills.containsAll(referenceTxtSkills));			
	}

	@Test
	public void parsingPDF() throws SkillerException {
		final String file_pdf = getClass().getResource("/applications_files/ET_201709.pdf").getFile();
		Resume experiencePdf = parser.extract(file_pdf, FileType.FILE_TYPE_PDF);
		// We loosed certainly some few skills during the convert into PDF, but the main skills are still present.
		Assert.assertTrue(experiencePdf.data().toArray().length == 30);
	}
}
