import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Constants } from '../constants';
import { query } from '@angular/core/src/render3/query';

@Component({
    selector: 'app-toolbar',
    templateUrl: './toolbar.component.html',
    styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

    /**
     * We'll send to the parent component (AppComponent) the selected form to be displayed
     */
    @Output() messengerFormActive = new EventEmitter<number>();

    DEVELOPERS_CRUD = Constants.DEVELOPERS_CRUD;
    SKILLS_CRUD = Constants.SKILLS_CRUD;
    PROJECT_TAB_FORM = Constants.PROJECT_TAB_FORM;

    /**
     * Type of entity currently active.
     */
    private editedEntity = Constants.DEVELOPERS_CRUD;


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
     * Previous identifier to be displayed if the user clicked on the PREV button
     */
    previousId: number;

    /**
     * Next identifier to be displayed if the user clicked on the NEXT button
     */
    nextId: number;

    constructor() { }

    ngOnInit() {
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
     * Master/Detail mode ON. The goBack() and goFoward() buttons are visible
     */
    isInMasterDetail() {
        return false;
    }

    /**
     * Inform the toolbar that the user has choosed an entity to be edited (Staff, Skill, Project)
     */
    mode (editedEntity: number) {
        this.editedEntity = editedEntity;
        this.messengerFormActive.emit(this.editedEntity);
        if (Constants.DEBUG) {
            console.log ('Actual mode', this.editedEntity);
        }
    }

    /**
     * Launch a query based on the criteria.
     */
    query() {
        if (Constants.DEBUG) {
            console.log ('Query on criteria', this.criteria);
        }
    }

    /**
     * Display the list behind the master/detail preview.
     */
    list() {
        console.log ('list');
    }

    /**
     * Pre-filter the data previewed on those who are still active, or take everything in account (with the complete history)
     */
    switchActiveOnly() {
        this.activeOnly = !this.activeOnly;
    }
}
