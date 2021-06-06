import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Project } from '../../../data/project';
import { MessageService } from '../../../interaction/message/message.service';
import { ProjectService } from '../../../service/project/project.service';

@Injectable({
	providedIn: 'root'
})
export class ListProjectsService  {

	static LookupCriteria = class {

		/**
		 * This set contains the skill identifiers corresponding to the skill label typed by the end-user.
		 */
		public skillIds: Set<Number>;

		/**
		 * Public simple construction.
		 *
		 * @param skill Skill involved in the search.
		 * @param name Part of the name involved in the search.
		 * @param risk risk given given by the end-user; it might be __"staff"__, or __"sonar"__, or __"audit"__.
		 * @param riskStartLevel Staff risk evaluation from 0 to 10.
		 */
		constructor(
			public skill: string,
			public name: string,
			public risk: string = null,
			public riskLevel: number = -1,
			public riskStartLevel: number = -1,
			public riskEndLevel: number = -1) {}

		/**
		 * @returns **TRUE** if a skill has been given by the end-user.
		 */
		hasSkill(): boolean {
			return (this.skill != null);
		}

		/**
		 * @returns **TRUE** if a staff risk has been given by the end-user.
		 */
		hasStaffRisk(): boolean {
			return (this.risk === 'staff');
		}

		/**
		 * @returns **TRUE** if a audit risk has been given by the end-user.
		 */
		hasAuditRisk(): boolean {
			return (this.risk === 'audit');
		}

		/**
		 * @returns  **TRUE** if the criteria is empty.
		 */
		isEmpty(): boolean {
			return ((!this.skill) && (!this.name) && (!this.risk));
		}
	};

	public filteredProjects$ = new BehaviorSubject<Project[]>([]);

	/**
	 * Current active search.
	 * This object is updated after each  reloadProjects(...)
	*/
	currentSearch  = {
		done: false,
		criteria: null,
		activeOnly: false
	};

	constructor(
		private messageService: MessageService,
		private skillService: SkillService,
		private projectService: ProjectService) {}

	/**
	 * Parse the lookup criteria and return an instance of **LookupCriteria**
	 *
	 * LookupCriteria contains all necessaries information used to filter the projects
	 *
	 * @param criteria the criteria given by the end-user in a string forma
	 * @return the resulting LookupCriteria
	 */
	parse(criteria: string): typeof ListProjectsService.LookupCriteria.prototype {

		const lookup = new ListProjectsService.LookupCriteria(null, null);

		function parseRiskCriteria(risk: string) {
			const riskLength = risk.length + 1;
			if (criteria.indexOf(risk + ':') === 0) {
				// select the skill corresponding to the criteria
				const index = criteria.indexOf(';');
				const level = (index === -1) ? criteria.substring(riskLength).toLocaleLowerCase()
							: criteria.substring(riskLength, index).toLocaleLowerCase();
				// This is a scenario of staff:1-3. We are filtering projects with a risk level from 1 to 3.
				const pos = level.indexOf('-');
				if (pos !== -1) {
					const startLevel = level.substring(0, pos);
					const endLevel = level.substring(pos + 1);
					if (!isNaN(parseInt(startLevel, 10)) && !isNaN(parseInt(endLevel, 10)))  {
						lookup.risk = risk;
						lookup.riskStartLevel = parseInt(startLevel, 10);
						lookup.riskEndLevel = parseInt(endLevel, 10);
					}
				} else {
					if (!isNaN(parseInt(level, 10))) {
						lookup.risk = risk;
						lookup.riskLevel = parseInt(level, 10);
					}
				}
			}
		}

		// Empty criteria
		if ((!criteria) || (criteria.length === 0)) {
			return lookup;
		}

		if (criteria.indexOf('skill:') === 0) {
			// select the skill corresponding to the criteria
			const index = criteria.indexOf(';');
			lookup.skill = (index === -1) ? criteria.substring(6).toLocaleLowerCase() : criteria.substring(6, index).toLocaleLowerCase();
		}

		parseRiskCriteria('staff');
		parseRiskCriteria('audit');

		const headerCriteria = Math.max(criteria.indexOf('skill:'), criteria.indexOf('staff:'), criteria.indexOf('audit:'));

		const remain =
			(headerCriteria === 0) ?
				( (criteria.indexOf(';') === -1) ? '' : criteria.substring(criteria.indexOf(';') + 1) ) :
				criteria.substring(criteria.indexOf(';') + 1);
		lookup.name = (remain === '') ? null : remain.toLocaleLowerCase();

		if (traceOn()) {
			console.log ('Lookup criterias skill:"%s" name:"%s" risk:"%s" %d %d %d',
				lookup.skill, lookup.name,
				lookup.risk,
				lookup.riskLevel, lookup.riskStartLevel, lookup.riskEndLevel);
		}
		return lookup;
	}

	saveCurrentSearch(myCriteria: string, activeOnly: boolean): void {
		this.currentSearch.criteria = myCriteria;
		this.currentSearch.activeOnly = activeOnly;
		this.currentSearch.done = true;
	}


	/**
	* Filter the projects for the passed criteria.
	* @param myCriteria criteria typed by the end-user
	* @param activeOnly filtering, or not, on **active** projects.
	*/
	public search(myCriteria: string, activeOnly: boolean): void {

		// We save the current search of criterias.
		this.saveCurrentSearch(myCriteria, activeOnly);

		const elligibleProjects = (activeOnly) ?
			this.projectService.allProjects.filter(project => (project.active)) :
			this.projectService.allProjects;

		if (traceOn()) {
			console.log ('number of elligible projects %d', elligibleProjects.length);
		}

		// '*' is a wildcard for all projects.
		if (myCriteria === '*') {
			this.filteredProjects$.next(elligibleProjects);
			return;
		}

		const projects: Project[] = [];

		const lookup = this.parse(myCriteria);
		if (lookup.hasSkill()) {
			const filteredSkillIds: number[] = this.skillService.filter(lookup.skill).map(skill => skill.id);
			lookup.skillIds = new Set<number>(filteredSkillIds);
		}

		function testCriteria(project, index, array) {

			if (lookup.isEmpty()) {
				return true;
			}

			if (lookup.hasSkill()) {
				let found = false;
				Object.keys(project.skills).forEach(id => {
					if (lookup.skillIds.has(Number(id))) {
						found = true;
					}
				});
				return (found) ? ((!lookup.name) || (project.name.toLocaleLowerCase().indexOf(lookup.name) > -1)) : false;
			}

			if (lookup.hasStaffRisk()) {
				let found = false;
				if (lookup.riskLevel > 0) {
					if (project.staffEvaluation === lookup.riskLevel) {
						found = true;
					}
				} else {
					if ( (project.staffEvaluation >= lookup.riskStartLevel) && (project.staffEvaluation <= lookup.riskEndLevel)) {
						found = true;
					}
				}
				return (found) ? ((!lookup.name) || (project.name.toLocaleLowerCase().indexOf(lookup.name) > -1)) : false;
			}

			if (lookup.hasAuditRisk()) {
				let found = false;
				if (lookup.riskLevel >= 0) {
					if (project.auditEvaluation === lookup.riskLevel) {
						found = true;
					}
				} else {
					if ( (project.auditEvaluation >= lookup.riskStartLevel) && (project.auditEvaluation <= lookup.riskEndLevel)) {
						found = true;
					}
				}
				return (found) ? ((!lookup.name) || (project.name.toLocaleLowerCase().indexOf(lookup.name) > -1)) : false;
			}

			return (project.name.toLowerCase().indexOf(lookup.name) > -1);
		}
		projects.push(...elligibleProjects.filter(testCriteria.bind(this)));

		/**
		 * We throw the resulting collection.
		 */
		this.filteredProjects$.next(projects);
	}

	/**
	* Reload the data set with the former.
	*/
	public reload(): void {
		if (this.currentSearch.done) {
			this.search(this.currentSearch.criteria, this.currentSearch.activeOnly);
		}
	}

	/**
	 * Return the project associated with this id in cache first, anf if not found, direct on the server
	 * @param id the project identifier
	 */
	getProject$(id: number): Observable<Project> {

		if (!this.projectService.allProjects) {
			this.messageService.info('Please wait until all projects have been loaded!');
			return EMPTY;
		}
		let foundProject: Project = null;
		foundProject = this.projectService.allProjects.find(project => project.id === id);

		if (foundProject) {
			return of(foundProject);
		} else {
			// The project's id is not, or no more, available in the cache.
			// We try a direct access
			if (traceOn()) {
				console.log('Direct access for : ' + id);
			}
			return this.projectService.get$(id).pipe(tap(
				(project: Project) => {
					if (traceOn()) {
						console.log('Direct access for : ' + id);
						if (project) {
							console.log('Project found : ' + project.name);
						} else {
							console.log('No project found for id ' + id);
						}
					}
				}));
		}
	}

	/**
	 * Return a project, if any, for this name
	 */
	lookupProject(projectName: string): Observable<Project> {
		return this.projectService.lookup$(projectName);
	}


}
