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
    criteria: string;

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
                        this.in_master_detail = false;
                        this.is_allowed_to_search = false;
                        break;
                    }
                    case Constants.SKILLS_CRUD: {
                        this.in_master_detail = false;
                        this.is_allowed_to_search = true;
                        break;
                    }
                    case Constants.SKILLS_SEARCH: {
                        this.in_master_detail = false;
                        this.is_allowed_to_search = true;
                        break;
                    }
                    case Constants.DEVELOPERS_CRUD: {
                        this.in_master_detail = this.tabsStaffListService.inMasterDetail;
                        this.is_allowed_to_search = true;
                        break;
                    }
                    case Constants.DEVELOPERS_SEARCH: {
                        this.in_master_detail = false;
                        this.is_allowed_to_search = true;
                        break;
                    }
                    case Constants.PROJECT_TAB_FORM: {
                        this.in_master_detail = false;
                        this.is_allowed_to_search = true;
                        break;
                    }
                    case Constants.PROJECT_SEARCH: {
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
        this.is_allowed_to_search = true;

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
            case Constants.DEVELOPERS_SEARCH:
                if (Constants.DEBUG) {
                    console.log(
                        'Searching ' + (this.activeOnly ? 'only active ' : 'all ')
                        + 'staff members for the search criteria ' + this.criteria + '');
                }
                if ((this.criteria !== null) && (this.criteria.length > 0)) {
                    this.tabsStaffListService.addTabResult(this.criteria, this.activeOnly);
                }
                break;
            case Constants.SKILLS_SEARCH: {
                if (Constants.DEBUG) {
                    console.log('Reloading skills for search criteria ' + this.criteria);
                }
                this.skillService.filter(new ListCriteria(this.criteria, this.activeOnly));
                this.staffService.countAll_groupBy_experience(this.activeOnly);
                break;
            }
            case Constants.PROJECT_SEARCH: {
                if (Constants.DEBUG) {
                    console.log('Reloading project for search criteria ' + this.criteria);
                }
                this.listProjectsService.reloadProjects(this.criteria);
                break;
            }
        }
    }

    goNewDeveloper(): void {
        if (Constants.DEBUG) {
            console.log('Creating a new developer');
        }
        this.criteria = null;
        this.router.navigate(['/user'], {});
    }

    public switchToDev() {
        this.in_master_detail = false;
        this.tabsStaffListService.inMasterDetail = false;
    }

    /**
     * User has entered into the search INPUT.
     */
    focusSearch() {
        switch (this.formId) {
            case Constants.SKILLS_SEARCH:
                this.router.navigate(['/searchSkill'], {});
                break;
            case Constants.DEVELOPERS_SEARCH:
                this.router.navigate(['/searchUser'], {});
                break;
            case Constants.PROJECT_SEARCH:
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
     * The end-user has switched the context to an another entity (staff/skill/project)
     */
    onChangeForm($event: number) {
        this.formId = $event;
        if (Constants.DEBUG) {
            console.log ('Changing to mode', Constants.CONTEXT[$event]);
        }
        if (this.isInSearchingMode()) {
            this.focusSearch();
        }
    }

    /**
     * @returns TRUE if the active mode in a searching mode
     */
    isInSearchingMode() {
        switch (this.formId) {
            case Constants.TABS_STAFF_LIST:
            case Constants.DEVELOPERS_SEARCH:
            case Constants.SKILLS_SEARCH:
            case Constants.PROJECT_SEARCH:
                return true;
            default:
                return false;
        }

    }

    /**
     * The end-user has switched the activeOnly data state to ON or OFF.
     */
    onChangeActiveOnly($event: boolean) {
        this.activeOnly = $event;
        if (Constants.DEBUG) {
            if (this.activeOnly) {
                console.log('Filter only active records');
            } else {
                console.log('Select all records');
            }
        }
    }

    /**
     * The end-user has requested a query based on the passed criteria.
     */
    onRequestQuery($event: string) {
        if (Constants.DEBUG) {
            console.log('Request of a query on criteria', $event);
        }
        this.criteria = $event;
        this.search();
    }

    /**
     * All subscriptions are closed in the BaseComponent
     */
    public ngOnDestroy() {
        super.ngOnDestroy();
    }

}
