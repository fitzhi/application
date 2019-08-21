package fr.skiller.bean;

import java.io.File;

import org.junit.Test;


public class SimpleTest {

	@Test
	public void test() throws Exception {

		String baseDir = "";
		String sunburstData_getLocation = "root";
		String source_getFilename = "test-perf.groovy";
		
		final String searchedFile = (baseDir.indexOf("root/") == 0)
				? (baseDir + sunburstData_getLocation + "/" + source_getFilename).substring("root/".length())
				: (baseDir + sunburstData_getLocation + "/" + source_getFilename).substring("/".length());	
		
			System.out.println(searchedFile);;
	}
}
	
