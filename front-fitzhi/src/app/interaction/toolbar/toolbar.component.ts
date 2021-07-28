import { Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { BaseDirective } from '../../base/base-directive.directive';
import { Constants } from '../../constants';
import { ProjectStaffService } from '../../tabs-project/project-staff-service/project-staff.service';
import { CinematicService } from '../../service/cinematic.service';
import { TabsStaffListService } from '../../tabs-staff-list/service/tabs-staff-list.service';
import { traceOn } from '../../global';

/**
 * The toolbar for the AppComponent.
 */
@Component({
	selector: 'app-toolbar',
	templateUrl: './toolbar.component.html',
	styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent extends BaseDirective implements OnInit, OnDestroy {

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
	SKILLS_SEARCH = Constants.SKILLS_SEARCH;
	SKILLS_CRUD = Constants.SKILLS_CRUD;
	PROJECT_TAB_FORM = Constants.PROJECT_TAB_FORM;
	PROJECT_TAB_STAFF = Constants.PROJECT_TAB_STAFF;
	PROJECT_SEARCH = Constants.PROJECT_SEARCH;
	TABS_STAFF_LIST = Constants.TABS_STAFF_LIST;

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
	 * Previous identifier to be displayed if the user clicked on the PREV button
	 */
	previousId: number;

	/**
	 * Next identifier to be displayed if the user clicked on the NEXT button
	 */
	nextId: number;

	constructor(
		public cinematicService: CinematicService,
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
			this.cinematicService.currentActiveForm$.subscribe({
				next: context => {
					if (traceOn()) {
						console.log('Active context : ', Constants.CONTEXT[context.formIdentifier]);
					}
					this.mode(context.formIdentifier);
				}
			})
		);
	}

	/**
	 * We listen each change of collaborator displayed,
	 * to handle properly the state of the PREV and NEXT buttons.
	 */
	listenOnCollaboratorDisplayed() {
		this.subscriptions.add(
			this.cinematicService.currentCollaborator$.subscribe(id => {
				if (traceOn()) {
					console.log ('Former form identifier (Where do we come from?)',
					this.cinematicService.getFormerFormIdentifier());
				}
				switch (this.cinematicService.getFormerFormIdentifier()) {
					case Constants.DEVELOPERS_CRUD:
					case Constants.TABS_STAFF_LIST:
						this.previousId = this.tabsStaffListService.previousCollaboratorId(id);
						this.nextId = this.tabsStaffListService.nextCollaboratorId(id);
						break;
					case Constants.PROJECT_TAB_STAFF:
						this.previousId = this.projectStaffService.previousIdStaff(id);
						this.nextId = this.projectStaffService.nextIdStaff(id);
						break;
				}
				if (traceOn()) {
					console.groupCollapsed('Cinematic buttons');
					console.log('ID active in the form ' + id);
					console.log('ID for button "Previous" ' + this.previousId);
					console.log('ID for button "Next" ' + this.nextId);
					console.groupEnd();
				}
			}));
	}

	/**
	 * @return true if the WELCOME button has been selected by the end-user.
	 */
	isDashboardActive() {
		return (this.editedEntity === Constants.WELCOME);
	}

	/**
	 * @return true if the STAFF button has been selected by the end-user.
	 */
	isStaffActive() {
		return ((this.editedEntity === this.DEVELOPERS_CRUD) || (this.editedEntity === Constants.TABS_STAFF_LIST));
	}

	/**
	 * @return true if the SKILL button has been selected by the end-user.
	 */
	isSkillActive() {
		return ((this.editedEntity === this.SKILLS_CRUD) || (this.editedEntity === Constants.SKILLS_SEARCH));
	}

	/**
	 * @return true if the PROJECT button has been selected by the end-user.
	 */
	isProjectActive() {
		return (
					(this.editedEntity === this.PROJECT_TAB_FORM)
				|| 	(this.editedEntity === Constants.PROJECT_TAB_STAFF)
				|| 	(this.editedEntity === Constants.PROJECT_SEARCH));
	}

	/**
	 * @return true if there is no entity selected by the end-user.
	 * Presumably, we just enter in the application.
	 */
	nothingActive() {
		return (this.editedEntity === 0);
	}

	/**
	 * Inform the toolbar that the user has choosed an entity to be edited (Staff, Skill, Project)
	 */
	mode(editedEntity: number) {

		if (this.editedEntity !== editedEntity) {

			this.editedEntity = editedEntity;
			this.criteria = null;

			// We have clicked on an 'Entity' button. We disabled the master detail behavior for the staff.
			this.cinematicService.masterDetailSubject$.next(false);
			this.messengerFormActive.emit(this.editedEntity);

			if (traceOn()) {
				console.log('Actual mode', Constants.CONTEXT[this.editedEntity]);
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
			case this.PROJECT_TAB_STAFF:
				this.messengerFormActive.emit(Constants.PROJECT_SEARCH);
				break;
			case this.SKILLS_CRUD:
				this.messengerFormActive.emit(Constants.SKILLS_SEARCH);
				if (traceOn()) {
					console.log ('Complete search as default search');
				}
				setTimeout(() => {
					this.criteria = '';
					this.messengerCriteria.emit(this.criteria);
				}, 0);
				break;
			case this.SKILLS_SEARCH:
			case this.PROJECT_SEARCH:
			case this.TABS_STAFF_LIST:
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
		if (this.criteria) {
			this.messengerCriteria.emit(this.criteria);
		}
	}

	/**
	 * Display the list behind the master/detail preview.
	 */
	list() {
		this.messengerFormActive.emit(Constants.BACK_TO_LIST);
	}

	/**
	 * Pre-filter the data previewed on those who are still active, or take everything in account (with the complete history)
	 */
	switchActiveOnly() {
		this.activeOnly = !this.activeOnly;
		this.messengerActiveOnly.emit(this.activeOnly);
	}

	/**
	 * The user has clicked on the "Staff" member
	 */
	switchToStaff() {
		this.cinematicService.masterDetailSubject$.next(false);
		this.mode(Constants.DEVELOPERS_CRUD);
	}

	/**
	 * End-user returns back to the main introducing dashboard.
	 */
	switchToDashboard() {
		this.cinematicService.masterDetailSubject$.next(false);
		this.mode(Constants.WELCOME);
	}

	/**
	 * All subscriptions are closed in the BaseComponent
	 */
	public ngOnDestroy() {
		super.ngOnDestroy();
	}

}
