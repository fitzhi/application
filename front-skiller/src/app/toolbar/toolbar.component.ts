import { Component, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { Constants } from '../constants';
import { BaseComponent } from '../base/base.component';
import { CinematicService } from '../service/cinematic.service';
import { TabsStaffListService } from '../tabs-staff-list/service/tabs-staff-list.service';
import { ProjectStaffService } from '../project/project-staff-service/project-staff.service';

@Component({
    selector: 'app-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent extends BaseComponent implements OnInit, OnDestroy {

    /**
     * We'll send to the parent component (AppComponent) the selected form to be displayed
     */
    @Output() messengerFormActive = new EventEmitter<number>();

    /**
     * This messenger is used to inform the parent component the search criteria.
     */
    @Output() messengerCriteria = new EventEmitter<string>();

    /**
     * This messenger is used to inform the parent component
     * that the user prefers to filter on the active data, or not (including therefore the whole history).
     */
    @Output() messengerActiveOnly = new EventEmitter<boolean>();

    DEVELOPERS_CRUD = Constants.DEVELOPERS_CRUD;
    SKILLS_CRUD = Constants.SKILLS_CRUD;
    PROJECT_TAB_FORM = Constants.PROJECT_TAB_FORM;

    /**
     * Type of entity currently active.
     */
    private editedEntity = 0;

    /**
     * Should we filter the data on active records only ?
     * By default, we se to TRUE this criteria.
     */
    activeOnly = true;

    /**
     * Requested criteria entered on the search field.
     */
    criteria: string;

    /**
    * Master/Detail mode ON. The goBack() and goFoward() buttons are visible
    */
    masterDetail: boolean;

    /**
     * Previous identifier to be displayed if the user clicked on the PREV button
     */
    previousId: number;

    /**
     * Next identifier to be displayed if the user clicked on the NEXT button
     */
    nextId: number;

    constructor(
        private cinematicService: CinematicService,
        private projectStaffService: ProjectStaffService,
        private tabsStaffListService: TabsStaffListService) { super(); }


    ngOnInit() {

        this.listenOnContext();

        this.listenOnCollaboratorDisplayed();
   }

    /**
     * We listen each change of context in the application,
     * to properly handle the state of the master/detail boolean.
     */
    listenOnContext() {
        this.subscriptions.add(
            this.cinematicService.currentActiveForm.subscribe(context => {
                if (Constants.DEBUG) {
                    console.log ('Active context', Constants.CONTEXT[context.formIdentifier]);
                }
                if (context.formIdentifier === Constants.DEVELOPERS_CRUD) {
                    this.masterDetail = this.tabsStaffListService.inMasterDetail;
                } else {
                    this.masterDetail = false;
                }
            }));
   }

    /**
     * We listen each change of collaborator displayed,
     * to handle properly the state of the PREV and NEXT buttons.
     */
    listenOnCollaboratorDisplayed() {
        this.subscriptions.add(
            this.cinematicService.newCollaboratorDisplayEmitted$.subscribe(id => {
                switch (this.cinematicService.getFormerFormIdentifier()) {
                    case Constants.TABS_STAFF_LIST:
                        this.previousId = this.tabsStaffListService.previousCollaboratorId(id);
                        this.nextId = this.tabsStaffListService.nextCollaboratorId(id);
                        break;
                    case Constants.PROJECT_TAB_STAFF:
                        this.previousId = this.projectStaffService.previousIdStaff(id);
                        this.nextId = this.projectStaffService.nextIdStaff(id);
                        break;
                }
                if (Constants.DEBUG) {
                    console.groupCollapsed('Cinematic buttons');
                    console.log('ID active in the form ' + id);
                    console.log('ID for button "Previous" ' + this.previousId);
                    console.log('ID for button "Next" ' + this.nextId);
                    console.groupEnd();
                }
            }));
    }


    /**
     * @return true if the STAFF button has been selected by the end-user.
     */
    isStaffActive () {
        return (this.editedEntity === this.DEVELOPERS_CRUD);
    }

    /**
     * @return true if the SKILL button has been selected by the end-user.
     */
    isSkillActive () {
        return (this.editedEntity === this.SKILLS_CRUD);
    }

    /**
     * @return true if the PROJECT button has been selected by the end-user.
     */
    isProjectActive () {
        return (this.editedEntity === this.PROJECT_TAB_FORM);
    }

    /**
     * @return true if there is no entity selected by the end-user.
     * Presumably, we just enter in the application.
     */
    nothingActive() {
        return (this.editedEntity === 0);
    }
    /**
     * Master/Detail mode ON. The goBack() and goFoward() buttons are visible
     */
    isInMasterDetail() {
        return this.masterDetail;
    }

    /**
     * Inform the toolbar that the user has choosed an entity to be edited (Staff, Skill, Project)
     */
    mode (editedEntity: number) {

        /**
         * Specific case : We are proceeding in master/detail mode and the user end-click on the "Staff" Button.
         * We continue with the same entity, but the leave the master/detail mode.
         */
        if (editedEntity === Constants.DEVELOPERS_CRUD) {
            // We have clicked on an 'Entity' button. We disabled the master detail behavior for the staff.
            this.masterDetail = false;
            this.criteria = null;
        }

        if (this.editedEntity !== editedEntity) {
            this.editedEntity = editedEntity;
            this.criteria = null;

            // We have clicked on an 'Entity' button. We disabled the master detail behavior for the staff.
            this.tabsStaffListService.inMasterDetail = false;

            this.messengerFormActive.emit(this.editedEntity);
            if (Constants.DEBUG) {
                console.log ('Actual mode', Constants.CONTEXT[this.editedEntity]);
            }
        }
    }

    /**
     * The user has entered inside the criteria field in order to proceed a search-request.
     */
    searching() {
        switch (this.editedEntity) {
            case this.DEVELOPERS_CRUD:
                this.messengerFormActive.emit(Constants.DEVELOPERS_SEARCH);
                break;
            case this.PROJECT_TAB_FORM:
                this.messengerFormActive.emit(Constants.PROJECT_SEARCH);
                break;
            case this.SKILLS_CRUD:
                this.messengerFormActive.emit(Constants.SKILLS_SEARCH);
                break;
            default:
                console.error('Unattempted editedEntity', Constants.CONTEXT[this.editedEntity]);
                break;
        }
    }

    /**
     * Launch a query based on the requested criteria, if, at least, one caracter is present on the search field.
     */
    query() {
        if ((typeof this.criteria !== 'undefined') && (this.criteria !== null)) {
            this.messengerCriteria.emit(this.criteria);
        }
    }

    /**
     * Display the list behind the master/detail preview.
     */
    list() {
        this.messengerFormActive.emit(Constants.DEVELOPERS_SEARCH);
    }

    /**
     * Pre-filter the data previewed on those who are still active, or take everything in account (with the complete history)
     */
    switchActiveOnly() {
        this.activeOnly = !this.activeOnly;
        this.messengerActiveOnly.emit(this.activeOnly);
    }

    /**
     * All subscriptions are closed in the BaseComponent
     */
    public ngOnDestroy() {
        super.ngOnDestroy();
    }
}
