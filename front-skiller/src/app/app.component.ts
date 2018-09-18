import {Component, OnInit} from '@angular/core';
import {CinematicService} from './cinematic.service';
import {DataService} from './data.service';
import {Constants} from './constants';
import { ListSkillService } from './list-skill-service/list-skill.service';
import { ListStaffService } from './list-staff-service/list-staff.service';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  /**
  * Title of the form
  */
  public formTitle: string;

  /**
  * Form Id
  */
  public formId: Number;

  /**
  * Searching mode ON. The INPUT searching block is enabled.
  */
  is_allowed_to_search: boolean;

  /**
  * Master/Detail mode ON. The goBack() and goFoward() buttons are visible
  */
  in_master_detail: boolean;

  /**
  * content of the searching filed.
  */
  searching_what: string;

  private nextId: number;
  private previousId: number;

  private skill_activated = true;
  private image_skill_activated = '/assets/img/skill-activated.png';
  private image_skill_inactive = '/assets/img/skill-inactive.png';

  private dev_activated = true;
  private image_dev_activated = '/assets/img/developper-activated.png';
  private image_dev_inactive = '/assets/img/developper-inactive.png';

  private project_activated = true;
  private image_project_activated = '/assets/img/project-activated.png';
  private image_project_inactive = '/assets/img/project-inactive.png';

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService,
    private listStaffService: ListStaffService,
    private listSkillService: ListSkillService,
    private location: Location,
    private router: Router) {

    this.cinematicService.newFormDisplayEmitted$.subscribe(data => {

      this.formId = data;
      switch (this.formId) {
        case Constants.WELCOME: {
          this.formTitle = 'Who\'s who';
          this.in_master_detail = false;
          this.is_allowed_to_search = false;
          break;
        }
        case Constants.SKILLS_CRUD: {
          this.formTitle = 'Skill mode';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.SKILLS_SEARCH: {
          this.formTitle = 'Searching';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.DEVELOPPERS_CRUD: {
          this.in_master_detail = (this.searching_what != null);
          this.is_allowed_to_search = true;
          this.formTitle = 'Developer mode';
          break;
        }
        case Constants.DEVELOPPERS_SEARCH: {
          this.formTitle = 'Searching';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.PROJECT_CRUD: {
          this.formTitle = 'Project mode';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.PROJECT_SEARCH: {
          this.formTitle = 'Searching project';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
      }

    });


    this.dataService.newCollaboratorDisplayEmitted$.subscribe(data => {
      if (Constants.DEBUG) {
        console.log('Receiving data ' + data);
      }

      /*
       * To avoid the life cycle check error :
       * "Expression has changed after it was checked"
       */
      setTimeout(() => {
        this.previousId = listStaffService.previousCollaboratorId(data);
        this.nextId = listStaffService.nextCollaboratorId(data);
      }
      );
      if (Constants.DEBUG) {
        console.log('this.previousId ' + this.previousId);
        console.log('this.nextId ' + this.nextId);
      }
    });
  }

  ngOnInit() {
    this.formTitle = 'Welcome';
    this.is_allowed_to_search = true;
    this.dev_activated = false;
    this.skill_activated = false;
    this.project_activated = false;
  }

  /**
	* Search button has been clicked.
	*/
  search(): void {
    if (Constants.DEBUG) {
      console.log('Searching ' + this.searching_what);
    }
    switch (this.formId) {
      case Constants.DEVELOPPERS_SEARCH:
        if (Constants.DEBUG) {
          console.log('Reloading collaborators for search criteria ' + this.searching_what);
        }
        this.listStaffService.reloadCollaborators(this.searching_what);
        break;
      case Constants.SKILLS_SEARCH: {
        if (Constants.DEBUG) {
          console.log('Reloading skills for search criteria ' + this.searching_what);
        }
        this.listSkillService.reloadSkills(this.searching_what);
        break;
      }
      case Constants.PROJECT_SEARCH: {
        if (Constants.DEBUG) {
          console.log('Reloading project for search criteria ' + this.searching_what);
        }
        this.dataService.reloadProjects(this.searching_what);
        break;
      }
    }
  }

  goBack(): void {
    this.location.back();
  }

  goNewDeveloper(): void {
    if (Constants.DEBUG) {
      console.log('Entering in the method goNewDeveloper()');
    }
    this.searching_what = null;
    this.router.navigate(['/user'], {});
  }

  switchToSkill () {
    this.searching_what = null;
    this.skill_activated = true;
    this.dev_activated = false;
    this.project_activated = false;
  }

  switchToDev () {
    this.searching_what = null;
    this.dev_activated = true;
    this.skill_activated = false;
    this.project_activated = false;
  }

  switchToProject () {
    this.searching_what = null;
    this.project_activated = true;
    this.dev_activated = false;
    this.skill_activated = false;
  }

  /**
   * User has entered into the search INPUT.
   */
  focusSearch() {
    switch (this.formId) {
        case Constants.SKILLS_CRUD:
          this.router.navigate(['/searchSkill'], {});
          break;
        case Constants.DEVELOPPERS_CRUD:
          this.router.navigate(['/searchUser'], {});
          break;
        case Constants.PROJECT_CRUD:
          this.router.navigate(['/searchProject'], {});
          break;
    }
  }
}
