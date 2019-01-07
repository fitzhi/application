package fr.skiller;

public class Error {

	public final static int CODE_UNDEFINED = -999;
	
	public final static int CODE_REPO_MUST_BE_ALREADY_CLONED = -1000;
	public final static String MESSAGE_REPO_MUST_BE_ALREADY_CLONED = "The repository must be already cloned on the file system";

	public final static int CODE_STAFF_NOFOUND = -1001;
	public final static String MESSAGE_STAFF_NOFOUND = "There is no staff member for id {0}";

	public final static int CODE_PROJECT_NOFOUND = -1002;
	public final static String MESSAGE_PROJECT_NOFOUND = "There is no project for the identifier {0}";


}
