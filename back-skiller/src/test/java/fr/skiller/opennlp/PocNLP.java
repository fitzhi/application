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
import java.util.stream.Collectors;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.skiller.data.GenerateSkillsInJSONTest;
import fr.skiller.data.internal.Skill;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Simple test of OPENNLP
 */
public class PocNLP {

	Logger logger = LoggerFactory.getLogger(PocNLP.class.getCanonicalName());
	
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
		String s = we.getText();
		we.close();
		return s;
	}
	
	final String car_accepted = "abcdefghijklmnopqrstuvwxyz-+#";

	public String cleanup(final String s) {
		StringBuilder sb = new StringBuilder();
		s.toLowerCase().chars().filter(car -> (car_accepted.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

	private Map<String, Long> treat(final String[] token) throws IOException {
		
		List<Skill> listSkills = new ArrayList<Skill>();
		for (String s : token) {
			String cleanLine = cleanup(s);

			List<Skill> skillsFromFile = GenerateSkillsInJSONTest.getReadSkillsJson();
			Map<String, Skill> skills = new HashMap<String, Skill>();
			skillsFromFile.forEach((sk -> skills.put(cleanup(sk.title), sk)));
			if (skills.containsKey(cleanLine)) {
				listSkills.add(skills.get(cleanLine));
			}
		}
		
		Map<String, Long> mapSkills = listSkills.stream().collect(Collectors.groupingBy(exp -> exp.title, Collectors.counting()));
		Map<String, Long> mapSkillsSorted = new HashMap<String, Long>();
		
		mapSkills.keySet().stream().sorted().forEach(key -> mapSkillsSorted.put(key, mapSkills.get(key)));
		mapSkillsSorted.keySet().forEach(skill -> logger.debug(skill + " " + mapSkills.get(skill)));

		return mapSkillsSorted;
	}

	/**
	 * @return 
	 * @throws IOException
	 */
	private Map<String, Long> getSkillsfromCVinTXT() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		logger.debug("getSkillsfromCVinDOCX : ");
		String[] token = wtk.tokenize(this.getCV_fromTxt());
		return treat(token);
	}
	
	private Map<String, Long> getSkillsfromCVinDOCX() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		logger.debug("getSkillsfromCVinDOCX : ");
		String[] token = wtk.tokenize(this.getCV_fromDOCX());
		return treat(token);
	}

	@Test
	public void testExtractSkillsfromCVs() throws Exception {
		Map<String, Long> mapTXT = this.getSkillsfromCVinTXT();
		Map<String, Long> mapDOCX = this.getSkillsfromCVinDOCX();
		Assert.assertArrayEquals(mapTXT.keySet().toArray(), mapDOCX.keySet().toArray());
	}


	@Test
	public void simpleSimpleTokenizer() {
		SimpleTokenizer wtk = SimpleTokenizer.INSTANCE;
		String[] token = wtk.tokenize("Hello World. I am ready for you.");
		if (logger.isDebugEnabled()) {
			for (String s : token) {
				logger.debug(s);
			}
		}
	}

	private static File resourcesDirectory = new File("src/test/resources");

	@Test
	public void firstApplicationWithSimpleTokenizer() throws Exception {
		SimpleTokenizer wtk = SimpleTokenizer.INSTANCE;
		String[] token = wtk.tokenize(getCV_fromTxt());
		if (logger.isDebugEnabled()) {
			for (String s : token) {
				logger.debug(s);
			}
		}
	}

	@Test
	public void firstApplicationWithTokenizer() throws Exception {
		try (InputStream modelIn = new FileInputStream(
				resourcesDirectory.getAbsolutePath() + "/opennlp/model/en-token.bin")) {
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String tokens[] = tokenizer.tokenize(getCV_fromTxt());
			if (logger.isDebugEnabled()) {
				for (String s : tokens) {
					logger.debug(s);
				}
			}
		}

	}
}
