package com.fitzhi.data.internal;

/**
 * Internal Class Test pour testing purpose.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL 
 */
public class ForTest {
	
	private String test;
	
	/**
	 * Empty constructor.
	 */
	public ForTest() { }
	
	/**
	 * @param test
	 */
	public ForTest(String test) {
		super();
		this.setTest(test);
	}

	@Override
	public String toString() {
		return "Test [mTest=" + getTest() + "]";
	}

	/**
	 * @return the test
	 */
	public String getTest() {
		return test;
	}

	/**
	 * @param test the test to set
	 */
	public void setTest(String test) {
		this.test = test;
	}

	
}
