package fr.skiller.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
import fr.skiller.data.internal.DeclaredExperience;

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
	DeclaredExperience experience_txt;
	
	@Before
	public void init() throws IOException {
		final String file_txt = getClass().getResource("/applications_files/ET_201709_UTF8.txt").getFile();
		experience_txt = parser.extract(file_txt, StorageService.FILE_TYPE_TXT);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testFileNotFound() throws IOException {
		parser.extract("unknown", StorageService.FILE_TYPE_DOC);
	}
	
	@Test
	public void parsingDOC() throws IOException {
		final String file_doc = getClass().getResource("/applications_files/ET_201709.doc").getFile();
		DeclaredExperience experience_doc = parser.extract(file_doc, StorageService.FILE_TYPE_DOC);
		Assert.assertArrayEquals(experience_txt.data().keySet().toArray(), experience_doc.data().keySet().toArray());
	}

	@Test
	public void parsingDOCX() throws IOException {
		final String file_docx = getClass().getResource("/applications_files/ET_201709.docx").getFile();
		DeclaredExperience experience_docx = parser.extract(file_docx, StorageService.FILE_TYPE_DOCX);
		Assert.assertArrayEquals(experience_txt.data().keySet().toArray(), experience_docx.data().keySet().toArray());
	}

	@Test
	public void parsingPDF() throws IOException {
		final String file_pdf = getClass().getResource("/applications_files/ET_201709.pdf").getFile();
		DeclaredExperience experience_pdf = parser.extract(file_pdf, StorageService.FILE_TYPE_PDF);
		// We loosed certainly some few skills during the convert into PDF, but the main skills are still present.
		Assert.assertTrue(experience_pdf.data().keySet().toArray().length == 30);
	}
}
