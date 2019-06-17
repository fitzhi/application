package fr.skiller;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Error {

	public static final int CODE_UNDEFINED = -999;
	
	public static final int CODE_REPO_MUST_BE_ALREADY_CLONED = -1000;
	public static final String MESSAGE_REPO_MUST_BE_ALREADY_CLONED = "The repository must be already cloned on the file system";

	public static final int CODE_STAFF_NOFOUND = -1001;
	public static final String MESSAGE_STAFF_NOFOUND = "There is no staff member for id {0}";

	public static final int CODE_PROJECT_NOFOUND = -1002;
	public static final String MESSAGE_PROJECT_NOFOUND = "There is no project for the identifier {0}";
	public static final int UNKNOWN_PROJECT = -1;
			
	public static final int CODE_IO_ERROR = -1003;
	public static final String MESSAGE_IO_ERROR = "IO Error with file {0}";

	public static final int CODE_SKILL_NOFOUND = -1004;
	public static final String MESSAGE_SKILL_NOFOUND = "There is no skill for the identifier {0}";
	
	public static final int CODE_FILE_CONNECTION_SETTINGS_NOFOUND = -1005;
	public static final String MESSAGE_FILE_CONNECTION_SETTINGS_NOFOUND = "The connection property file {0} doesn't exist!";
	
	public static final int CODE_UNEXPECTED_VALUE_PARAMETER = -1006;
	public static final String MESSAGE_UNEXPECTED_VALUE_PARAMETER = "The variable {0} has the unexpected value {1}";
	
	public static final int CODE_MULTIPLE_LOGIN = -1007;
	public static final String MESSAGE_MULTIPLE_LOGIN = "Cannot connect the login {0} to a developer. {1} are eligible.";
	
	public static final int CODE_MULTIPLE_TASK = -1008;
	public static final String MESSAGE_MULTIPLE_TASK = "This asynchronous operation is already launched.";

	public static final int CODE_LOGIN_ALREADY_EXIST = -1009;
	public static final String MESSAGE_LOGIN_ALREADY_EXIST = "Login must be unique. And the login {0} already exists for {1} {2}.";

	public static final int CODE_UNREGISTERED_LOGIN = -1010;
	public static final String MESSAGE_UNREGISTERED_LOGIN = "Your login does not exist. Please contact your administrator.";
	
	public static final int CODE_INVALID_LOGIN_PASSWORD = -1011;
	public static final String MESSAGE_INVALID_LOGIN_PASSWORD = "Invalid login/password."; //NOSONAR
	
	public static final int CODE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED = -1012;
	public static final String MESSAGE_INVALID_FIRST_USER_ADMIN_ALREADY_CREATED = "Cannot create 2 times, the first admin user."; 
		
	public static final int CODE_CANNOT_SELF_CREATE_USER = -1013;
	public static final String MESSAGE_CANNOT_SELF_CREATE_USER = "You cannot create your own user. Please contact your administrator"; 

	private Error() {
	}
	
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
