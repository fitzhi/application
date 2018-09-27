/**
 * 
 */
package fr.skiller.opennlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Simple test of OPENNLP
 */
public class SimpleTest {

	Logger logger = LoggerFactory.getLogger(SimpleTest.class.getCanonicalName());
	
	private Map<String, String> getSkills() throws IOException {
		Map<String, String> skills = new HashMap<String, String>();
		final BufferedReader br = new BufferedReader(
				new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/opennlp/skills")));
		br.lines().forEach(line -> skills.put(cleanup(line), line));
		br.close();
		return skills;
	}

	private String getCV_fromTxt() throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(
				new FileReader(new File(resourcesDirectory.getAbsolutePath() + "/opennlp/in/ET_201709_UTF8.txt")));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

	private String getCV_fromDOCX() throws IOException {
		XWPFDocument docx = new XWPFDocument(
				new FileInputStream(resourcesDirectory.getAbsolutePath() + "/opennlp/in/ET_201709.docx"));
		XWPFWordExtractor we = new XWPFWordExtractor(docx);
		return we.getText();
	}
	
	final String car_accepted = "abcdefghijklmnopqrstuvwxyz-+";

	public String cleanup(final String s) {
		StringBuilder sb = new StringBuilder();
		s.toLowerCase().chars().filter(car -> (car_accepted.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

	/**
	 * @return 
	 * @throws IOException
	 */
	private List<String> getSkillsfromCVinTXT() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.getCV_fromTxt());
		Set<String> set = new HashSet<String>();
		for (String s : token) {
			String cleanLine = cleanup(s);
			System.out.println(cleanLine);
			Map<String, String> skills = getSkills();
			if (skills.containsKey(cleanLine)) {
				set.add(skills.get(cleanLine));
			}
		}
		logger.debug("getSkillsfromCVinDOCX : ");
		set.stream().forEach(skill -> logger.debug(skill));

		List<String> sortedList = new ArrayList<String>(set);
		Collections.sort(sortedList);
		return sortedList;
	}

	List<String> getSkillsfromCVinDOCX() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		String[] token = wtk.tokenize(this.getCV_fromDOCX());
		Set<String> set = new HashSet<String>();
		for (String s : token) {
			String cleanLine = cleanup(s);
			Map<String, String> skills = getSkills();
			if (skills.containsKey(cleanLine)) {
				set.add(skills.get(cleanLine));
			}
		}
		logger.debug("getSkillsfromCVinDOCX : ");
		set.stream().forEach(skill -> logger.debug(skill));
		
		List<String> sortedList = new ArrayList<String>(set);
		Collections.sort(sortedList);
		return sortedList;
	}

	@Test
	public void simpleTestComplete() throws Exception {
		List<String> setTXT = this.getSkillsfromCVinTXT();
		List<String> setDOCX = this.getSkillsfromCVinDOCX();
		Assert.assertArrayEquals(setTXT.toArray(), setDOCX.toArray());
	}


	@Test
	public void simpleSimpleTokenizer() {
		SimpleTokenizer wtk = SimpleTokenizer.INSTANCE;
		String[] token = wtk.tokenize("Hello World. I am ready for you.");
	}

	private static File resourcesDirectory = new File("src/test/resources");

	@Test
	public void firstApplicationWithSimpleTokenizer() throws Exception {
		SimpleTokenizer wtk = SimpleTokenizer.INSTANCE;
		String[] token = wtk.tokenize(getCV_fromTxt());
		for (String s : token) {
			logger.debug(s);
		}
	}

	@Test
	public void firstApplicationWithTokenizer() throws Exception {
		try (InputStream modelIn = new FileInputStream(
				resourcesDirectory.getAbsolutePath() + "/opennlp/model/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String tokens[] = tokenizer.tokenize(getCV_fromTxt());
			for (String s : tokens) {
				logger.debug(s);
			}
		}

	}
}
