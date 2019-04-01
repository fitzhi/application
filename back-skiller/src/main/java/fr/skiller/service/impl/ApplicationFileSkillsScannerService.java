package fr.skiller.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import fr.skiller.bean.SkillHandler;
import fr.skiller.controller.ReferentialController;
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.Skill;
import fr.skiller.exception.SkillerException;
import fr.skiller.service.ResumeParserService;
import fr.skiller.service.StorageService;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class ApplicationFileSkillsScannerService implements ResumeParserService {

	@Autowired
	SkillHandler skillHandler;

	@Autowired
	StorageService storageService;

	Logger logger = LoggerFactory.getLogger(ApplicationFileSkillsScannerService.class.getCanonicalName());

	final String car_allowed = "abcdefghijklmnopqrstuvwxyz-+#";

	@Override
	public Resume extract(final String fileName, final int fileType) throws SkillerException {
		if (logger.isDebugEnabled()) {
			logger.debug("extracting skills from " + fileName);
		}
		try {
			switch (fileType) {
			case StorageService.FILE_TYPE_TXT:
				return extractSkillsfromCV_inFormatTXT(fileName);
			case StorageService.FILE_TYPE_DOC:
				return extractSkillsfromCV_inFormatDOC(fileName);
			case StorageService.FILE_TYPE_DOCX:
				return extractSkillsfromCV_inFormatDOCX(fileName);
			case StorageService.FILE_TYPE_PDF:
				return extractSkillsfromCV_inFormatPDF(fileName);
			default:
				throw new RuntimeException("Should not pass here for type " + fileType);
			}
		} catch (final IOException ioe) {
			logger.error("Error for fileName " + fileName);;
			logger.error(ioe.getLocalizedMessage());
			throw new SkillerException(-1, 
					"IO error occurs when retrieving the skills from the resume."
					+ "Original error : " + ioe.getLocalizedMessage());
		}
	}

	private Resume parseTheApplicationFile(final String token[]) throws SkillerException {
		Resume resume = new Resume();
		
		/**
		 * We retrieved the skills list registered and we create a map based on the "cleaned-up" title  
		 * in order to process the comparison with the skills extracted from the resume 
		 */
		Collection<Skill> skillsDeclared = skillHandler.getSkills().values();
		Map<String, Skill> skills = new HashMap<>();
		skillsDeclared.forEach((sk -> skills.put(cleanup(sk.getTitle()), sk)));

		/**
		 * We filter the words from the content of the resume, those who match with the current skills collection
		 */
		List<Skill> listSkills = new ArrayList<>();
		for (String s : token) {
			String cleanLine = cleanup(s);
			if (skills.containsKey(cleanLine)) {
				listSkills.add(skills.get(cleanLine));
			}
		}
		if (listSkills.isEmpty()) {
			throw new SkillerException(-1, 
					"No skill has been detected inside the resume. Did-you upload a resume?");
		}
		/**
		 * We aggregate the skills, based on the count number of presence in the resume.
		 * (That might be an indication of their importance) 
		 */
		Map<Integer, Long> mapSkills = listSkills.stream()
				.collect(Collectors.groupingBy(exp -> exp.getId(), Collectors.counting()));

		mapSkills.keySet().stream().sorted().forEach(key -> resume.put(key, mapSkills.get(key)));

		return resume;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private Resume extractSkillsfromCV_inFormatTXT(final String filename) throws IOException, SkillerException {
		if (logger.isDebugEnabled()) {
			logger.debug("extactSkillsfromCV_inFormatTXT for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		final String content = this.readTheCvInFormatTXT(filename);
		String[] token = wtk.tokenize(content);
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatDOCX(final String filename) throws IOException, SkillerException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOCX for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOCX(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatDOC(final String filename) throws IOException, SkillerException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOC for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOC(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatPDF(final String filename) throws IOException, SkillerException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatPDF for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatPDF(filename));
		return parseTheApplicationFile(token);
	}

	private String readTheCvInFormatTXT(final String fileName) throws IOException {
		return storageService.readFileTXT(fileName);
	}

	private String readTheCvInFormatDOC(final String fileName) throws IOException {
		return storageService.readFileDOC(fileName);
	}

	private String readTheCvInFormatDOCX(final String fileName) throws IOException {
		return storageService.readFileDOCX(fileName);
	}

	private String readTheCvInFormatPDF(final String fileName) throws IOException {
		return storageService.readFilePDF(fileName);
	}

	private String cleanup(final String s) {
		StringBuilder sb = new StringBuilder();
		s.toLowerCase().chars().filter(car -> (car_allowed.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

}
