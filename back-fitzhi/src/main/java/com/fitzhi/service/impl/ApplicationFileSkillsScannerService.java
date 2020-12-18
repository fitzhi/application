package com.fitzhi.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fitzhi.ApplicationRuntimeException;
import com.fitzhi.bean.SkillHandler;
import com.fitzhi.data.internal.Resume;
import com.fitzhi.data.internal.Skill;
import com.fitzhi.exception.ApplicationException;
import com.fitzhi.service.FileType;
import com.fitzhi.service.ResumeParserService;
import com.fitzhi.service.StorageService;

import opennlp.tools.tokenize.WhitespaceTokenizer;

@Service
public class ApplicationFileSkillsScannerService implements ResumeParserService {

	@Autowired
	SkillHandler skillHandler;

	@Autowired
	@Qualifier("Application")
	StorageService storageService;

	Logger logger = LoggerFactory.getLogger(ApplicationFileSkillsScannerService.class.getCanonicalName());

	private static final String CAR_ALLOWED = "abcdefghijklmnopqrstuvwxyz-+#";

	@Override
	public Resume extract(final String fileName, final FileType typeOfApplication) throws ApplicationException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("extracting skills from %s", fileName));
		}
		try {
			switch (typeOfApplication) {
			case FILE_TYPE_TXT:
				return extractSkillsfromCVInFormatTXT(fileName);
			case FILE_TYPE_DOC:
				return extractSkillsfromCVInFormatDOC(fileName);
			case FILE_TYPE_DOCX:
				return extractSkillsfromCVInFormatDOCX(fileName);
			case FILE_TYPE_PDF:
				return extractSkillsfromCVInFormatPDF(fileName);
			default:
				throw new ApplicationRuntimeException(String.format("Should not pass here for type %s", typeOfApplication.toString()));
			}
		} catch (final IOException ioe) {
			if (logger.isErrorEnabled()) {
				logger.error(String.format("Error for fileName %s", fileName));
				logger.error(ioe.getLocalizedMessage());
			}
			throw new ApplicationException(-1, 
					"IO error occurs when retrieving the skills from the resume."
					+ "Original error : " + ioe.getLocalizedMessage());
		}
	}

	private Resume parseTheApplicationFile(final String[] token) throws ApplicationException {
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
			throw new ApplicationException(-1, 
					"No skill has been detected inside the resume. Did-you upload a resume?");
		}
		/**
		 * We aggregate the skills, based on the count number of presence in the resume.
		 * (That might be an indication of their importance) 
		 */
		Map<Integer, Long> mapSkills = listSkills.stream()
				.collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));

		mapSkills.keySet().stream().sorted().forEach(key -> resume.put(key, mapSkills.get(key)));

		return resume;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	private Resume extractSkillsfromCVInFormatTXT(String filename) throws IOException, ApplicationException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("extactSkillsfromCV_inFormatTXT for file %s", filename));
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		final String content = this.readTheCvInFormatTXT(filename);
		String[] token = wtk.tokenize(content);
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCVInFormatDOCX(final String filename) throws IOException, ApplicationException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("extractSkillsfromCV_inFormatDOCX for file %s", filename));
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOCX(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCVInFormatDOC(final String filename) throws IOException, ApplicationException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("extractSkillsfromCV_inFormatDOC for file %s", filename));
		}
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.readTheCvInFormatDOC(filename));
		return parseTheApplicationFile(token);
	}

	private Resume extractSkillsfromCVInFormatPDF(final String filename) throws IOException, ApplicationException {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("extractSkillsfromCV_inFormatPDF for file %s", filename));
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
		s.toLowerCase().chars().filter(car -> (CAR_ALLOWED.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

}
