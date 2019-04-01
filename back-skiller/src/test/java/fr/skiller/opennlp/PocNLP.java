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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

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
		File file = new File(getClass().getResource("/applications_files/ET_201709_UTF8.txt").getFile()); 
		if (logger.isDebugEnabled()) {
			logger.debug(identifyFileTypeUsingUrlConnectionGetContentType(file.getAbsolutePath()));
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		br.lines().forEach(line -> sb.append(line));
		br.close();
		return sb.toString();
	}

	private String getCV_fromDOC() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(identifyFileTypeUsingUrlConnectionGetContentType(resourcesDirectory.getAbsolutePath() + "/opennlp/in/ET_201709.doc"));
		}
		final InputStream in = getClass().getResource("/applications_files/ET_201709.doc").openStream();
		HWPFDocument doc = new HWPFDocument(in);
		String content = doc.getDocumentText();
		doc.close();
		return content;
	}
	
	private String getCV_fromDOCX() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(identifyFileTypeUsingUrlConnectionGetContentType(resourcesDirectory.getAbsolutePath() + "/opennlp/in/ET_201709.docx"));
		}
		XWPFDocument docx = new XWPFDocument(getClass().getResource("/applications_files/ET_201709.docx").openStream());
		XWPFWordExtractor we = new XWPFWordExtractor(docx);
		String s = we.getText();
		we.close();
		return s;
	}
	
	private String getCV_fromPDF() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug(identifyFileTypeUsingUrlConnectionGetContentType(resourcesDirectory.getAbsolutePath() + "/applications_files/ET_201709.pdf"));
		}
		PdfReader reader = new PdfReader(
	    		getClass().getResource("/applications_files/ET_201709.pdf").openStream());
	    final StringBuilder sb = new StringBuilder();
	    for (int pageNumber = 1; pageNumber < reader.getNumberOfPages(); pageNumber++) {
    		sb.append(PdfTextExtractor.getTextFromPage(reader, pageNumber));
	    }
	    reader.close();
	    return sb.toString();
	}
	
	final String car_accepted = "abcdefghijklmnopqrstuvwxyz-+#";

	public String cleanup(final String s) {
		StringBuilder sb = new StringBuilder();
		s.toLowerCase().chars().filter(car -> (car_accepted.indexOf(car) != -1)).forEach(car -> sb.append((char) car));
		return sb.toString();
	}

	private Map<String, Long> treat(final String[] token) throws IOException {
		
		List<Skill> listSkills = new ArrayList<>();
		for (String s : token) {
			String cleanLine = cleanup(s);

			List<Skill> skillsFromFile = GenerateSkillsInJSONTest.getReadSkillsJson();
			Map<String, Skill> skills = new HashMap<>();
			skillsFromFile.forEach((sk -> skills.put(cleanup(sk.getTitle()), sk)));
			if (skills.containsKey(cleanLine)) {
				listSkills.add(skills.get(cleanLine));
			}
		}
		
		Map<String, Long> mapSkills = listSkills.stream().collect(Collectors.groupingBy(exp -> exp.getTitle(), Collectors.counting()));
		Map<String, Long> mapSkillsSorted = new HashMap<>();
		
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

	private Map<String, Long> getSkillsfromCVinDOC() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		logger.debug("getSkillsfromCVinDOC : ");
		String[] token = wtk.tokenize(this.getCV_fromDOC());
		return treat(token);
	}
	
	private Map<String, Long> getSkillsfromCVinPDF() throws IOException {
		WhitespaceTokenizer wtk = WhitespaceTokenizer.INSTANCE;
		logger.debug("getSkillsfromCVinPDF : ");
		String[] token = wtk.tokenize(this.getCV_fromPDF());
		return treat(token);
	}
	
	@Test
	public void testExtractSkillsfromCVs() throws Exception {
		Map<String, Long> mapTXT = this.getSkillsfromCVinTXT();
		Map<String, Long> mapDOCX = this.getSkillsfromCVinDOCX();
		Map<String, Long> mapDOC = this.getSkillsfromCVinDOC();
		Map<String, Long> mapPDF = this.getSkillsfromCVinPDF();
		Assert.assertArrayEquals(mapTXT.keySet().toArray(), mapDOCX.keySet().toArray());
		Assert.assertArrayEquals(mapTXT.keySet().toArray(), mapDOC.keySet().toArray());
		// We loosed certainly few skills during the convert into PDF
		Assert.assertTrue(mapPDF.keySet().toArray().length == 30);
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
				resourcesDirectory.getAbsolutePath() + "/opennlp/en-token.bin")) {
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

	/**
	 * Identify file type of file with provided path and name
	 * using JDK's URLConnection.getContentType().
	 *
	 * @param fileName Name of file whose type is desired.
	 * @return Type of file for which name was provided.
	 */
	public String identifyFileTypeUsingUrlConnectionGetContentType(final String fileName)
	{
	   String fileType = "Undetermined";
	   try
	   {
	      final URL url = new URL("file://" + fileName);
	      final URLConnection connection = url.openConnection();
	      fileType = connection.getContentType();
	   }
	   catch (MalformedURLException badUrlEx)
	   {
	      System.out.println("ERROR: Bad URL - " + badUrlEx);
	   }
	   catch (IOException ioEx)
	   {
	      System.out.println("Cannot access URLConnection - " + ioEx);
	   }
	   return fileType;
	}
	/**
	 * Identify file type of file with provided path and name
	 * using JDK's URLConnection.guessContentTypeFromStream(InputStream).
	 *
	 * @param fileName Name of file whose type is desired.
	 * @return Type of file for which name was provided.
	 */
	public String identifyFileTypeUsingUrlConnectionGuessContentTypeFromStream(final String fileName)
	{
	   String fileType;
	   try
	   {
	      fileType = URLConnection.guessContentTypeFromStream(new FileInputStream(new File(fileName)));
	   }
	   catch (IOException ex)
	   {
	      System.out.println("ERROR: Unable to process file type for " + fileName + " - " + ex);
	      fileType = "null";
	   }
	   return fileType;
	}
}
