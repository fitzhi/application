import { Injectable } from '@angular/core';
import { Project } from '../data/project';
import { ProjectDTO } from '../data/external/projectDTO';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs';
import { InternalService } from '../internal-service';

import { Constants } from '../constants';
import { Skill } from '../data/skill';
import { ContributorsDTO } from '../data/external/contributorsDTO';
import { PseudoList } from '../data/PseudoList';
import { SettingsGeneration } from '../data/settingsGeneration';
import { BackendSetupService } from './backend-setup/backend-setup.service';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class ProjectService extends InternalService {


	constructor(
		private httpClient: HttpClient,
		private backendSetupService: BackendSetupService) {
		super();
	}

	/**
   * Return the global list of ALL collaborators, working for the company.
   */
	getAll(): Observable<Project[]> {
		if (Constants.DEBUG) {
			this.log('Fetching the projects on URL ' + this.backendSetupService.url() + '/project/all');
		}
		return this.httpClient.get<Project[]>(this.backendSetupService.url() + '/project/all');
	}

	/**
	* Save the project.
	*/
	save(project: Project): Observable<Project> {
		if (Constants.DEBUG) {
			console.log(((typeof project.id !== 'undefined') ? 'Saving ' : 'Adding ') + 'project ' + project.name);
		}
		return this.httpClient.post<Project>(this.backendSetupService.url() + '/project/save', project, httpOptions);
	}

	/**
	* Add a skill to a project.
	*/
	addSkill(idProject: number, skillTitle: string): Observable<ProjectDTO> {
		if (Constants.DEBUG) {
			console.log('Adding the skill  ' + skillTitle + ' for the project whom id is ' + idProject);
		}
		const body = { idProject: idProject, newSkillTitle: skillTitle };
		return this.httpClient.post<ProjectDTO>(this.backendSetupService.url() + '/project/skills/save', body, httpOptions);
	}

	/**
	* Change the skill inside a project.
	*/
	changeSkill(idProject: number, formerSkillTitle: string, newSkillTitle: string): Observable<ProjectDTO> {
		if (Constants.DEBUG) {
			console.log('Changing the skill  ' + formerSkillTitle + ' to ' + newSkillTitle + ' for the project whom id is ' + idProject);
		}
		const body = { idProject: idProject, formerSkillTitle: formerSkillTitle, newSkillTitle: newSkillTitle };
		return this.httpClient.post<ProjectDTO>(this.backendSetupService.url() + '/project/skills/save', body, httpOptions);
	}

	/**
	 * POST: Remove a skill from project skills list.
	 */
	removeSkill(idProject: number, idSkill: number): Observable<ProjectDTO> {
		if (Constants.DEBUG) {
			console.log('Remove a the skill with ID ' + idSkill + ' from the project with ID ' + idProject);
		}
		const body = { idProject: idProject, idSkill: idSkill };
		return this.httpClient.post<ProjectDTO>(this.backendSetupService.url() + '/project/skills/del', body, httpOptions);
	}

	/**
	 * Load the projects associated with the staff member identified by this id.
	 */
	loadSkills(idProject: number): Observable<Skill[]> {
		return this.httpClient.get<Skill[]>(this.backendSetupService.url() + '/project/skills/' + idProject);
	}

	/**
	 * GET the project associated to this id from the back-end od skiller. Will throw a 404 if this id is not found.
	 */
	get(id: number): Observable<Project> {
		const url = this.backendSetupService.url() + '/project/id/' + id;
		if (Constants.DEBUG) {
			console.log('Fetching the project ' + id + ' on the address ' + url);
		}
		return this.httpClient.get<Project>(url);
	}

	/**
	 * GET the project associated to the passed name, if any, from the back-end skiller.
	 * Will throw a 404 if this name is not retrieved.
	 */
	lookup(projectName: string): Observable<Project> {
		const url = this.backendSetupService.url() + '/project/name/' + projectName;
		if (Constants.DEBUG) {
			console.log('Fetching the project name ' + projectName + ' on the address ' + url);
		}
		return this.httpClient.get<Project>(url);
	}

	/**
	 * Retrieve the Sunburst data, figuring the activity on the source code regarding the passed project.
	 */
	loadDashboardData(settings: SettingsGeneration): Observable<any> {
		if (Constants.DEBUG) {

			console.log('Retrieving the Sunburst data for the project '
				+ settings.idProject + ', idStaff '
				+ settings.idStaffSelected
				+ ' and starting date ' + settings.startingDate
				+ ' on URL ' + this.backendSetupService.url() + '/project/sunburst');
		}
		const body = {
			idProject: settings.idProject, idStaffSelected: settings.idStaffSelected,
			startingDate: settings.startingDate
		};
		return this.httpClient.post<any>(this.backendSetupService.url() + '/project/sunburst', body, httpOptions);
	}

	/**
	 * Retrieve the contributors for a given project
	 * @param idProject project identifier
	 */
	contributors(idProject: number): Observable<ContributorsDTO> {
		const url = this.backendSetupService.url() + '/project/contributors/' + idProject;
		if (Constants.DEBUG) {
			console.log('Retrieve the contributors for the project identifier ' + idProject);
		}
		return this.httpClient.get<ContributorsDTO>(url);
	}

	/**
	 * Save the ghosts list with their connected data;
	 */
	saveGhosts(pseudoList: PseudoList): Observable<PseudoList> {
		if (Constants.DEBUG) {
			console.log('Saving data with for the project ' + pseudoList.idProject);
		}
		return this.httpClient.post<any>(this.backendSetupService.url() + '/project/api-ghosts', pseudoList, httpOptions);
	}

	/**
	 * Save the ghosts list with their connected data;
	 */
	resetDashboard(id: number): Observable<string> {
		const url = this.backendSetupService.url() + '/project/resetDashboard/' + id;
		if (Constants.DEBUG) {
			console.log('Reset of the dashboard data on URL ' + url);
		}
		return this.httpClient.get<string>(url, httpOptions);
	}
}
