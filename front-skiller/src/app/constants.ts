import {StarsSkillLevelRenderComponent} from './staff/starsSkillLevelRenderComponent';

/**
 * Constants file
 */
export class Constants {

	/**
	* DEBUG Mode for development purpose
	*/
	public static DEBUG = true;

	/**
	* Form active
	*/
	public static WELCOME = 0;
	public static SKILLS_SEARCH = 1;
	public static SKILLS_CRUD = 2;
	public static DEVELOPPERS_SEARCH = 3;
	public static DEVELOPPERS_CRUD = 4;

	/**
	* Type of errors
	*/
  	public static MESSAGE_VOID = 0;
  	public static MESSAGE_ERROR = 1;
  	public static MESSAGE_INFO = 2;


	/**
	 * Declared settings for the grid dedicated to the skills inside the form STAFF 
	 */
	 public static SETTINGS_SKILL_SMARTTABLE = {
	    columns: {
	        title: {
		        title: 'Skills',
		        filter: false,
		        width: '75%',
		        type: 'text'
	        },
	        level: {
		        title: 'Level',
		        filter: false,
		        width: '25%',
		        type: 'custom',
		        renderComponent: StarsSkillLevelRenderComponent
	        }
	    },
	    hideSubHeader: true,
	    attr: {
        	class: 'table-bordered skills_table'
      	},
	    actions: {
	        add: false,
	        edit: false,
	        delete: false
	    },
	    pager: {
		    perPage: 8
		}
	};

	/**
	 * Declared settings for the grid dedicated to the PROJECTS inside the form STAFF 
	 */
	 public static SETTINGS_PROJECTS_SMARTTABLE = {
	    columns: {
	        name: {
		        title: 'Projects',
		        filter: false,
		        width: '100%',
		        type: 'text'
	        }
	    },
	    add: {
      		addButtonContent: '<img width="20px" height="20px" src="/assets/img/add.jpeg"></img>',
  			createButtonContent: '<img width="25px" height="25px" src="/assets/img/update-cloud.jpeg"></img>',
  			cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>'    		
   		},
	    delete: {
      		deleteButtonContent: '<img width="20px" height="20px" src="/assets/img/delete.jpeg"></img>',
   		},
	    edit: {
      		editButtonContent: '<img width="20px" height="20px" src="/assets/img/edit.jpeg"></img>',
  			cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>'    		
 			saveButtonContent: '<img width="25px" height="25px" src="/assets/img/update-cloud.jpeg"></img>',
   		},
	    hideSubHeader: false,
	    attr: {
        	class: 'table table-bordered'
      	},
	    actions: {
  			columnTitle: '____',
 	        add: true,
	        edit: true,
	        delete: true
	    },
	    pager: {
		    perPage: 8
		}
	}
	
}

	