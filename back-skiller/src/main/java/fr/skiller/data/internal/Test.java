package fr.skiller.data.internal;

/**
 * Internal Class Test pour testing purpose.
 * 
 * @author Fr&eacute;d&eacute;ric VIDAL 
 */
public class Test {
	public String test;
	
	/**
	 * Empty constructor.
	 */
	public Test() { }
	
	/**
	 * @param test
	 */
	public Test(String test) {
		super();
		this.test = test;
	}

	@Override
	public String toString() {
		return "Test [test=" + test + "]";
	}
}
