/**
 * 
 */
package fr.skiller.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.skiller.data.internal.DataChart;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL Test of the JSON generation of the
 *         object GitDir.
 */
public class CodeDirJsonTest  {

	private static File resourcesDirectory = new File("src/test/resources");

	private static Logger logger = LoggerFactory.getLogger(CodeDirJsonTest.class.getCanonicalName());

	/**
	 * Initialization of the Google JSON parser.
	 */
	private static Gson gson = new GsonBuilder().create();

	@Before
	public void before() throws IOException {
		File dir = new File(resourcesDirectory.getAbsolutePath() + "/gitdir");
		if (!dir.exists()) {
			Assert.assertTrue(dir.mkdir());
			logger.debug(dir.getAbsolutePath() + " created !");
		}
	}
	
	@Test
	public void testGenerate() throws IOException {

		DataChart gRoot = new DataChart("VEGEO");
		gRoot.setNumberOfFiles(20);
		
		DataChart g1 = new DataChart("com");
		g1.setNumberOfFiles(15);
		
		DataChart g1_bis = new DataChart("fr");
		g1_bis.setNumberOfFiles(5);

		gRoot.addSubDir(g1);
		gRoot.addSubDir(g1_bis);
		
		
		DataChart g2 = new DataChart("google");
		g2.setNumberOfFiles(5);

		DataChart g3 = new DataChart("amazon");
		g3.setNumberOfFiles(10);

		g1.addSubDir(g2);
		g1.addSubDir(g3);

		String s = gson.toJson(gRoot);
		if (logger.isDebugEnabled()) {
			logger.debug(s);
		}
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(new File(resourcesDirectory.getAbsolutePath() + "/gitdir/gitdir.json")));
		bw.write(s);
		bw.close();

		Assert.assertTrue(true);
	}

}
