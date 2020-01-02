import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Collaborator } from '../data/collaborator';
import { Project } from '../data/project';
import { StaffDTO } from '../data/external/staffDTO';

import { Constants } from '../constants';
import { DeclaredExperience } from '../data/declared-experience';
import { Experience } from '../data/experience';
import { Observable, Subject } from 'rxjs';

import { saveAs } from 'file-saver';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take } from 'rxjs/operators';
import { BooleanDTO } from '../data/external/booleanDTO';
import { FileService } from './file.service';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json', 'observe': 'response' })
};

@Injectable({
	providedIn: 'root'
})
export class StaffService {

	/**
     * Observable to a map containig the count of staff members aggregated by skill & level (i.e. experience)
     */
	public peopleCountExperience$ = new Subject<Map<string, number>>();

	constructor(
		private http: HttpClient,
		private fileService: FileService,
		private backendSetupService: BackendSetupService) {
	}

	/**
     * Return the global list of ALL collaborators, working for the company.
     */
	getAll(): Observable<Collaborator[]> {
		if (Constants.DEBUG) {
			console.log('Fetching the collaborators');
		}
		return this.http.get<Collaborator[]>(this.backendSetupService.url() + '/staff' + '/all');
	}

	/**
     * GET staff member associated to this id. Will throw a 404 if id not found.
     */
	get(id: number): Observable<Collaborator> {
		const url = this.backendSetupService.url() + '/staff' + '/' + id;
		if (Constants.DEBUG) {
			console.log('Fetching the collaborator ' + id + ' on the address ' + url);
		}
		return this.http.get<Collaborator>(url);
	}

	/**
     * POST: update or add a new collaborator to the server
     */
	save(collaborator: Collaborator): Observable<Collaborator> {
		if (Constants.DEBUG) {
			console.log('Saving the collaborator with id ' + collaborator.idStaff);
		}

		return this.http.post<Collaborator>(this.backendSetupService.url() + '/staff' + '/save', collaborator, httpOptions);
	}

	/**
     * DELETE delete a staff member from the server
     */
	delete(collaborater: Collaborator | number): Observable<Collaborator> {
		const id = typeof collaborater === 'number' ? collaborater : collaborater.idStaff;
		const url = `${this.backendSetupService.url() + '/staff'}/${id}`;

		return this.http.delete<Collaborator>(url, httpOptions);
	}

	/**
     * POST Verb :
	 * Add the contribution of a staff member into a project.
	 * @param idStaff the staff identifier.
	 * @param idProject the project identifier to add.
	 * @returns an observable emetting the staff record updated or an empty staff if any error occurs.
     */
	addProject(idStaff: number, idProject: number): Observable<BooleanDTO> {
		if (Constants.DEBUG) {
			console.log('Adding the collaborator with the id : ' + idStaff + ' into the project id ' + idProject);
		}
		const body = { idStaff: idStaff, idProject: idProject };
		return this.http.post<BooleanDTO>(this.backendSetupService.url() + '/staff' + '/project/add', body, httpOptions);
	}

	/**
     * POST Verb :
	 * Unregister the contribution of a staff member into a project.
	 * @param idStaff the staff identifier.
	 * @param idProject the project identifier to remove from the missions.
	 * @returns an observable emetting the staff record updated or an empty staff if any error occurs.
     */
	removeProject(idStaff: number, idProject: number): Observable<StaffDTO> {
		if (Constants.DEBUG) {
			console.log('Removing the collaborator with id : ' + idStaff + ' from project with id ' + idProject);
		}
		const body = { idStaff: idStaff, idProject: idProject };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/project/del', body, httpOptions);
	}

	/**
     * Load the projects associated with the staff member identified by this id.
     */
	loadProjects(idStaff: number): Observable<Project[]> {
		return this.http.get<Project[]>(this.backendSetupService.url() + '/staff' + '/projects/' + idStaff);
	}

	/**
    * Load the experience of the staff member identified by this id.
    */
	loadExperiences(idStaff: number): Observable<Experience[]> {
		return this.http.get<Experience[]>(this.backendSetupService.url() + '/staff' + '/experiences/' + idStaff);
	}

	/**
     * POST: Add an asset to a staff member defined by its name
     */
	addExperienceByItsTitle(idStaff: number, skillTitle: string, level: number): Observable<StaffDTO> {
		if (Constants.DEBUG) {
			console.log('Adding the skill  ' + skillTitle + ' for the staff member whom id is ' + idStaff);
		}
		const body = { idStaff: idStaff, newSkillTitle: skillTitle, level: level };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/experiences/save', body, httpOptions);
	}

	/**
     * POST: Add the relevant declared experiences (certainly retrieved from the resume)
     */
	addDeclaredExperience(idStaff: number, skills: DeclaredExperience[]): Observable<StaffDTO> {
		if (Constants.DEBUG) {
			console.log('Adding ' + skills.length + ' experiences to the staff Id  ' + idStaff);
		}
		const body = { idStaff: idStaff, skills: skills };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/api/experiences/resume/save',
			body, httpOptions);
	}
	/**
     * POST: Revoke an experience from a staff member.
     */
	revokeExperience(idStaff: number, idSkill: number): Observable<StaffDTO> {
		if (Constants.DEBUG) {
			console.log('Revoking the experence ' + idSkill + ' from the collaborator application');
		}
		const body = { idStaff: idStaff, idSkill: idSkill };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/experiences/del', body, httpOptions);
	}

	/**
     * POST: Revoke an experience from a staff member.
     */
	removeExperience(idStaff: number, idSkill: number): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.log('Revoking the experence ' + idSkill + ' from the collaborator ' + idStaff);
		}
		const body = { idStaff: idStaff, idSkill: idSkill };
		return this.http.post<Boolean>
			(this.backendSetupService.url() + '/staff' + '/experiences/remove', body, httpOptions)
			.pipe(take(1));
	}

	/**
     * POST Method: Add an experience to a staff member.
     */
	addExperience({ idStaff, idSkill, level }: { idStaff: number; idSkill: number; level: number; }): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.log('Adding the skill  ' + idSkill + ' to the staff member whom id is ' + idStaff);
		}
		const body = { idStaff: idStaff, idSkill: idSkill, level: level };
		return this.http.post<Boolean>(this.backendSetupService.url() + '/staff' + '/experiences/add', body, httpOptions)
			.pipe(take(1));
	}

	/**
     * POST Method: Add an experience to a staff member.
     */
	updateExperienceLevel({ idStaff, idSkill, level }: { idStaff: number; idSkill: number; level: number; }): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.log('Adding the skill  ' + idSkill + ' to the staff member whom id is ' + idStaff);
		}
		const body = { idStaff: idStaff, idSkill: idSkill, level: level };
		return this.http.post<Boolean>(this.backendSetupService.url() + '/staff' + '/experiences/update', body, httpOptions)
			.pipe(take(1));
	}

	/**
     * POST: Change the experience defined by its title, or its level for a developer.
     */
	changeExperience(idStaff: number, formerSkillTitle: string, newSkillTitle: string, level: number): Observable<StaffDTO> {
		if (Constants.DEBUG) {
			console.log('Change the skill for the collaborator with id : '
				+ idStaff + ' from ' + formerSkillTitle + ' to ' + newSkillTitle);
		}
		const body = { idStaff: idStaff, formerSkillTitle: formerSkillTitle, newSkillTitle: newSkillTitle, level: level };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/experiences/save', body, httpOptions);
	}

	/**
     * GET : Download the application file of a staff member.
	 * @param the given staff member whose application has to be retrieved
     */
	downloadApplication(staff: Collaborator) {
		if ((staff.application === null) || (staff.application.length === 0)) {
			return;
		}
		if (Constants.DEBUG) {
			console.log('Download the application file : '
				+ staff.application + ' for ' + staff.firstName + ' ' + staff.lastName);
		}

		this.fileService.downloadFile(
			staff.application,
			this.backendSetupService.url() + '/staff' + '/' + staff.idStaff + '/application');
	}

	/**
     * Retrieving the sum of staff members aggregated by skill & level (i.e. experience)
     * @param activeOnly : Only active employees count into the aggregation.
     */
	countAll_groupBy_experience(activeOnly: boolean) {
		if (Constants.DEBUG) {
			console.log('countAll_groupBy_experience loading aggegations count from the server');
		}
		this.http.get<any>(this.backendSetupService.url() + '/staff' + '/countGroupByExperiences'
			+ (activeOnly ? '/active' : '/all'))
			.subscribe(
				response => {
					const peopleCountExperience = new Map<string, number>();
					Object.entries(response)
						.forEach(entry => {
							let key: string;
							let value: string;
							key = entry[0] as string;
							value = entry[1] as string;
							peopleCountExperience.set(key, parseInt(value, 0));
						});
						if (Constants.DEBUG) {
							console.groupCollapsed('peopleCountExperience');
							peopleCountExperience.forEach((key, value) => {
								console.log (key, value);
							});
							console.groupEnd();
						}
						this.peopleCountExperience$.next(peopleCountExperience);
				},
				error => console.log(error),
				() => {
					if (Constants.DEBUG) {
						console.log('peopleCountExperience is completly loaded');
					}
				}
			);
	}

	/**
	 * Register a new user inside the application
	 * @param veryFirstConnection TRUE if the is the VERY FIRST USER to be created, and subsequently the FIRST ADMIN USER.
	 * @param username the given username
	 * @param password  the given password
	 */
	registerUser(veryFirstConnection: boolean, username: string, password: string): Observable<StaffDTO> {
		return this.http.get<StaffDTO>(
			this.backendSetupService.url() + '/admin/' +
			(veryFirstConnection ? 'veryFirstUser' : 'register'),
			{ params: { login: username, password: password } });
	}


}
