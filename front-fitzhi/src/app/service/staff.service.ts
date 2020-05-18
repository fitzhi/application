import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Collaborator } from '../data/collaborator';
import { Project } from '../data/project';
import { StaffDTO } from '../data/external/staffDTO';

import { DeclaredExperience } from '../data/declared-experience';
import { Experience } from '../data/experience';
import { Observable, Subject } from 'rxjs';

import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take } from 'rxjs/operators';
import { BooleanDTO } from '../data/external/booleanDTO';
import { FileService } from './file.service';
import { traceOn } from '../global';
import { StaffDataExchangeService } from '../tabs-staff/service/staff-data-exchange.service';
import { MessageService } from '../message/message.service';

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
		private messageService: MessageService,
		private staffDataExchangeService: StaffDataExchangeService,
		private backendSetupService: BackendSetupService) {
	}

	/**
     * Return the global list of ALL collaborators, working for the company.
     */
	getAll(): Observable<Collaborator[]> {
		if (traceOn()) {
			console.log('Fetching the collaborators');
		}
		return this.http.get<Collaborator[]>(this.backendSetupService.url() + '/staff' + '/all');
	}

	/**
     * GET staff member associated to this id. Will throw a 404 if id not found.
     */
	get(id: number): Observable<Collaborator> {
		const url = this.backendSetupService.url() + '/staff' + '/' + id;
		if (traceOn()) {
			console.log('Fetching the collaborator ' + id + ' on the address ' + url);
		}
		return this.http.get<Collaborator>(url);
	}

	/**
     * POST: update or add a new collaborator to the server
	 * @param collaborator the given collaborator
     */
	save(collaborator: Collaborator): Observable<Collaborator> {
		if (traceOn()) {
			console.log('Saving the collaborator with id ' + collaborator.idStaff);
		}

		return this.http.post<Collaborator>(this.backendSetupService.url() + '/staff' + '/save', collaborator, httpOptions);
	}

	/**
     * Activate or inactivate a staff member.
	 *
	 * This (de-)activation is based on an end-user choise, in opposition to the method **processActiveStatus**
	 * which processes the state for the staf member, based on his Git activity.
	 * @param idStaff the staff identifier to (de)activate
     */
	switchActiveStatus(collaborator: Collaborator) {
		if (traceOn()) {
			console.log (
				'Switching the active status for the collaborator with id %d to %s',
				collaborator.idStaff,
				(collaborator.active) ? 'active' : 'inactive');
		}

		this.http.get<boolean>(
			this.backendSetupService.url() + '/staff/forceActiveStatus/' + collaborator.idStaff,
			httpOptions).
			pipe(take(1)).
			subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.messageService.success('Active state updated');
					} else {
						console.error('INTERNAL ERROR : Should not pass here!');
					}
				}
			});
	}

	/**
     * Activate or inactivate a staff member.
	 * @param idStaff the staff identifier to (de)activate
     */
	processActiveStatus(collaborator: Collaborator) {
		if (traceOn()) {
			console.log(
				'Processing the activity-state for the collaborator %s (id: %d)',
				collaborator.firstName + ' ' + collaborator.lastName, collaborator.idStaff);
		}

		this.http.get<Collaborator>(
			this.backendSetupService.url() + '/staff/processActiveStatus/' + collaborator.idStaff,
			httpOptions).
			pipe(take(1)).
			subscribe({
				next: staff => {
					if (traceOn()) {
						console.log ('%s is now %s', staff.lastName, staff.active ? 'active' : 'inactive');
					}
					this.staffDataExchangeService.collaborator.active = staff.active;
					this.staffDataExchangeService.collaborator.dateInactive = staff.dateInactive;
					this.staffDataExchangeService.collaboratorLoaded$.next(true);
				}
			});
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
		if (traceOn()) {
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
		if (traceOn()) {
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
		if (traceOn()) {
			console.log('Adding the skill  ' + skillTitle + ' for the staff member whom id is ' + idStaff);
		}
		const body = { idStaff: idStaff, newSkillTitle: skillTitle, level: level };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/experiences/save', body, httpOptions);
	}

	/**
     * POST: Add the relevant declared experiences (certainly retrieved from the resume)
     */
	addDeclaredExperience(idStaff: number, skills: DeclaredExperience[]): Observable<StaffDTO> {
		if (traceOn()) {
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
		if (traceOn()) {
			console.log('Revoking the experence ' + idSkill + ' from the collaborator application');
		}
		const body = { idStaff: idStaff, idSkill: idSkill };
		return this.http.post<StaffDTO>(this.backendSetupService.url() + '/staff' + '/experiences/del', body, httpOptions);
	}

	/**
     * POST: Revoke an experience from a staff member.
     */
	removeExperience(idStaff: number, idSkill: number): Observable<Boolean> {
		if (traceOn()) {
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
		if (traceOn()) {
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
		if (traceOn()) {
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
		if (traceOn()) {
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
		if (traceOn()) {
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
		if (traceOn()) {
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
						if (traceOn()) {
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
					if (traceOn()) {
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
