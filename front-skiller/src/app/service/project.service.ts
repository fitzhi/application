import { Injectable } from '@angular/core';
import { Project } from '../data/project';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { InternalService } from '../internal-service';

import { Constants } from '../constants';
import { Skill } from '../data/skill';
import { ContributorsDTO } from '../data/external/contributorsDTO';
import { SettingsGeneration } from '../data/settingsGeneration';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take } from 'rxjs/operators';
import { Library } from '../data/library';
import { BooleanDTO } from '../data/external/booleanDTO';
import { ReferentialService } from './referential.service';
import { SonarProject } from '../data/SonarProject';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class ProjectService extends InternalService {

	allProjects: Project[];

	allProjects$ = new Subject<Project[]>();

	constructor(
		private httpClient: HttpClient,
		private referentialService: ReferentialService,
		private backendSetupService: BackendSetupService) {
			super();
	}

	/**
   	* Load the global list of ALL collaborators, working for the company.
   	*/
	loadProjects() {
		if (Constants.DEBUG) {
			this.log('Fetching the projects on URL ' + this.backendSetupService.url() + '/project/all');
		}
		this.httpClient
			.get<Project[]>(this.backendSetupService.url() + '/project/all')
			.pipe(take(1))
			.subscribe(projects => {
				if (Constants.DEBUG) {
					console.groupCollapsed('Projects retrieved');
					projects.forEach (project => console.log (project.name));
					console.groupEnd();
				}
				this.allProjects = projects;
				this.allProjects$.next(projects);
			});
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
	* @param idProject the project identifier
	* @param idSkill the skill identifier
	*/
	addSkill(idProject: number, idSkill: number): Observable<BooleanDTO> {
		if (Constants.DEBUG) {
			console.log('Adding the skill  ' + idSkill + ' for the project whom id is ' + idProject);
		}
		const body = { idProject: idProject, idSkill: idSkill };
		return this.httpClient.
			post<BooleanDTO>(this.backendSetupService.url() + '/project/skill/add', body, httpOptions)
			.pipe(take(1));
	}

	/**
	 * POST: Remove a skill from the project skills list.
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 */
	delSkill(idProject: number, idSkill: number): Observable<BooleanDTO> {
		if (Constants.DEBUG) {
			console.log('Remove a the skill with ID ' + idSkill + ' from the project with ID ' + idProject);
		}
		const body = { idProject: idProject, idSkill: idSkill };
		return this.httpClient
			.post<BooleanDTO>(this.backendSetupService.url() + '/project/skill/del', body, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Link a Sonar project to 'our project'
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 */
	addSonarProject(idProject: number, sonarProject: SonarProject) {
		return this.accessSonarProject(idProject, sonarProject, 'saveEntry');
	}

	/**
	 * Unlink a Sonar project to 'our project'
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 */
	delSonarProject(idProject: number, sonarProject: SonarProject) {
		return this.accessSonarProject(idProject, sonarProject, 'removeEntry');
	}

	/**
	 * Access the Sonar projects of 'our project'
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 * @param action the action to be executed on the Sonar projects collection
	 */
	private accessSonarProject(idProject: number, sonarProject: SonarProject, action: string) {

		if (Constants.DEBUG) {
			console.log ('Action ' + action + ' for a Sonar project ' + sonarProject.name + ' for project ID ' + idProject);
		}

		const body = {
			'idProject': idProject,
			'sonarProject': {'key': sonarProject.key, 'name': sonarProject.name}
		};

		return this.httpClient
			.post<BooleanDTO>(this.backendSetupService.url() + '/project/sonar/' + action, body, httpOptions)
			.pipe(take(1));
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
	 * Search the project associated to the passed name within the collection of all projects
	 * @param projectName the project name to search for inside the collection
	 * @returns the found project or undefined if none's found
	 */
	getProject(projectName: string): Project {

		const project = this.allProjects.find(prj => prj.name === projectName);
		return project;
	}

	/**
	 * Load the project associated to the passed name, if any, from the back-end skiller.
	 * Will throw a 404 if this name is not retrieved.
	 * @param projectName the project name to loook for on the backend.
	 */
	lookup(projectName: string): Observable<Project> {

		const project = this.getProject(projectName);
		if (!project) {
			return new BehaviorSubject<Project>(project);
		}

		// If we do not find the project in the global collection of projects,
		// we try our chance in the backend.
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
	resetDashboard(id: number): Observable<string> {
		const url = this.backendSetupService.url() + '/project/resetDashboard/' + id;
		if (Constants.DEBUG) {
			console.log('Reset of the dashboard data on URL ' + url);
		}
		return this.httpClient.get<string>(url, httpOptions);
	}

	/**
	 * Retrieve the directories starting with 'criteria'.
	 * @param idProject the identifier of the project
	 * @param criteria the searched criteria
	 * @return an observable to the list of directories found
	 */
	libDirLookup(idProject: number, criteria: string): Observable<string[]> {
		const url = this.backendSetupService.url()
			+ '/project/analysis/lib-dir/lookup?idProject=' + idProject
			+ '&criteria=' + criteria;
		return this.httpClient.get<string[]>(url)
			.pipe(take(1));
	}

	/**
	 * Save the libraries detected or declared.
	 * @param idProject the identifier of the project
	 * @param libraries the libraries directories
	 */
	libDirSave(idProject: number, libraries: Library[]) {
		if (Constants.DEBUG) {
			console.groupCollapsed('Saving libraries', libraries);
			libraries.forEach(lib => console.log (lib.exclusionDirectory));
			console.groupEnd();
		}
		const url = this.backendSetupService.url()
			+ '/project/analysis/lib-dir/save/' + idProject;
		return this.httpClient
			.post<Boolean>(url, libraries, httpOptions)
			.pipe(take(1))
			.subscribe(res => console.log(res));
	}

	/**
	* Update a ghost from a project.
	* @param idProject the given project identifier
	* @param pseudo the pseudo used by a ghost to proceed a commit
	* @param idRelatedStaff a staff identifier if the ghost has to be related to him, or -1 if this pseudo is related to no one.
	* @param technical: TRUE if the ghost is in fact a technical user used for administration operations
	*/
	updateGhost(idProject: number, pseudo: string, idRelatedStaff: number, technical: boolean): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Updating a ghost');
			console.log ('idProject', idProject);
			console.log ('pseudo', pseudo);
			console.log ('idStaff', idRelatedStaff);
			console.log ('techinical?', technical);
			console.groupEnd();
		}
		const body = { idProject: idProject, pseudo: pseudo, idStaff: idRelatedStaff, technical: technical };
		return this.httpClient.post<Boolean>(this.backendSetupService.url() + '/project/ghost/save', body, httpOptions);
	}

	/**
	* Remove a ghost from the ghost list of a project.
	* @param idProject the given project identifier
	* @param pseudo the pseudo used by a ghost to proceed a commit
	*/
	removeGhost(idProject: number, pseudo: string): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Removing a ghost');
			console.log ('idProject', idProject);
			console.log ('pseudo', pseudo);
			console.groupEnd();
		}
		const body = { idProject: idProject, pseudo: pseudo };
		return this.httpClient.post<Boolean>(this.backendSetupService.url() + '/project/ghost/remove', body, httpOptions);
	}

	/**
	 * @param risk the risk evaluated for a project
	 * @returns the color corresponding to the passed risk
	 */
	getRiskColor(risk: number): string {
		switch (risk) {
			case -1:
				return 'whiteSmoke';
			default:
				return this.referentialService.legends
					.find (legend => legend.level === risk)
					.color;
			}

	}
}
