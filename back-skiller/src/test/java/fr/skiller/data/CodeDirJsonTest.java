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

import fr.skiller.data.internal.RiskChartData;

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

		RiskChartData gRoot = new RiskChartData("VEGEO");
		gRoot.numberOfFiles = 20;
		
		RiskChartData g1 = new RiskChartData("com");
		g1.numberOfFiles = 15;
		
		RiskChartData g1_bis = new RiskChartData("fr");
		g1_bis.numberOfFiles = 5;

		gRoot.addsubDir(g1);
		gRoot.addsubDir(g1_bis);
		
		
		RiskChartData g2 = new RiskChartData("google");
		g2.numberOfFiles = 5;

		RiskChartData g3 = new RiskChartData("amazon");
		g3.numberOfFiles = 10;

		g1.addsubDir(g2);
		g1.addsubDir(g3);

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
