import { Component, OnInit } from '@angular/core';
import { CinematicService } from './cinematic.service';
import { Constants } from './constants';
import { ListProjectsService } from './list-projects-service/list-projects.service';
import { ListSkillService } from './list-skill-service/list-skill.service';
import { ListStaffService } from './list-staff-service/list-staff.service';
import { ReferentialService } from './referential.service';
import { StaffService } from './staff.service';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectStaffService } from './project/project-staff-service/project-staff.service';

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
  * Searching request typed & displayed in the searching field.
  */
  searching_what: string;

  /**
  * Filter on active employees if true (by default), or include all the employees in the database
  */
  activeOnly = true;

  /**
   * NEXT element ID, if any, from the underlying collection supplying the master/detail feature.
   */
  nextId: number;

  /**
   * PREVIOUS element ID, if any, from the underlying collection supplying the master/detail feature.
   */
  previousId: number;

  skill_activated = true;
  image_skill_activated = '/assets/img/skill-activated.png';
  image_skill_inactive = '/assets/img/skill-inactive.png';

  dev_activated = true;
  image_dev_activated = '/assets/img/developer-activated.png';
  image_dev_inactive = '/assets/img/developer-inactive.png';

  project_activated = true;
  image_project_activated = '/assets/img/project-activated.png';
  image_project_inactive = '/assets/img/project-inactive.png';


  constructor(
    private cinematicService: CinematicService,
    private listStaffService: ListStaffService,
    private projectStaffService: ProjectStaffService,
    private listSkillService: ListSkillService,
    private listProjectsService: ListProjectsService,
    private referentialService: ReferentialService,
    private location: Location,
    private staffService: StaffService,
    private router: Router) {

    this.cinematicService.currentActiveForm.subscribe(data => {

      this.formId = data.formIdentifier;
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
        case Constants.DEVELOPERS_CRUD: {
          this.in_master_detail = (
            (this.searching_what != null) ||
            (this.cinematicService.getFormerFormIdentifier() === Constants.PROJECT_TAB_STAFF));
          this.is_allowed_to_search = true;
          this.formTitle = 'Developer mode';
          break;
        }
        case Constants.DEVELOPERS_SEARCH: {
          this.formTitle = 'Searching';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.PROJECT_TAB_FORM: {
          this.formTitle = 'Project mode';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
        case Constants.PROJECT_SEARCH: {
          this.formTitle = 'Searching';
          this.in_master_detail = false;
          this.is_allowed_to_search = true;
          break;
        }
      }
    });

    this.cinematicService.newCollaboratorDisplayEmitted$.subscribe(data => {
      if (Constants.DEBUG) {
        console.log('Receiving new staff member with ID ' + data);
      }

      /*
       * To avoid the life cycle check error :
       * "Expression has changed after it was checked"
       */
      setTimeout(() => {
        switch (this.cinematicService.getFormerFormIdentifier()) {
          case Constants.DEVELOPERS_SEARCH:
            this.previousId = listStaffService.previousCollaboratorId(data);
            this.nextId = listStaffService.nextCollaboratorId(data);
            break;
          case Constants.PROJECT_TAB_STAFF:
            console.log('nope');
            this.previousId = projectStaffService.previousIdStaff(data);
            this.nextId = projectStaffService.nextIdStaff(data);
            break;
        }
      });
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
    this.referentialService.loadAllReferentials();

  }

  /**
	* Search button has been clicked.
	*/
  search(): void {
    if (Constants.DEBUG) {
      console.log('Searching ' + this.searching_what);
    }
    switch (this.formId) {
      case Constants.DEVELOPERS_SEARCH:
        if (Constants.DEBUG) {
          console.log('Reloading collaborators for search criteria ' + this.searching_what);
        }
        this.listStaffService.reloadCollaborators(this.searching_what, this.activeOnly);
        break;
      case Constants.SKILLS_SEARCH: {
        if (Constants.DEBUG) {
          console.log('Reloading skills for search criteria ' + this.searching_what);
        }
        this.listSkillService.reloadSkills(this.searching_what);
        this.staffService.countAll_groupBy_experience(this.activeOnly);
        break;
      }
      case Constants.PROJECT_SEARCH: {
        if (Constants.DEBUG) {
          console.log('Reloading project for search criteria ' + this.searching_what);
        }
        this.listProjectsService.reloadProjects(this.searching_what);
        break;
      }
    }
  }

  goBack(): void {
    this.location.back();
  }

  goNewDeveloper(): void {
    if (Constants.DEBUG) {
      console.log('Creating a new developer');
    }
    this.searching_what = null;
    this.router.navigate(['/user'], {});
  }

  switchToSkill() {
    this.searching_what = null;
    this.skill_activated = true;
    this.dev_activated = false;
    this.project_activated = false;
  }

  switchToDev() {
    this.searching_what = null;
    this.dev_activated = true;
    this.skill_activated = false;
    this.project_activated = false;
  }

  switchToProject() {
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
      case Constants.DEVELOPERS_CRUD:
        this.router.navigate(['/searchUser'], {});
        break;
      case Constants.PROJECT_TABS_HOST:
      case Constants.PROJECT_TAB_STAFF:
      case Constants.PROJECT_TAB_FORM:
        this.router.navigate(['/searchProject'], {});
        break;
    }
  }

  public list() {
    console.log(this.cinematicService.getFormerFormIdentifier() + ' ' + this.cinematicService.previousForm.url);
    switch (this.formId) {
      case Constants.DEVELOPERS_CRUD:
        switch (this.cinematicService.getFormerFormIdentifier()) {
          case Constants.DEVELOPERS_SEARCH:
            this.router.navigate(['/searchUser'], {});
            break;
          case Constants.PROJECT_TAB_STAFF:
            this.router.navigate([this.cinematicService.previousForm.url], {});
            break;
          default:
            break;
        }
        break;
      default:
        console.error('Unattempted formId ' + this.formId);
        break;
    }
  }
}
