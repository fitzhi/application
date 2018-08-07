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
  public static PROJECT_SEARCH = 5;
  public static PROJECT_CRUD = 6;

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
        renderComponent: StarsSkillLevelRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [ { value: '1', title: '*'},
                    { value: '2', title: '**'},
                    { value: '3', title: '***'},
                    { value: '4', title: '****'},
                    { value: '5', title: '*****'}]
          },
        },
      }
    },
    attr: {
      class: 'table-bordered skills_table'
    },
    actions: {
      columnTitle: '____',
      add: true,
      edit: true,
      delete: true
    },
    add: {
      addButtonContent: '<img width="20px" height="20px" src="/assets/img/add.jpeg"></img>',
      createButtonContent: '<img width="25px" height="25px" src="/assets/img/update-cloud.jpeg"></img>',
      cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>',
      confirmCreate: true,
    },
    delete: {
      deleteButtonContent: '<img width="20px" height="20px" src="/assets/img/delete.jpeg"></img>',
      confirmDelete: true,
    },
    edit: {
      editButtonContent: '<img width="20px" height="20px" src="/assets/img/edit.jpeg"></img>',
      cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>',
      saveButtonContent: '<img width="25px" height="25px" src="/assets/img/update-cloud.jpeg"></img>',
      confirmSave: true,
    },
    hideSubHeader: false,
    noDataMessage: 'No experience registered yet.',
    pager: {
      perPage: 5
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
      cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>',
      confirmCreate: true,
    },
    delete: {
      deleteButtonContent: '<img width="20px" height="20px" src="/assets/img/delete.jpeg"></img>',
      confirmDelete: true,
    },
    edit: {
      editButtonContent: '<img width="20px" height="20px" src="/assets/img/edit.jpeg"></img>',
      cancelButtonContent: '<img width="20px" height="20px" src="/assets/img/cancel.png"></img>',
      saveButtonContent: '<img width="25px" height="25px" src="/assets/img/update-cloud.jpeg"></img>',
      confirmSave: true,
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
    noDataMessage: 'There is no skill associated yet.',
    pager: {
      perPage: 5
    }
  };
}


