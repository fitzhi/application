package fr.skiller;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Error {

	public final static int CODE_UNDEFINED = -999;
	
	public final static int CODE_REPO_MUST_BE_ALREADY_CLONED = -1000;
	public final static String MESSAGE_REPO_MUST_BE_ALREADY_CLONED = "The repository must be already cloned on the file system";

	public final static int CODE_STAFF_NOFOUND = -1001;
	public final static String MESSAGE_STAFF_NOFOUND = "There is no staff member for id {0}";

	public final static int CODE_PROJECT_NOFOUND = -1002;
	public final static String MESSAGE_PROJECT_NOFOUND = "There is no project for the identifier {0}";
	public final static int UNKNOWN_PROJECT = -1;
			
	public final static int CODE_IO_ERROR = -1003;
	public final static String MESSAGE_IO_ERROR = "IO Error with file {0}";

	public final static int CODE_SKILL_NOFOUND = -1004;
	public final static String MESSAGE_SKILL_NOFOUND = "There is no skill for the identifier {0}";
	
	public final static int CODE_FILE_CONNECTION_SETTINGS_NOFOUND = -1005;
	public final static String MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND = "The connection property file {0} doesn't exist!";
	
	public final static int CODE_UNEXPECTED_VALUE_PARAMETER = -1006;
	public final static String MESSAGE_UNEXPECTED_VALUE_PARAMETER = "The variable {0} has the unexpected value {1}";
	
	public final static int CODE_MULTIPLE_LOGIN = -1007;
	public final static String MESSAGE_MULTIPLE_LOGIN = "Cannot connect the login {0} to a developer. {1} are eligible.";
	
	public final static int CODE_MULTIPLE_TASK = -1008;
	public final static String MESSAGE_MULTIPLE_TASK = "This asynchronous operation is already launched.";
	
	/**
	 * @param e the exception
	 * @return the stack trace in {@code String} format
	 */
	public static String getStackTrace(final Exception e) {
		final StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
