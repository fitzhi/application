package fr.skiller.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.springframework.util.ResourceUtils;

import fr.skiller.Global;
import fr.skiller.data.internal.Resume;
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
	Resume experience_txt;
	List<Integer> reference_txt_skills;
	
	@Before
	public void init() throws SkillerException {
		final String file_txt = getClass().getResource("/applications_files/ET_201709_UTF8.txt").getFile();
		experience_txt = parser.extract(file_txt, StorageService.FILE_TYPE_TXT);
		reference_txt_skills = experience_txt.data().stream()
				.map(item -> item.idSkill).collect(Collectors.toList());
	}
	
	@Test(expected = SkillerException.class)
	public void testFileNotFound() throws SkillerException {
		parser.extract("unknown", StorageService.FILE_TYPE_DOC);
	}
	
	@Test
	public void parsingDOC() throws SkillerException {
		final String file_doc = getClass().getResource("/applications_files/ET_201709.doc").getFile();
		Resume experience_doc = parser.extract(file_doc, StorageService.FILE_TYPE_DOC);
		List<Integer> reference_doc_skills =experience_doc.data().stream()
				.map(item -> item.idSkill).collect(Collectors.toList());
		Assert.assertTrue(reference_doc_skills.containsAll(reference_txt_skills));			
	}

	@Test
	public void parsingDOCX() throws SkillerException {
		final String file_docx = getClass().getResource("/applications_files/ET_201709.docx").getFile();
		Resume experience_docx = parser.extract(file_docx, StorageService.FILE_TYPE_DOCX);
		List<Integer> reference_docx_skills =experience_docx.data().stream()
				.map(item -> item.idSkill).collect(Collectors.toList());
		Assert.assertTrue(reference_docx_skills.containsAll(reference_txt_skills));			
	}

	@Test
	public void parsingPDF() throws SkillerException {
		final String file_pdf = getClass().getResource("/applications_files/ET_201709.pdf").getFile();
		Resume experience_pdf = parser.extract(file_pdf, StorageService.FILE_TYPE_PDF);
		// We loosed certainly some few skills during the convert into PDF, but the main skills are still present.
		Assert.assertTrue(experience_pdf.data().toArray().length == 30);
	}
}
