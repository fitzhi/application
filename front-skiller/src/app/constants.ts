import {StarsSkillLevelRenderComponent} from './staff/starsSkillLevelRenderComponent';

/**
 * Constants file
 */
export class Constants {

  /**
	* DEBUG Mode for development purpose
	*/
  public static DEBUG = true;

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

  public static canAdd = true;

  /**
	 * Declared settings for the grid dedicated to the experiences inside the form STAFF.
	 */
  public static SETTINGS_EXPERIENCE_SMARTTABLE = {
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
        defaultValue: 1,
        type: 'custom',
        renderComponent: StarsSkillLevelRenderComponent,
        editor: {
          type: 'list',
          config: {
            list: [{value: '1', title: '*'},
            {value: '2', title: '**'},
            {value: '3', title: '***'},
            {value: '4', title: '****'},
            {value: '5', title: '*****'}]
          },
        },
      }
    },
    attr: {
      class: 'table-bordered skills_table'
    },
    actions: {
      columnTitle: '____',
      add: Constants.canAdd,
      edit: true,
      delete: true,
    },
    add: {
      addButtonContent: '<img src="/assets/img/add.jpeg"></img>',
      createButtonContent: '<img src="/assets/img/update-cloud.jpeg"></img>',
      cancelButtonContent: '<img src="/assets/img/cancel.png"></img>',
      confirmCreate: true,
    },
    delete: {
      deleteButtonContent: '<img src="/assets/img/delete.jpeg"></img>',
      confirmDelete: true,
    },
    edit: {
      editButtonContent: '<img src="/assets/img/edit.jpeg"></img>',
      cancelButtonContent: '<img src="/assets/img/cancel.png"></img>',
      saveButtonContent: '<img src="/assets/img/update-cloud.jpeg"></img>',
      confirmSave: true,
    },
    hideSubHeader: false,
    noDataMessage: 'No experience registered yet.',
    pager: {
      perPage: 5
    }
  };

  /**
   * Declared settings for the grid dedicated to the skills inside the form STAFF.
   */
  public static SETTINGS_SKILL_SMARTTABLE = {
    columns: {
      title: {
        title: 'Skills',
        filter: false,
        width: '75%',
        type: 'text'
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
    noDataMessage: 'No skill registered yet.',
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


