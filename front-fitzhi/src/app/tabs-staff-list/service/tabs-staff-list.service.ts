import { Injectable } from '@angular/core';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, finalize, switchMap, take } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Collaborator } from '../../data/collaborator';
import { ListCriteria } from '../../data/listCriteria';
import { StaffListContext } from '../../data/staff-list-context';
import { MessageService } from '../../interaction/message/message.service';
import { StaffService } from '../../tabs-staff/service/staff.service';

@Injectable({
	providedIn: 'root'
})
export class TabsStaffListService {

	/**
     * List of criterias dispplay on the tab staff lists dashboard.
     */
	public staffListContexts: Map<string, StaffListContext> = new Map<string, StaffListContext>();

	/**
     * Index of the active tab.
     */
	public activeTab = 0;

	/**
     * Key of the active tab.
     */
	public activeKey: string;

	/**
     * New search is made by the developer.
     */
	public criterias$ = new Subject<ListCriteria>();

	constructor(
		private staffService: StaffService,
		// SkillService is declared here to pass some tests. Some déclaration are mad in the SkillService construction
		private skillService: SkillService,
		private messageService: MessageService) {

		this.criterias$.subscribe(criterias => {
			if (traceOn()) {
				console.log('Adding criterias ' + criterias.criteria);
			}

			if (!this.staffListContexts.has(this.key(criterias))) {
				this.staffListContexts.set(
					this.key(criterias),
					new StaffListContext(criterias)
				);
			}
		});
	}

	/**
	 * Construct the key of the criterias.
	 */
	key(criterias: ListCriteria): string {
		return criterias.criteria + '-' + (criterias.activeOnly ? '1' : '0');
	}

	/**
     * Add a tab of results in the tab containter.
     */
	public addTabResult(criteria: string, activeOnly: boolean) {

		const myCriteria = new ListCriteria(criteria, activeOnly);
		if (this.staffListContexts.has(this.key(myCriteria))) {
			this.messageService.info('This criteria is already present!');
			return;
		}
		this.criterias$.next(myCriteria);
	}

	/**
     * Searching staff members corresponding to the 2 passed criterias.
     * @param criteria the criteria
     * @param activeOnly active only **true** / **false**
	 * @returns an observable emetting an array of collaborators
     */
	public search$(criteria: string, activeOnly: boolean): Observable<Collaborator[]> {

		const collaborator = [];

		/**
         * Cache of the skills filter.
         */
		const skillsFilter: Filter[] = [];

		/**
         * List of criterias unretrieved in the skills collection
         */
		const criteriasUnknown: string[] = [];

		const ALL_LEVELS = 0;

		/**
		 * Key to store the criteria which will be linked to a list of results.
		 */
		const key = this.key(new ListCriteria(criteria, activeOnly));

		/**
         * The reminder has already, or not, be extracted from the criterias string
         */
		let reminderIsAlreadyKnown = false;

		/**
         * Reminder parsed and saved in this property.
         */
		let reminderExtracted: string;

		// We bind the internal function _getSkillsFilter with the main class
		const getSkillsFilter = _getSkillsFilter.bind(this);

		class Filter {
			id: number;
			level: number;
			constructor(id: number, level: number) {
				this.id = id;
				this.level = level;
			}
		}

		//
		// If this search has already been executed, we load it from the cache
		//
		if (this.staffListContexts.has(key)) {
			const context = this.staffListContexts.get(key);
			if (context.staffSelected.length > 0) {
				if (traceOn()) {
					console.log('Using cache for key ' + key + ' ' + context.staffSelected.length + ' records');
				}
				collaborator.push(...context.staffSelected);
				return of(collaborator);
			}
		}

		function extractCriteriaSkills(): string {
			const pos_start_skills = criteria.toLowerCase().indexOf('skill:');
			if (pos_start_skills > -1) {
				const pos_end_skills = criteria.toLowerCase().indexOf(';', pos_start_skills);
				// Assuming that until the end of string, we have skills
				if (pos_end_skills === -1) {
					return criteria.toLowerCase().substring(pos_start_skills + 'skill:'.length);
				} else {
					return criteria.toLowerCase().substring(pos_start_skills + 'skill:'.length, pos_end_skills);
				}
			}
			return '';
		}

		/**
		 * Extract the filter for skills the the criterias string and return the reminder, which might be empty
         * @returns the remonder of criterias
         */
		function reminderCriteria(): string {

			if (reminderIsAlreadyKnown) {
				return reminderExtracted;
			}
			const pos_start_skills = criteria.toLowerCase().indexOf('skill:');
			if (pos_start_skills === -1) { return criteria; }
			const pos_end_skills = criteria.indexOf(';', pos_start_skills);
			reminderExtracted = (pos_end_skills === -1) ?
				criteria.substr(0, pos_start_skills) :
				criteria.substr(0, pos_start_skills - 1) + criteria.substr(pos_end_skills + 1);
			if (traceOn()) {
				console.log('reminderCriteria(' + criteria + ') = ' + reminderExtracted);
			}
			reminderIsAlreadyKnown = true;
			return reminderExtracted;
		}

		/**
         * Parse the criterias and returns an array of skills filters.
         * @returns the skills filters
         */
		function _getSkillsFilter(): Filter[] {

			// We cache the array of skills id. We don't need to parse the criteria for each entry.
			if ((skillsFilter.length > 0) || (criteriasUnknown.length > 0)) {
				return skillsFilter;
			}

			const criteriaSkills = extractCriteriaSkills();
			if (criteriaSkills.length > 0) {
				const skills = criteriaSkills.split(',');
				if (traceOn()) {
					console.groupCollapsed('Skills candidate ');
					skills.forEach(skill => console.log(skill));
					console.groupEnd();
				}
				const allSkills = this.skillService.allSkills;
				skills.forEach(skill => {
					let found = false;
					allSkills.forEach(sk => {
						const posLevel = skill.indexOf(':');
						let skillTitle = '';
						if (posLevel === -1) {
							skillTitle = sk.title.toLowerCase();
							if (skill.toLowerCase() === skillTitle) {
								skillsFilter.push(new Filter(sk.id, ALL_LEVELS));
								found = true;
							}
						} else {
							skillTitle = skill.toLowerCase().substring(0, posLevel);
							if (sk.title.toLowerCase() === skillTitle) {
								const levelStr = skill.substring(posLevel + 1);
								if (!isNaN(Number(levelStr))) {
									const levelNum = parseInt(levelStr, 10);
									if ((levelNum >= 1) && (levelNum <= 5)) {
										skillsFilter.push(new Filter(sk.id, levelNum));
										found = true;
									}
								}
							}
						}
					});
					if (!found) {
						criteriasUnknown.push(skill);
					}
				});
				if (traceOn()) {
					console.groupCollapsed('id of skills candidate');
					console.table(skillsFilter);
					console.groupEnd();
					if (criteriasUnknown.length > 0) {
						console.groupCollapsed('Unknown skills');
						console.table(criteriasUnknown);
						console.groupEnd();
					}
				}

				if (criteriasUnknown.length > 0) {
					if (criteriasUnknown.length === 1) {
						this.messageService.warning('The skill ' + criteriasUnknown[0]
							+ ' is unknown. It will be ignored.');
					} else {
						this.messageService.warning('The skills ' + criteriasUnknown.join(', ')
							+ ' are unknown. They will be ignored.');
					}
				}
			}
			return skillsFilter;
		}

		function testCriteria(collab: Collaborator): boolean {

			const filters = getSkillsFilter();

			if (!filters.every(filter => collab.experiences.some(
				exp => ((filter.id === exp.id) && ((filter.level === exp.level) || (filter.level === ALL_LEVELS)))))) {
				// If we do not find each skill in the selected array of skills, we reject this collaborator.
				return false;
			}

			const reminder = reminderCriteria().trim().toLowerCase();
			if (reminder.length === 0) {
				if ((filters.length === 0) && (criteriasUnknown.length > 0)) {
					return false;
				} else {
					return (activeOnly ? collab.active : true);
				}
			}

			const lastname = (collab.lastName) ? collab.lastName : '';
			const firstname = (collab.firstName) ? collab.firstName : '';
			return (
				((firstname.toLowerCase().indexOf(reminder) > -1)
					|| (lastname.toLowerCase().indexOf(reminder) > -1))
				&& (activeOnly ? collab.active : true)
			);
		}

		return this.staffService.getAll$().pipe(
			take(1),
			switchMap((staffs: Collaborator[]) => {
					collaborator.push(...staffs.filter(staff => testCriteria(staff)));
					// We store the array of collaborators in the cache
					if (this.staffListContexts.has(key)) {
						if (traceOn()) {
							console.log('Saving collaborators for key ' + key);
						}
						this.staffListContexts.get(key).store(collaborator);
					}
					return of(collaborator);
				}),
			catchError(error => {
				this.messageService.error(error);
				return EMPTY;
			}),
			finalize(() => {
				if (traceOn()) {
					console.log(`The staff collection is containing now ${collaborator.length} records`);
					console.groupCollapsed('Staff members found : ');
					collaborator.forEach(collab => console.log(collab.firstName + ' ' + collab.lastName));
					console.groupEnd();
				}
			})
		);
	}

	/**
     * Remove from history the search entry corresponding to this key.
	 * @param key the key of the history to be removed
     */
	public removeHistory(key: string) {
		this.staffListContexts.delete(key);
		if (traceOn()) {
			console.groupCollapsed('Remaining tabs');
			this.staffListContexts.forEach(criterias => console.log(criterias.criteria));
			console.groupEnd();
		}
	}

	/**
     * @param current identifier
     * @returns the NEXT collaborator's id associated with this id in the staff list.
     */
	nextCollaboratorId(id: number): number {

		const context = this.staffListContexts.get(this.activeKey);

		// No context defined. Most probably, the end-user type directly a staff identifier in the search zone
		if (!context) {
			return undefined;
		}

		const index = context.staffSelected.findIndex(collab => collab.idStaff === id);
		if (traceOn()) {
			console.log('Current index : ' + index + ' for ' + this.activeKey);
			console.log('List size : ' + context.staffSelected.length);
		}
		if (index < context.staffSelected.length - 1) {
			return context.staffSelected[index + 1].idStaff;
		} else {
			return undefined;
		}
	}

	/**
     * @param current identifier
     * @returns the PREVIOUS collaborator's id associated with this id in the staff list.
     */
	previousCollaboratorId(id: number): number {

		const context = this.staffListContexts.get(this.activeKey);

		// No context defined. Most probably, the end-user type directly a staff identifier in the search zone
		if (!context) {
			return undefined;
		}

		const index = context.staffSelected.findIndex(collab => collab.idStaff === id);
		if (index > 0) {
			return context.staffSelected[index - 1].idStaff;
		} else {
			return undefined;
		}
	}

	/**
     * Actualize the information of a collaborator.
     * This method is call (for instance) after any update on the staff form.
	 *
	 * @param collaborator the collaborator to be actualized
     */
	public actualizeCollaborator(collaborator: Collaborator) {

		this.staffListContexts.forEach((staffListContext: StaffListContext, tabKey: string) => {
			staffListContext.staffSelected.forEach(staff => {
				if (staff.idStaff === collaborator.idStaff) {
					if (traceOn()) {
						console.log('Actualizing the collaborator with '
							+ collaborator.idStaff + ' inside the list for tab ', tabKey);
					}
					staff.firstName = collaborator.firstName;
					staff.lastName = collaborator.lastName;
					staff.level = collaborator.level;
					staff.active = collaborator.active;
					staff.external = collaborator.external;

					staff.experiences.length = 0;
					staff.experiences.push(...collaborator.experiences);
				}
			});
		});

	}


	/**
     * Retrieve the context associated to the key.
	 *
     * @param key searched key
     * @returns the context associated to this list or null if none exists (which is suspected to be an internal error)
     */
	getContext(key: string): StaffListContext {
		if (!this.staffListContexts.has(key)) {
			console.error('Cannot retrieve key ' + key);
			return null;
		} else {
			return this.staffListContexts.get(key);
		}
	}
}
