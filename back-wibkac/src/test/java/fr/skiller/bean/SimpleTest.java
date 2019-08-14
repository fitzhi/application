package fr.skiller.bean;

import java.io.File;

import org.junit.Test;


public class SimpleTest {

	@Test
	public void test() throws Exception {
		
		File f = new File ("src/main/java/fr/skiller/bean/SimpleTest.java");
		System.out.println(f.exists());
	}
}
