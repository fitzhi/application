import { Component, OnInit, OnDestroy } from '@angular/core';
import { CinematicService } from './service/cinematic.service';
import { Constants } from './constants';
import { ListProjectsService } from './list-projects-service/list-projects.service';
import { StaffListService } from './staff-list-service/staff-list.service';
import { ReferentialService } from './service/referential.service';
import { StaffService } from './service/staff.service';
import { Router } from '@angular/router';
import { ProjectStaffService } from './project/project-staff-service/project-staff.service';
import { BaseComponent } from './base/base.component';
import { TabsStaffListService } from './tabs-staff-list/service/tabs-staff-list.service';
import { SkillService } from './service/skill.service';
import { ListCriteria } from './data/listCriteria';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent extends BaseComponent implements OnInit, OnDestroy {

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

  dev_activated = true;

  project_activated = true;

  constructor(
    private cinematicService: CinematicService,
    private listStaffService: StaffListService,
    private tabsStaffListService: TabsStaffListService,
    private projectStaffService: ProjectStaffService,
    private skillService: SkillService,
    private listProjectsService: ListProjectsService,
    private referentialService: ReferentialService,
    private staffService: StaffService,
    private router: Router) {

    super();

    this.subscriptions.add(
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
            this.formTitle = 'Skill';
            this.in_master_detail = false;
            this.is_allowed_to_search = true;
            break;
          }
          case Constants.SKILLS_SEARCH: {
            this.formTitle = 'Skills Search';
            this.in_master_detail = false;
            this.is_allowed_to_search = true;
            break;
          }
          case Constants.DEVELOPERS_CRUD: {
            this.in_master_detail = this.tabsStaffListService.inMasterDetail;
            this.is_allowed_to_search = true;
            this.formTitle = 'Staff';
            break;
          }
          case Constants.DEVELOPERS_SEARCH: {
            this.formTitle = 'Staff Search';
            this.in_master_detail = false;
            this.is_allowed_to_search = true;
            break;
          }
          case Constants.PROJECT_TAB_FORM: {
            this.formTitle = 'Project';
            this.in_master_detail = false;
            this.is_allowed_to_search = true;
            break;
          }
          case Constants.PROJECT_SEARCH: {
            this.formTitle = 'Projects Search';
            this.in_master_detail = false;
            this.is_allowed_to_search = true;
            break;
          }
        }
      }));

    this.subscriptions.add(

      this.cinematicService.newCollaboratorDisplayEmitted$.subscribe(data => {
        if (Constants.DEBUG) {
        }

        /*
        * To avoid the life cycle check error :
        * "Expression has changed after it was checked"
        */
        setTimeout(() => {
          switch (this.cinematicService.getFormerFormIdentifier()) {
            case Constants.TABS_STAFF_LIST:
              this.previousId = this.tabsStaffListService.previousCollaboratorId(data);
              this.nextId = this.tabsStaffListService.nextCollaboratorId(data);
              break;
            case Constants.PROJECT_TAB_STAFF:
              this.previousId = this.projectStaffService.previousIdStaff(data);
              this.nextId = this.projectStaffService.nextIdStaff(data);
              break;
          }
        });
        if (Constants.DEBUG) {
            console.groupCollapsed('Cinematic buttons');
            console.log('ID active in the form ' + data);
            console.log('ID for button "Previous" ' + this.previousId);
            console.log('ID for button "Next" ' + this.nextId);
            console.groupEnd();
        }
      }));
  }

  ngOnInit() {
    this.formTitle = 'Welcome';
    this.is_allowed_to_search = true;
    this.dev_activated = false;
    this.skill_activated = false;
    this.project_activated = false;

    /**
     * Loading the referentials.
     */
    this.referentialService.loadAllReferentials();
  }

  /**
	* Search button has been clicked.
	*/
  search(): void {
    switch (this.formId) {
      case Constants.TABS_STAFF_LIST:
        if (Constants.DEBUG) {
          console.log(
            'Searching ' + (this.activeOnly ? 'only active ' : 'all ')
            + 'staff members for the search criteria ' + this.searching_what + '');
        }
        if ((this.searching_what !== null) && (this.searching_what.length > 0)) {
          this.tabsStaffListService.addTabResult(this.searching_what, this.activeOnly);
        }
        break;
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
        this.skillService.filter(new ListCriteria(this.searching_what, this.activeOnly));
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

  goNewDeveloper(): void {
    if (Constants.DEBUG) {
      console.log('Creating a new developer');
    }
    this.searching_what = null;
    this.router.navigate(['/user'], {});
  }

  public switchToSkill() {
    this.searching_what = null;
    this.skill_activated = true;
    this.dev_activated = false;
    this.project_activated = false;
  }

  public switchToDev() {
    this.dev_activated = true;
    this.skill_activated = false;
    this.project_activated = false;

    this.searching_what = null;
    this.in_master_detail =   false;
    this.tabsStaffListService.inMasterDetail = false;
  }

  public switchToProject() {
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
    switch (this.formId) {
      case Constants.DEVELOPERS_CRUD:
        switch (this.cinematicService.getFormerFormIdentifier()) {
          case Constants.TABS_STAFF_LIST:
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

  /**
   * @returns TRUE if the container is in master/detail way.
   */
  public isInMasterDetail(): boolean {
    if (this.formId === Constants.TABS_STAFF_LIST) {
      return true;
    }
    return !this.in_master_detail;
  }

  /**
   * Unsubscription implemented in the BaseComponent
   */
  public ngOnDestroy() {
    super.ngOnDestroy();
  }

}
