/**
 * 
 */
package fr.skiller.data.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.time.LocalDate;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static fr.skiller.Global.INTERNAL_FILE_SEPARATOR;

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
		final DataChart root = new DataChart("root");
		DataChart data = root; 
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s[] = testClassname.split(INTERNAL_FILE_SEPARATOR);
		for (int i=0; i<s.length-1; i++) {
			data = data.addSubDir(new DataChart(s[i]));
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
		final DataChart root = new DataChart("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		int[] i = new int[0];
		root.injectFile(root, testClassname.split(INTERNAL_FILE_SEPARATOR), 1, LocalDate.now(), i);

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
		
		final DataChart root = new DataChart("root");
		DataChart data = root; 
		
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s1[] = testClassname.split(INTERNAL_FILE_SEPARATOR);
		for (int i=0; i<s1.length-1; i++) {
			data = data.addSubDir(new DataChart(s1[i]));
			data.setNumberOfFiles(data.getNumberOfFiles() + 1);
		}
		
		data = root;
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		String s2[] = testClassname.split(INTERNAL_FILE_SEPARATOR);
		for (int i=0; i<s2.length-1; i++) {
			data = data.addSubDir(new DataChart(s2[i]));
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
		final DataChart root = new DataChart("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		int[] i = new int[0];
		root.injectFile(root, testClassname.split(INTERNAL_FILE_SEPARATOR), 1, LocalDate.now(), i);
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		root.injectFile(root, testClassname.split(INTERNAL_FILE_SEPARATOR), 1, LocalDate.now(), i);

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
