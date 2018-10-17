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
import fr.skiller.data.internal.Resume;
import fr.skiller.data.internal.Skill;
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
	public Resume extract(final String fileName, final int fileType) throws IOException {
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

	private Resume parseTheApplicationFile(final String token[]) {
		Resume experience = new Resume();
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
	private Resume extractSkillsfromCV_inFormatTXT(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extactSkillsfromCV_inFormatTXT for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		final String content = this.readTheCvInFormatTXT(filename);
		String[] token = wtk.tokenize(content);
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatDOCX(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOCX for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOCX(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatDOC(final String filename) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("extractSkillsfromCV_inFormatDOC for file " + filename);
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOC(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCV_inFormatPDF(final String filename) throws IOException {
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
