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
import fr.skiller.controler.ReferentialController;
import fr.skiller.data.internal.DeclaredExperience;
import fr.skiller.data.internal.Skill;
import fr.skiller.service.ResumeParserService;
import fr.skiller.service.StorageService;
import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class ApplicationFileSkillsScannerService implements ResumeParserService {

	@Autowired
	SkillHandler skillHandler;

	Logger logger = LoggerFactory.getLogger(ReferentialController.class.getCanonicalName());

	final String car_allowed = "abcdefghijklmnopqrstuvwxyz-+#";

	@Override
	public DeclaredExperience extract(final String fileName, final int fileType) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extracting skills from " + fileName);
		}
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
	}

	private DeclaredExperience parseTheApplicationFile(final String token[]) {
		DeclaredExperience experience = new DeclaredExperience();
		List<Skill> listSkills = new ArrayList<Skill>();
		Collection<Skill> skillsDeclared = skillHandler.getSkills().values();
		Map<String, Skill> skills = new HashMap<String, Skill>();
		skillsDeclared.forEach((sk -> skills.put(cleanup(sk.title), sk)));

		for (String s : token) {
			String cleanLine = cleanup(s);
			if (skills.containsKey(cleanLine)) {
				listSkills.add(skills.get(cleanLine));
			}
		}

		Map<String, Long> mapSkills = listSkills.stream()
				.collect(Collectors.groupingBy(exp -> exp.title, Collectors.counting()));

		mapSkills.keySet().stream().sorted().forEach(key -> experience.put(key, mapSkills.get(key)));

		return experience;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private DeclaredExperience extractSkillsfromCV_inFormatTXT(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extactSkillsfromCV_inFormatTXT for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		final String content = this.readTheCvInFormatTXT(filename);
		String[] token = wtk.tokenize(content);
		return parseTheApplicationFile(token);
	}

	private DeclaredExperience extractSkillsfromCV_inFormatDOCX(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOCX for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOCX(filename));
		return parseTheApplicationFile(token);
	}

	private DeclaredExperience extractSkillsfromCV_inFormatDOC(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOC for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOC(filename));
		return parseTheApplicationFile(token);
	}

	private DeclaredExperience extractSkillsfromCV_inFormatPDF(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatPDF for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatPDF(filename));
		return parseTheApplicationFile(token);
	}

	private String readTheCvInFormatTXT(final String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		br.lines().forEach(line -> sb.append(line));
		br.close();
		return sb.toString();
	}

	private String readTheCvInFormatDOC(final String fileName) throws IOException {
		FileInputStream in = new FileInputStream(fileName);
		HWPFDocument doc = new HWPFDocument(in);
		String content = doc.getDocumentText();
		doc.close();
		return content;
	}

	private String readTheCvInFormatDOCX(final String fileName) throws IOException {
		XWPFDocument docx = new XWPFDocument(new FileInputStream(fileName));
		XWPFWordExtractor we = new XWPFWordExtractor(docx);
		String content = we.getText();
		we.close();
		return content;
	}

	private String readTheCvInFormatPDF(final String filename) throws IOException {
		PdfReader reader = new PdfReader(filename);
		final StringBuilder sb = new StringBuilder();
		for (int pageNumber = 1; pageNumber < reader.getNumberOfPages(); pageNumber++) {
			sb.append(PdfTextExtractor.getTextFromPage(reader, pageNumber));
		}
		reader.close();
		return sb.toString();
	}

	private String cleanup(final String s) {
		StringBuilder sb = new StringBuilder();
		s.toLowerCase().chars().filter(car -> (car_allowed.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

}
