package fr.skiller;

import java.util.Date;

public class Global {

	public static final String LN = System.getProperty("line.separator");
	
	public static final String  BACKEND_RETURN_CODE = "backend.return_code";
	public static final String  BACKEND_RETURN_MESSAGE = "backend.return_message";
	
	/**
	 * Value of a key when no record are found.<BR/>
	 * The default value is equal to -1.
	 */
	public static final int UNKNOWN = -1;

	public static Date now() {
		return new Date();
	}
	
}
