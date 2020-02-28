import { Injectable } from '@angular/core';
import { Skill } from '../data/skill';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject, of } from 'rxjs';
import { InternalService } from '../internal-service';

import { Constants } from '../constants';
import { ListCriteria } from '../data/listCriteria';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take, tap, map, switchMap } from 'rxjs/operators';
import { traceOn } from '../global';
import { DetectionTemplate } from '../data/detection-template';
import { FormGroup } from '@angular/forms';
import { isNumber } from 'util';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class SkillService extends InternalService {

	/*
	 * Are skills loaded or not ?
	 */
	private _allSkillsLoaded$  = new BehaviorSubject<boolean>(false);
	public allSkillsLoaded$  = this._allSkillsLoaded$.asObservable();

	/*
	 * list of skills filtered.
	 * This observable will be listen from the ListSkillComponent.
	 */
	public filteredSkills$  = new BehaviorSubject<Skill[]>([]);

	/*
	 * skills
	 */
	public allSkills: Skill[];

	/**
	 * skills
	 */
	public filteredSkills: Skill[] = [];

	/**
	 * Context of search
	 */
	criteria: ListCriteria;

	/**
	 * Array of `DetectionTemplate`.
	 */
	private detectionTemplates: DetectionTemplate[];

	public detectionTemplatesLoaded$ = new BehaviorSubject(false);

	constructor(private httpClient: HttpClient, private backendSetupService: BackendSetupService) {
		super();
		if (traceOn() && !this.backendSetupService.hasSavedAnUrl()) {
			console.log('Skills loading is postponed due to the lack of backend URL.');
		}
		if (this.backendSetupService.hasSavedAnUrl()) {
			this.loadSkills();
		}
	}

	/**
	 * load the list of ALL collaborators skills, working for the company.
	 */
	loadSkills() {

		if (traceOn()) {
			this.log('Fetching all skills on URL ' + this.backendSetupService.url() + '/skill' + '/all');
		}
		this.httpClient
			.get<Skill[]>(this.backendSetupService.url() + '/skill' + '/all')
			.pipe(
				tap(skills => {
					if (traceOn()) {
						console.groupCollapsed('Skills registered : ');
						skills.forEach(function (skill) {
							console.log(skill.id + ' ' + skill.title);
						});
						console.groupEnd();
					}
				}),
				take(1))
			.subscribe( {
				next: skills => this.setAllSkills(skills),
				error: error => console.log(error)
			});
	}

	/**
	 * Set the array containing all skills.
	 * @param skills the complete list of skills
	 */
	private setAllSkills(skills: Skill[]) {
		this.allSkills = skills;
		this._allSkillsLoaded$.next(true);
	}

	/**
	* Save the given skill and return the new skill.
	* @param skill the skill to be saved
	*/
	save$(skill: Skill): Observable<Skill> {
		if (traceOn()) {
			console.log((skill.id) ? 'Saving ' : 'Adding' + ' skill ' + skill.title);
		}
		return this.httpClient
			.post<Skill>(this.backendSetupService.url() + '/skill' + '/save', skill, httpOptions)
			.pipe(take(1));
	}

	/**
	 * @returns the title associated to the passed skill identifier
	 */
	title(idSkill: number) {
		const foundSkill = this.allSkills.find(skill => skill.id === idSkill);
		return (foundSkill) ? ('ERR : no title for id ' + idSkill) : foundSkill.title;
	}

	/**
	 * @returns the ID associated to the passed skill title.
	 */
	id(title: string): number {
		const found = this.allSkills.find(skill => skill.title === title);
		if (!found) {
			return -1;
		} else {
			return found.id;
		}
	}

	/**
	 * GET the skill associated to this id from the backend skiller. Will throw a 404 if this id is not found.
	 */
	get(id: number): Observable<Skill> {
		const url = this.backendSetupService.url() + '/skill' + '/' + id;
		if (traceOn()) {
			console.log('Fetching the skill ' + id + ' on the address ' + url);
		}
		return this.httpClient.get<Skill>(url);
	}

	/**
	 * GET the skill associated to the passed name, if any, from the back-end skiller.
	 * Will throw a 404 if this name is not retrieved.
	 */
	lookup(skillTitle: string): Observable<Skill> {
		const url = this.backendSetupService.url() + '/name/' + skillTitle;
		if (traceOn()) {
			console.log('Fetching the skill title ' + skillTitle + ' on the address ' + url);
		}
		return this.httpClient.get<Skill>(url);
	}

	/**
	 * Search accross the array "all skills" for a skill with the same name as the passed one.
	 * @param title the given title of the skill (as e.g. 'Java')
	 * @return either undefined if found none, or the first one AND THE ONLY ONE with the same title.
	 */
	search(title: String) {
		if (this.allSkills.length === 0) {
			console.error('the array containing all skills is empty');
			return undefined;
		}
		return this.allSkills.find (skill => skill.title === title);
	}

	/**
	 * Filter and emit a filtered list of skills corresponding to the current criteria.
	 * @param criteria the criteria filled by the user.
	 */
	filterSkills(criteria: ListCriteria) {

		if (traceOn()) {
			console.log ('Filtering the skills for the criteria', criteria);
		}
		this.criteria = criteria;

		const filteredSkills: Skill[] = [];
		this.allSkills.forEach (skill => {
			if (skill.title.indexOf(this.criteria.criteria) !== -1)  {
				filteredSkills.push(skill);
			}
		});
		if (traceOn()) {
			console.groupCollapsed('Emitting the skills');
			filteredSkills.forEach (skill => {
				console.log (skill.id, skill.title);
			});
			console.groupEnd();
		}
		this.filteredSkills$.next(filteredSkills);
	}

	detectionTemplates$(): Observable<DetectionTemplate[]> {
		if (this.detectionTemplates) {
			return of(this.detectionTemplates);
		}

		this.detectionTemplates = [];
		const url = this.backendSetupService.url() + '/skill/detection-templates';
		return this.httpClient.get<{ [key: string]: string; }>(url)
			.pipe(
				tap(response => {
					Object.keys(response).forEach(key => {
						this.detectionTemplates.push(new DetectionTemplate(Number(key), response[key]));
					});
				}),
				switchMap(rep => of(this.detectionTemplates)));
	}

	/**
	 * Fill the given skill with the data entered
	 * @param skill the fill to be updated with the data entered in the form
	 * @param formGroupSkill the SKILL formGroup
	 */
	fillSkill(skill: Skill, formGroupSkill: FormGroup) {
		skill.title = formGroupSkill.get('title').value;
		let detectionTemplate: DetectionTemplate;
		//
		// Either the user didn't choose a template of detection and the detectionType is empty
		// or the user choose a template, and the detection type is a numeric
		//
		if (!isNumber(formGroupSkill.get('detectionType').value)) {
			detectionTemplate = null;
		} else {
			detectionTemplate = new DetectionTemplate(
				formGroupSkill.get('detectionType').value,
				formGroupSkill.get('pattern').value
			);
		}
		skill.detectionTemplate = detectionTemplate;
	}

}
