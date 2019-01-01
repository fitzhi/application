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
		final SunburstData root = new SunburstData("root");
		SunburstData data = root; 
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s[] = testClassname.split(File.separator);
		for (int i=0; i<s.length-1; i++) {
			data = data.addsubDir(new SunburstData(s[i]));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
        
        assertThat(root.children.get(0).children.get(0).directory).isEqualTo("skiller");
        assertThat(root.children.get(0).children.get(0).children.get(0).directory).isEqualTo("test");
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).directory).isEqualTo("world");
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.get(0).directory).isEqualTo("mbappe");
        
	}

	@Test
	public void testInjectFileName() {
		final SunburstData root = new SunburstData("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		root.injectFile(root, testClassname.split(File.separator), new Date());

		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
        
        assertThat(root.children.get(0).children.get(0).directory).isEqualTo("skiller");
        assertThat(root.children.get(0).children.get(0).children.get(0).directory).isEqualTo("test");
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).directory).isEqualTo("world");
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.get(0).directory).isEqualTo("mbappe");        
	}
	
	@Test
	public void addTwoClass() {
		
		final SunburstData root = new SunburstData("root");
		SunburstData data = root; 
		
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		String s1[] = testClassname.split(File.separator);
		for (int i=0; i<s1.length-1; i++) {
			data = data.addsubDir(new SunburstData(s1[i]));
			data.numberOfFiles++;
		}
		
		data = root;
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		String s2[] = testClassname.split(File.separator);
		for (int i=0; i<s2.length-1; i++) {
			data = data.addsubDir(new SunburstData(s2[i]));
			data.numberOfFiles++;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
        assertThat(root.children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(2);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
        assertThat(root.children.get(0).children.get(0).children.get(0).children.get(1).children.size()).isEqualTo(1);
        
	}

	@Test
	public void testInjectFileName2Times() {
		final SunburstData root = new SunburstData("root");
		String testClassname = "fr/skiller/test/world/mbappe/Number9.java";
		root.injectFile(root, testClassname.split(File.separator), new Date());
		testClassname = "fr/skiller/test/world-champion/mbappe/Number9.java";
		root.injectFile(root, testClassname.split(File.separator), new Date());

		if (logger.isDebugEnabled()) {
			logger.debug(root.toString());
		}
		
		assertThat(root.children.size()).isEqualTo(1);
		assertThat(root.children.get(0).children.size()).isEqualTo(1);
		assertThat(root.children.get(0).children.get(0).children.size()).isEqualTo(1);
		assertThat(root.children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(2);
		assertThat(root.children.get(0).children.get(0).children.get(0).children.get(0).children.size()).isEqualTo(1);
		assertThat(root.children.get(0).children.get(0).children.get(0).children.get(1).children.size()).isEqualTo(1);
	 }

}
