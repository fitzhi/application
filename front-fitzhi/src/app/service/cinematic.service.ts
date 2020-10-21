import { Constants } from '../constants';
import { BehaviorSubject } from 'rxjs';
import { Subject } from 'rxjs/Subject';
import { AuditDetailsHistory } from './cinematic/audit-details-history';
import { AuditDetail } from '../data/audit-detail';
import { traceOn } from '../global';
import { Injectable } from '@angular/core';

class Form {

	/**
	 * Active form identifier.
	 */
	public formIdentifier = -1;

	/**
	 * url activated for this form.
	 */
	public url: string;

	/**
	 * Constructor
	 */
	constructor(formIdentifier: number, url: string) {
		this.formIdentifier = formIdentifier;
		this.url = url;
	}

	public trace() {
		if (traceOn()) {
			console.log('Form Identifier ' + Constants.CONTEXT[this.formIdentifier] + ' for url ' + this.url);
		}
	}
}

@Injectable()
export class CinematicService {

	/**
	 * Identifier of the select form on stage on the SPA.
	 */
	public currentActiveForm$ = new BehaviorSubject<Form>(new Form(Constants.WELCOME, 'Welcome'));

	/**
	  * Current collaborator's identifier previewed on the form.
	  */
	public emitActualCollaboratorDisplay = new Subject<number>();

	/**
	 * Observable associated with the current collaborator previewed.
	 */
	newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

	/**
	 * This `BehaviorSubject` broadcasts the selection of an audit thumbnail by the end-user.
	 */
	public auditTopicSelected$ = new BehaviorSubject<number>(-1);

	/**
	 * `idTopic` of the selected Topic thumbnail.
	 *
	 * This identifier is used to manager the display selection behavior of a topic thumbnail
	 * (such as its border thickness)
	 */
	public idTopicSelected = -1;

	/**
	 * This subject informs on the selected tab in the projects Tab Group container.
	 * Each-time the end-user clicks on a tab, this BehaviorSubject emits an identifier corresponding to the tab selected.
	 *
	 * This identifier can be :
	 *
	 *  - `PROJECT_IDX_TAB_FORM` for the project tab form
	 *  - `PROJECT_IDX_TAB_STAFF` for the project staff list
	 *  - `PROJECT_IDX_TAB_SUNBURST` for the Staff coverage graph
	 *  - `PROJECT_IDX_TAB_SONAR` for the Sonar dashboard
	 *  - `PROJECT_IDX_TAB_AUDIT` for the Audit dashboard
	 *
	 */
	public tabProjectActivated$ = new BehaviorSubject<number>(Constants.PROJECT_IDX_TAB_FORM);

	/**
	 * Index of the current selected tab in the Project forms set.
	 */
	public projectTabIndex: number;

	/**
	 * Previous form active
	 */
	public previousForm: Form = new Form(Constants.WELCOME, '/welcome');

	/**
	 * History of active detail panels (shown or hidden)
	 */
	public auditHistory: { [idTopic: number]: AuditDetailsHistory } = {};

	/**
	 * Returns `true` if the detail panel is visible, `false` otherwise.
	 * @param idTopic the Topic identifier
	 * @param auditDetail the type of panel detail
	 */
	public isPanelDetailSelected(idTopic: number, auditDetail: AuditDetail) {
		if (auditDetail === AuditDetail.Report) {
			return this.auditHistory[idTopic].reportVisible;
		}
		if (auditDetail === AuditDetail.Tasks) {
			return this.auditHistory[idTopic].tasksVisible;
		}
		throw new Error('WTF : Should not pass here. Enum is no more what it\'s used to be');
	}

	/**
	* Set the new form identifier.
	*
	* We memorize at the same time the previous active form.
	* We need to save that information, in order to detect master/detail sequences behind each list/form couples
	* The navigation toolbar on the top left corner, need to know if we reach that point
	*
	* * either from the natural URL
	* * or the jump link from the list
	*/
	setForm(formIdentifier: number, url: string) {

		/**
		 * We do not change the active form.
		 */
		if (formIdentifier === this.currentActiveForm$.getValue().formIdentifier) {
			return;
		}

		this.previousForm = this.currentActiveForm$.getValue();

		if (traceOn()) {
			this.previousForm.trace();
		}

		/**
		* Fire the event. Has to be at the end of the method.
		*/
		this.currentActiveForm$.next(new Form(formIdentifier, url));
	}

	/**
	 * @return The former Form identifier
	 */
	getFormerFormIdentifier() {
		return this.previousForm.formIdentifier;
	}

	/**
	 * Fire the event that the tab index has changed.
	 */
	setProjectTab(tab: number) {
		this.tabProjectActivated$.next(tab);
	}

}

