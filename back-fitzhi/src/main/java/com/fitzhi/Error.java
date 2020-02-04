package com.fitzhi;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Error {

	public static final String SHOULD_NOT_PASS_HERE = "SHOULD NOT PASS HERE !";
	
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
	public static final String MESSAGE_UNEXPECTED_VALUE_PARAMETER = "The mode of connection is unknown. You have to setup this mode on the project form.";
	
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

	public static final int CODE_PARSING_SOURCE_CODE = -1014;
	public static final String MESSAGE_PARSING_SOURCE_CODE = "an error occurs when parsing the source code on server."; 

	public static final int CODE_MISSION_NOFOUND = -1015;
	public static final String MESSAGE_MISSION_NOFOUND = "There is no mission for the staff identifier {0} and project identifier {1}";

	public static final int CODE_STAFF_ACTIVE_ON_PROJECT = -1016;
	public static final String MESSAGE_STAFF_ACTIVE_ON_PROJECT = "You cannot remove an active project. {0} commits are registered for this staff member";
	
	public static final int CODE_FILE_REFERENTIAL_NOFOUND = -1017;
	public static final String MESSAGE_FILE_REFERENTIAL_NOFOUND = "The referential file {0} doesn't exist!";

	public static final int CODE_SONAR_KEY_NOFOUND = -1018;
	public static final String MESSAGE_SONAR_KEY_NOFOUND = "There is no Sonar project {0} declared for the project {1}";

	public static final int CODE_PROJECT_TOPIC_ALREADY_DECLARED = -1019;
	public static final String MESSAGE_PROJECT_TOPIC_ALREADY_DECLARED = "The topic {0} is already declared for the project {1}";
	
	public static final int CODE_PROJECT_TOPIC_UNKNOWN = -1020;
	public static final String MESSAGE_PROJECT_TOPIC_UNKNOWN = "The topic {0} is not involved in project {1}";

	public static final int CODE_PROJECT_INVALID_WEIGHTS = -1021;
	public static final String MESSAGE_PROJECT_INVALID_WEIGHTS = "The sum of weights has to be equal to 100";
	
	public static final int CODE_TASK_NOT_FOUND = -1022;
	public static final String MESSAGE_TASK_NOT_FOUND = "There is no operation {0} for the project id {1}";
	
	public static final int CODE_CONTRIBUTOR_INVALID = -1023;
	public static final String MESSAGE_CONTRIBUTOR_INVALID = "Contributor cannot be created for the staff member %s inside the project %s";
	
	public static final int CODE_IO_EXCEPTION = -1024;
	
	public static final int CODE_ENCRYPTION_FAILED = -1024;
	public static final String MESSAGE_ENCRYPTION_FAILED = "Internal error : En(De)cryption failed ! Error message : {0}";
	
	public static final int CODE_CANNOT_RETRIEVE_ATTACHMENTFILE = -1025;
	public static final String LIB_CANNOT_RETRIEVE_ATTACHMENTFILE = "Cannot retrieve the attachmentFile n°{2} for the project/topic {0}/{1}";
	
	private Error() {
	}
	
	/**
	 * @param e the exception
	 * @return the stack trace in {@code String} format
	 */
	public static String getStackTrace(final Throwable e) {
		return Arrays.stream(e.getStackTrace())
	    	.map(StackTraceElement::toString)
	    	.collect(Collectors.joining("\n"));
	}
	
}
