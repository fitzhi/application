/**
 * 
 */
package fr.skiller.data.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.skiller.data.source.CommitRepository;
import fr.skiller.source.scanner.git.GitScanner;

/**
 * @author Fr&eacute;d&eacute;ric VIDAL
 * Testing the SunburstData class.
 */
public class SunburstDataTest {
	
 	/**
 	 * The logger for the SunburstDataTest class.
 	 */
	Logger logger = LoggerFactory.getLogger(SunburstDataTest.class.getCanonicalName());

	@Test
	public void addOneClass() {
		final RiskChartData root = new RiskChartData("root");
		RiskChartData data = root; 
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s[] = testClassname.split(File.separator);
		for (int i=0; i<s.length-1; i++) {
			data = data.addsubDir(new RiskChartData(s[i]));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        
        assertThat(root.getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("skiller");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("test");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("world");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("mbappe");
        
	}

	@Test
	public void testInjectFileName() {
		final RiskChartData root = new RiskChartData("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		int[] i = new int[0];
		root.injectFile(root, testClassname.split(File.separator), new Date(), i);

		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        
        assertThat(root.getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("skiller");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("test");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("world");
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getLocation()).isEqualTo("mbappe");        
	}
	
	@Test
	public void addTwoClass() {
		
		final RiskChartData root = new RiskChartData("root");
		RiskChartData data = root; 
		
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s1[] = testClassname.split(File.separator);
		for (int i=0; i<s1.length-1; i++) {
			data = data.addsubDir(new RiskChartData(s1[i]));
			data.setNumberOfFiles(data.getNumberOfFiles() + 1);
		}
		
		data = root;
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		String s2[] = testClassname.split(File.separator);
		for (int i=0; i<s2.length-1; i++) {
			data = data.addsubDir(new RiskChartData(s2[i]));
			data.setNumberOfFiles(data.getNumberOfFiles() + 1);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(2);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
        assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().size()).isEqualTo(1);
        
	}

	@Test
	public void testInjectFileName2Times() {
		final RiskChartData root = new RiskChartData("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		int[] i = new int[0];
		root.injectFile(root, testClassname.split(File.separator), new Date(), i);
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		root.injectFile(root, testClassname.split(File.separator), new Date(), i);

		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
		
		assertThat(root.getChildren().size()).isEqualTo(1);
		assertThat(root.getChildren().get(0).getChildren().size()).isEqualTo(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(2);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size()).isEqualTo(1);
		assertThat(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().size()).isEqualTo(1);
	 }

}
