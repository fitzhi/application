package fr.skiller;

import java.util.Date;

public class Global {

	public static String LN = System.getProperty("line.separator");
	
	/**
	 * Value of a key when no record are found.<BR/>
	 * The default value is equal to -1.
	 */
	public static int UNKNOWN = -1;

	public static Date now() {
		return new Date();
	}
	
}
