import { StarsSkillLevelRenderComponent } from './tabs-staff/starsSkillLevelRenderComponent';

/**
 * Constants file
 */
export class Constants {

	/**
	  * DEBUG Mode for development purpose
	  */
	public static DEBUG = true;

	/**
	 * Value for Unknown object;
	 */
	public static UNKNOWN = -1;


	public static LEVEL_Expert = 5;
	public static LEVEL_Experienced = 4;
	public static LEVEL_Senior = 3;
	public static LEVEL_Junior = 2;
	public static LEVEL_Beginner = 1;

	public static LEVELS = [
		Constants.LEVEL_Expert,
		Constants.LEVEL_Experienced,
		Constants.LEVEL_Senior,
		Constants.LEVEL_Junior,
		Constants.LEVEL_Beginner];

	/**
	  * Form identifiers active
	  */
	public static WELCOME = 0;
	public static SKILLS_SEARCH = 1;
	public static SKILLS_CRUD = 2;
	public static DEVELOPERS_SEARCH = 3;
	public static DEVELOPERS_CRUD = 4;
	public static PROJECT_SEARCH = 5;
	public static PROJECT_TABS_HOST = 6;
	public static PROJECT_TAB_FORM = 7;
	public static PROJECT_TAB_STAFF = 8;
	public static TABS_STAFF_LIST = 9;
	public static BACK_TO_LIST = 10;


	public static CONTEXT: string[] = ['WELCOME', 'SKILLS_SEARCH', 'SKILLS_CRUD', 'DEVELOPERS_SEARCH',
		'DEVELOPERS_CRUD', 'PROJECT_SEARCH', 'PROJECT_TABS_HOST', 'PROJECT_TAB_FORM', 'PROJECT_TAB_STAFF', 'TABS_STAFF_LIST',
		'BACK_TO_LIST'];

	/*
	* Indexes of tab inside the form group Project
	*/
	public static PROJECT_IDX_TAB_FORM = 0;
	public static PROJECT_IDX_TAB_STAFF = 1;
	public static PROJECT_IDX_TAB_SUNBURST = 2;

	/*
	* Indexes of tab inside the form group STAFF
	*/
	public static STAFF_IDX_TAB_FORM = 0;
	public static STAFF_IDX_TAB_PROJECTS = 1;
	public static STAFF_IDX_TAB_EXPERIENCES = 2;

	/**
	 * Type of file allowed to be uploaded by the back-end for the application file.
	 * These constants declaration are inherited from the back-end project
	 */
	static FILE_TYPE_PDF = 0;
	static FILE_TYPE_DOCX = 1;
	static FILE_TYPE_DOC = 2;
	static FILE_TYPE_TXT = 3;

	public static APPLICATION_FILE_TYPE_ALLOWED = new Map([
		['application/pdf', Constants.FILE_TYPE_PDF],
		['application/vnd.openxmlformats-officedocument.wordprocessingml.document', Constants.FILE_TYPE_DOCX],
		['application/msword', Constants.FILE_TYPE_DOC]
	]);

	public static ERROR = -1;
	public static OK = 1;

	/**
	  * Type of errors
	  */
	public static MESSAGE_VOID = 0;
	public static MESSAGE_ERROR = 1;
	public static MESSAGE_INFO = 2;
	public static MESSAGE_WARNING = 3;
	public static MESSAGE_SUCCESS = 4;

	public static canAdd = true;

}
