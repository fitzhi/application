import { Injectable, EventEmitter } from '@angular/core';
import { Project } from '../../data/project';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, BehaviorSubject, EMPTY, of} from 'rxjs';
import { InternalService } from '../../internal-service';

import { Constants } from '../../constants';
import { Skill } from '../../data/skill';
import { ContributorsDTO } from '../../data/external/contributorsDTO';
import { SettingsGeneration } from '../../data/settingsGeneration';
import { BackendSetupService } from '../backend-setup/backend-setup.service';
import { take, tap, retryWhen, retry, flatMap, switchMap, catchError } from 'rxjs/operators';
import { Library } from '../../data/library';
import { BooleanDTO } from '../../data/external/booleanDTO';
import { ReferentialService } from '../referential.service';
import { SonarProject } from '../../data/SonarProject';
import { FilesStats } from '../../data/sonar/FilesStats';
import { ProjectSonarMetricValue } from '../../data/project-sonar-metric-value';
import { MessageService } from '../../interaction/message/message.service';
import { ResponseComponentMeasures } from '../../data/sonar/reponse-component-measures';
import { SonarService } from '../sonar.service';
import { MessageGravity } from '../../interaction/message/message-gravity';
import { AuditTopic } from '../../data/AuditTopic';
import { Task } from '../../data/task';
import { AttachmentFile } from '../../data/AttachmentFile';
import { FileService } from '../file.service';
import { Ecosystem } from '../../data/ecosystem';
import { traceOn, HttpCodes } from '../../global';
import { SunburstCinematicService } from '../../tabs-project/project-sunburst/service/sunburst-cinematic.service';
import { ProjectSkill } from '../../data/project-skill';
import { SkillService } from '../../skill/service/skill.service';
import { CinematicService } from '../cinematic.service';
import { GitService } from '../git/git.service';
import { ListProjectsService } from 'src/app/tabs-project/list-project/list-projects-service/list-projects.service';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class ProjectService extends InternalService {

	/**
	 * `BehaviorSubject` signaling to the application that a project has been loaded and ready to used.
	 */
	public projectLoaded$ = new BehaviorSubject<boolean>(false);

	/**
	 * Current project.
	 */
	public project: Project;

	/**
	 * The Sonar server declared for this project is accessible.
	 */
	public sonarIsAccessible = false;

	/**
	 * `BehaviorSubject` signaling that all projects have been loaded.
	 */
	allProjectsIsLoaded$ = new BehaviorSubject<boolean>(false);

	/**
	 * Array containing all projects retrieved from Fitzhì backend.
	 */
	allProjects: Project[];

	/**
	 * List of branches detected on the GIT repository.
	 */
	public branches$ = new BehaviorSubject<string[]>([]);

	constructor(
		private httpClient: HttpClient,
		private referentialService: ReferentialService,
		private cinematicService: CinematicService,
		private skillService: SkillService,
		private fileService: FileService,
		private messageService: MessageService,
		private gitService: GitService,
		private sunburstCinematicService: SunburstCinematicService,
		private backendSetupService: BackendSetupService) {
			super();
	}

	/**
   	* Load the global list of ALL projects, started in the organisation.
   	*/
	loadProjects() {
		if (traceOn()) {
			this.log(`Fetching the projects on URL ${this.backendSetupService.url()}/api/project`);
		}
		this.httpClient
			.get<Project[]>(`${this.backendSetupService.url()}/project`)
			.pipe(take(1))
			.subscribe(projects => {
				if (traceOn()) {
					console.groupCollapsed('Projects retrieved');
					console.table (projects);
					console.groupEnd();
				}
				projects.forEach(project => this.loadMapSkills(project));
				this.allProjects = projects;
				this.allProjectsIsLoaded$.next(true);
			});
	}

	/**
	 * Add the project to the global set of projects.
	 * 
	 * @param project the given project 
	 */
	public addProject(project: Project) {
		if  (this.allProjects.find(prj => prj.id === project.id)) {
			console.log ('WTF : project %d %s is already present in the allProjects collection.', project.id, project.name);
		}
		this.allProjects.push(project);
	}

	/**
	 * Actualize a single project in the projects array from the Rest API
	 * .
	 * @param idProject the project identifier to actualize
	 */
	public actualizeProject(idProject: number) {
		if (traceOn()) {
			this.log('Actualizing the project with URL ' + this.backendSetupService.url() + '/project/' + idProject);
		}
		this.httpClient
			.get<Project>(`${this.backendSetupService.url()}/project/${idProject}`)
			.pipe(take(1))
			.subscribe({
				next: project => {
					const actualProject = this.allProjects.find(prj => prj.id === idProject);
					//
					// Update the skills updated on the server on an actual project
					// Or add a new one in the list
					//
					if (actualProject) {
						actualProject.skills = project.skills;
						this.loadMapSkills(actualProject);
						this.dump(actualProject, 'projectService after updated the project');
					} else {
						this.loadMapSkills(project);
						this.allProjects.push(project);
						this.dump(project, 'projectService when creating a project');
					}
				},
				error: error => console.error ('WTF : ', error)
			});
	}

	/**
	 * Parse and return the given project identifier, or `undefined` if none was found.
	 *
	 * 2 kinds of URL can be passed to this function
	 * * .../project/(number)
	 * * .../project/(number)/staff
	 *
	 * The function has to return the given (number).
	 * @param url the url which sent the user in the project's area
	 */
	parseUrl(url: string): number {
		if (url === '/project') {
			return undefined;
		} else {
			//
			// Position of '/staff' in the given URL, if any.
			//
			const posStaff = url.indexOf('/staff');
			return (posStaff === -1) ?
				Number(url.substr('/project/'.length)) :
				Number(url.substr('/project/'.length, posStaff - '/project/'.length));

		}
	}

	/**
	 * Create an empty project
	 */
	createEmptyProject() {
		this.project = new Project();
	}

	/**
	* Create a new project, read the saved one, and return the project in an observable.
	*/
	createNewProject$ (): Observable<Project> {
		if (traceOn()) {
			console.log( 'Creating the project %s', this.project.name);
		}
		return this.httpClient.post(this.backendSetupService.url() + '/project', this.project, {observe: 'response'})
			.pipe(
				take(1),
				switchMap(response => {
					const location = response.headers.get('Location');
					if (traceOn()) {
						console.log (`Project created successfully, location returned ${location}`);
					}
					return (location) ? this.loadProject$(location) : EMPTY;
				}),
				catchError(error => {
					if (traceOn()) {
						console.log ('Error thrown', error);
					}
					return EMPTY;
				}
			)
		);
	}

	/**
	 * Load the project behind an URI location.
	 * 
	 * @param location the location to be accessed
	 */
	loadProject$(location: string): Observable<Project> {

		return this.httpClient.get<Project>(location)
			.pipe(switchMap(
				project => {
					this.project = project;
					if (!project.mapSkills) {
						project.mapSkills = new Map<number, ProjectSkill>();
					}

					// We add the project into the set 'allProjects'.
					this.addProject(project);

					return of(project);
				}
			)
		);
	}

	/**
	* Updating the current project
	*/
	public updateCurrentProject$(): Observable<boolean> {
		if (traceOn()) {
			console.log( 'Updating the project %s', this.project.name);
		}
		return this.httpClient
			.put<Project>(`${this.backendSetupService.url()}/project/${this.project.id}`, this.project,  {observe: 'response'})
			.pipe(
				take(1),
				switchMap( response => {
					if (response.status === HttpCodes.noContent) {
						this.messageService.success('Project successfully updated!');
						return of(true);
					} else {
						console.error ('WTF : Should not pass here!');
						return of(false);
					}
				}),
				catchError( responseInError => {
					switch (responseInError.status) {
						case HttpCodes.methodNotAllowed:
							this.messageService.error('You are not allowed to modify this project');
							break;
						case  HttpCodes.notFound:
							this.messageService.error('This project has most probably been removed by another user');
							break;
						default:
							console.error ('WTF : Should not pass here!');
					}
					return of(false);
				})
			);
	}

	/**
	* Add a skill to a project.
	* @param idProject the project identifier
	* @param idSkill the skill identifier
	*/
	addSkill$(idProject: number, idSkill: number): Observable<Boolean> {
		if (traceOn()) {
			console.log(`Adding the skill ${idSkill} for the project with id ${idProject}`);
		}
		return this.httpClient
			.put<Boolean>(`${this.backendSetupService.url()}/project/${idProject}/skill/${idSkill}`, httpOptions)
			.pipe(take(1));
	}

	/**
	 * POST: Remove a skill from the project skills list.
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 */
	delSkill$(idProject: number, idSkill: number): Observable<Boolean> {
		if (traceOn()) {
			console.log(`Remove the skill with ID ${idSkill} from the project with ID ${idProject}`);
		}
		return this.httpClient
			.delete<Boolean>(`${this.backendSetupService.url()}/project/${idProject}/skill/${idSkill}`, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Link a Sonar project to 'our project'
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 */
	addSonarProject(idProject: number, sonarProject: SonarProject): Observable<boolean> {

		if (traceOn()) {
			console.log (`Adding the Sonar project ${sonarProject.name} to the project ID ${idProject}`);
		}

		return this.httpClient
			.put<boolean>(`${this.backendSetupService.url()}/project/${idProject}/sonar`, sonarProject, httpOptions)
			.pipe(take(1));

	}

	/**
	 * Unlink a Sonar project to a Fitzhi project.
	 * 
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 */
	delSonarProject(idProject: number, sonarProject: SonarProject) {

		if (traceOn()) {
			console.log ('Removing the Sonar project ' + sonarProject.name + ' to the project ID ' + idProject);
		}

		return this.httpClient
			.delete<boolean>(`${this.backendSetupService.url()}/project/${idProject}/sonar/${sonarProject.key}`, 
				httpOptions)
			.pipe(take(1));
	}

	/**
	 * Add a topic to the audit in the current project.
	 * @param idTopic the topic identifier
	 * @return an observable emitting **true** if the creation succeeds, **false** otherwise
	 */
	addAuditTopic$(idTopic: number): Observable<boolean> {
		return this.httpClient
			.put<boolean>(`${this.backendSetupService.url()}/project/${this.project.id}/audit/topic/${idTopic}`, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Remove a topic from the scope of audit in the current project.
	 * @param idTopic the topic identifier
	 * @return an observable emitting **true** if the removal succeeds, **false** otherwise
	 */
	removeAuditTopic$(idTopic: number): Observable<boolean> {
		return this.httpClient
			.delete<boolean>(`${this.backendSetupService.url()}/project/${this.project.id}/audit/topic/${idTopic}`, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Load the skills associated with the given project.
	 * @param idProject the project identifier
	 * @returns an obsbervable emetting the array of skills declared or detected for the given project 
	 */
	loadSkills$(idProject: number): Observable<Skill[]> {
		return this.httpClient.get<Skill[]>(`${this.backendSetupService.url()}/project/skills/${idProject}`);
	}

	/**
	 * GET the project associated to this id from the back-end of Fitzhi. Will throw a 404 if this id is not found.
	 */
	get(id: number): Observable<Project> {
		const url =  `${this.backendSetupService.url()}/project/${id}`;
		if (traceOn()) {
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
	 * Return the found project or `undefined` if none's found
	 * Search the project associated with the passed identifier within the collection of all projects
	 * @param idProject the project identifier to search for inside the collection
	 */
	getProjectById(idProject: number): Project {

		const project = this.allProjects.find(prj => prj.id === idProject);
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
		const url = `${this.backendSetupService.url()}/project/name/${projectName}`;
		if (traceOn()) {
			console.log('Fetching the project name ' + projectName + ' on the address ' + url);
		}
		return this.httpClient.get<Project>(url);
	}

	/**
	 * Retrieve the Sunburst data, figuring the activity on the source code regarding the passed project.
	 * @param settings the settings givent to this generation, such as
	 * * the project identifier (of course)
	 * * the staff identifier
	 * * the starting date of test
	 */
	loadDashboardData$(settings: SettingsGeneration): Observable<any> {
		if (traceOn()) {
			console.log('Retrieving the Sunburst data for the project '
				+ settings.idProject + ', idStaff '
				+ settings.idStaffSelected
				+ ' and starting date ' + settings.startingDate
				+ ' on URL ' + this.backendSetupService.url() + '/api/project/sunburst');
		}
		const body = {
			idProject: settings.idProject, 
			idStaffSelected: settings.idStaffSelected,
			startingDate: settings.startingDate
		};
		return this.httpClient
			.put<any>(`${this.backendSetupService.url()}/project/${settings.idProject}/sunburst`, body, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Loading the **Map<number, ProjectSkill>** based on what we have read from the server.
	 * @param project project whose mapSkills have to be updated.
	 */
	public loadMapSkills(project: Project) {
		project.mapSkills = new Map<number, ProjectSkill>();
		Object.keys(project.skills).forEach(id => {
			project.mapSkills.set(Number(id), project.skills[id]);
		});
	}

	/**
	 * Return an observable emiting the tracking record for the dashboard generation.
	 * Load the activity records for the dashboard generation.
	 * @param idProject the project identifier
	 */
	loadTaskActivities$(idProject: number): Observable<Task> {
		return this.httpClient
			.get<Task>(
				this.backendSetupService.url() + '/project/tasks/dashboardGeneration/' + idProject,
				httpOptions);
	}

	/**
	 * Retrieve the contributors for a given project.
	 * If the given project identifier is equal to -1, this function will return an empty observable.
	 * @param idProject project identifier
	 */
	contributors$(idProject: number): Observable<ContributorsDTO> {

		if (idProject === -1) {
			return EMPTY;
		}

		const url =  `${this.backendSetupService.url()}/project/${idProject}/contributors`;
		if (traceOn()) {
			console.log('Retrieve the contributors for the project identifier %d @ url %s', idProject, url);
		}
		return this.httpClient.get<ContributorsDTO>(url);
	}

	/**
	 * **RESET** the whole chart data, connected to the dashboard generation.
	 * @param id project identifier to **RESET**
	 */
	public resetDashboard(id: number): Observable<string> {
		const url = `${this.backendSetupService.url()}/project/${id}/sunburst`;
		if (traceOn()) {
			console.log(`Reset the dashboard data on URL ${url}`);
		}
		this.sunburstCinematicService.listenEventsFromServer$.next(true);
		return this.httpClient.delete<string>(url, httpOptions);
	}

	/**
	 * **RELOAD** the chart data.
	 * @param id project identifier to **RELOAD**
	 */
	public reloadSunburst$(id: number): Observable<string> {
		const url = `${this.backendSetupService.url()}/project/${id}/sunburst`;
		if (traceOn()) {
			console.log(`Reload the dashboard data on URL ${url}`);
		}
		this.sunburstCinematicService.listenEventsFromServer$.next(true);
		return this.httpClient.put<string>(url, httpOptions);
	}

	/**
	 * Test a connection to GIT on server, in order to validate the connection settings.
	 * @param idProject project whose connection settings has to be tested
	 */
	testConnection$(idProject: number): Observable<boolean> {
		const url =  `${this.backendSetupService.url()}/project/${idProject}/test`;
		if (traceOn()) {
			console.log('Testing the connection settings on URL ' + url);
		}
		return this.httpClient.get<boolean>(url, httpOptions);
	}

	/**
	 * Load the branches available on GIT for the given project.
	 */
	public loadBranches() {

		if (traceOn()) {
			console.log ('Loading the branches...');
		}

		// The project is not already created.
		if (!this.project) {
			this.branches$.next([]);
		}

		// The project is not yet associated to a source code repository.
		if (!this.project.urlRepository) {
			this.branches$.next([]);
		}

		const url = `${this.backendSetupService.url()}/project/${this.project.id}/branches`;
		if (traceOn()) {
			console.log('Loading the branches for the URL ' + url);
		}

		this.httpClient.get<any>(url, httpOptions)
			.pipe(take(1))
			.subscribe({
				next: (branches: string[]) => {
					if (traceOn()) {
						console.groupCollapsed('List of branches for project %s', this.project.name);
						branches.forEach(branch => console.log (branch));
						console.groupEnd();
					}
					this.branches$.next(branches);
					this.gitService.assistanceMessageGitBranches$.next(false);
				}
			});

	}

	/**
	 * Test if the given url is valid, or not.
	 * @param urlCodeFactor URL of the codeFactor.io project
	 */
	public testConnectionCodeFactorIO$(): Observable<Boolean> {
		return this.httpClient
			.get<boolean>(this.urlCodeFactorIO(), { observe: 'response' })
			.pipe(
				take(1),
				switchMap(
					response => {
						console.log (response);
						return of(true);
					}),
				catchError((e) => {
					console.error (e);
					return of(false);
				}
			));
	}

	/**
	 * Return the url pointing out to the codeFactor.io badge.
	 */
	urlCodeFactorIO() {
		return  `${this.project.urlCodeFactorIO}/badge/master?style=plastic`;
	}

	/**
	 * Retrieve the directories starting with 'criteria'.
	 * 
	 * @param idProject the identifier of the project
	 * @param criteria the searched criteria
	 * @return an observable emitting the list of directories.
	 */
	libDirLookup$(idProject: number, criteria: string): Observable<string[]> {
		const url =  `${this.backendSetupService.url()}/project/${idProject}/analysis/lib-dir/${criteria}`;
		return this.httpClient.get<string[]>(url).pipe(take(1));
	}

	/**
	 * Save the libraries detected or declared.
	 * 
	 * @param idProject the identifier of the project
	 * @param libraries the libraries directories
	 */
	libDirSave(idProject: number, libraries: Library[]) {
		if (traceOn()) {
			console.groupCollapsed('Saving libraries', libraries);
			libraries.forEach(lib => console.log (lib.exclusionDirectory));
			console.groupEnd();
		}
		const url =  `${this.backendSetupService.url()}/project/${idProject}/analysis/lib-dir/`;
		this.httpClient
			.post<boolean>(url, libraries, httpOptions)
			.pipe(take(1))
			.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.messageService.info('Libraries detected have been saved');
					}
				}
			});
	}

	/**
	* Update a ghost from a project.
	* @param idProject the given project identifier
	* @param pseudo the pseudo used by a ghost to proceed a commit
	* @param idRelatedStaff a staff identifier if the ghost has to be related to him, or -1 if this pseudo is related to no one.
	* @param technical: TRUE if the ghost is in fact a technical user used for administration operations
	*/
	updateGhost$(idProject: number, pseudo: string, idRelatedStaff: number, technical: boolean): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed('Updating a ghost');
			console.log ('idProject', idProject);
			console.log ('pseudo', pseudo);
			console.log ('idStaff', idRelatedStaff);
			console.log ('techinical?', technical);
			console.groupEnd();
		}
		const body = { idProject: idProject, pseudo: pseudo, idStaff: idRelatedStaff, technical: technical };
		return this.httpClient.post<boolean>(`${this.backendSetupService.url()}/project/${idProject}/ghost`, body, httpOptions);
	}

	/**
	* Remove a ghost from the ghost list of a project.
	* @param idProject the given project identifier
	* @param pseudo the pseudo used by a ghost to proceed a commit
	*/
	removeGhost$(idProject: number, pseudo: string): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed('Removing a ghost');
			console.log ('idProject', idProject);
			console.log ('pseudo', pseudo);
			console.groupEnd();
		}
		return this.httpClient.delete<boolean>(
			this.backendSetupService.url() + '/project/' + idProject + '/ghost/' + pseudo, httpOptions);
	}

	/**
	 * Returns the color associated to the given evalution.
	 *
	 * The function transform the evaluation into a risk and then will invoke `getRiskColor`.
	 * @param evaluation the evaluation processed for the project (between 0 and 100).
	 */
	getEvaluationColor(evaluation: number): string {
		const risk = 10 - Math.ceil(evaluation / 10);
		return this.getRiskColor(risk);
	}

	/**
	 * Returns the color associated to the given risk.
	 * @param risk the risk evaluated for ths project. A risk is a number between 0 an 10.
	 */
	getRiskColor(risk: number): string {

		switch (risk) {
			case -1:
				return 'whiteSmoke';
			default:
				const riskLegend = this.referentialService.legends
					.find (legend => legend.level === risk);
				if (riskLegend) {
					return riskLegend.color;
				} else {
					if (traceOn()) {
						console.log('Unknown risk level', risk);
					}
					return 'whiteSmoke';
				}
			}
	}

	/**
	* Save the file statistics for a project.
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param filesStats the language file statistics retrieved from the Sonar instance.
	*/
	saveFilesStats$(idProject: number, key: string, filesStats: FilesStats[]): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed('Save the files stats');
			console.log ('idProject', idProject);
			console.log ('key', key);
			filesStats.forEach(fs => console.log (fs.language, fs.numberOfFiles));
			console.groupEnd();
		}

		return this.httpClient.put<boolean>(
			 `${this.backendSetupService.url()}/project/${idProject}/sonar/${key}/filesStats`, filesStats, httpOptions);
	}

	/**
	* Save the file statistics for a project.
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param filesStats the language file statistics retrieved from the Sonar instance.
	*/
	saveMetricValues$(idProject: number, key: string, metricValues: ProjectSonarMetricValue[]): Observable<Boolean> {
		if (traceOn()) {
			console.groupCollapsed('Save the metric values for Sonar entry %s project identifier %d', key, idProject);
			metricValues.forEach(mv => console.log ('Saving %s %d %d', mv.key, mv.weight, mv.value));
			console.groupEnd();
		}

		return this.httpClient.put<boolean>(
			 `${this.backendSetupService.url()}/project/${idProject}/sonar/${key}/metricValues`, metricValues, httpOptions);
	}

	/**
	 * Select and retrieve the SonarProject.
	 * @param project the given project
	 * @param sonarKey the key of the Sonar project.
	 * @returns the Sonar project for the given key
	 */
	getSonarProject(project: Project, sonarKey: string): SonarProject {
		return project.sonarProjects.find(sonarP => (sonarKey === sonarP.key));
	}

	/**
	 * Load the Sonar project for the given parameter.
	 * @param project the current project
	 * @param sonarKey the key of the Sonar project
	 */
	loadSonarProject$ (project: Project, sonarKey: string): Observable<SonarProject> {
		return this.httpClient.get<SonarProject>(
			`${this.backendSetupService.url()}/project/${project.id}/sonar/${sonarKey}`, httpOptions)
			.pipe(tap(
				(sonarProject: SonarProject) => {
					if (traceOn()) {
						console.groupCollapsed ('Sonar project', sonarProject.key);
						sonarProject.projectSonarMetricValues.forEach(metricValues => {
							console.log (metricValues.key, metricValues.weight);
						});
						console.groupEnd();
					}
				}));

	}

	/**
	 * Select and retrieve the ProjectSonarMetricValue or undefined if none is found.
	 * @param project the given project
	 * @param sonarKey the key of the Sonar project.
	 * @returns the Sonar metric record for the given metric key
	 */
	getProjectSonarMetricValue(project: Project, sonarKey: string, metricKey: string): ProjectSonarMetricValue {
		const sonarProject = this.getSonarProject(project, sonarKey);
		if (sonarProject) {
			return sonarProject.projectSonarMetricValues.find(
				(psmv: ProjectSonarMetricValue) => (metricKey === psmv.key));
		}
		return undefined;
	}

	/**
	 * Load from Sonar the evaluation for the given metrics.
	 * @param sonarService the service **sonarService** is passed to the method after the creation of the service **projectService**.
	 * Because, the url of the *Sonar server* is saved on the project object.
	 * So we need to create :
	 * - First the projectService,
	 * - Then the sonarService *(which will be informed of the url of the Sonar server)*.
	 * @param project the given project
	 * @param sonarKey the key of the Sonar project
	 * @param metricValues the array of Metric records to update with the Sonar last evaluation.
	 * @param messageErrorEmitter an eventEmitter to throw the success, or error message, if any.
	 */
	loadAndSaveEvaluations(
			sonarService: SonarService,
			project: Project,
			sonarKey: string,
			metricValues: ProjectSonarMetricValue[],
			messageErrorEmitter: EventEmitter<MessageGravity>) {

		sonarService.loadProjectSonarComponentMeasures$(
				project,
				sonarKey,
				metricValues.map(psmv => psmv.key))
			.subscribe({
				next: (measures: ResponseComponentMeasures) => this.loadMesures(project, sonarKey, metricValues, measures, messageErrorEmitter),
				error: error => console.log(error)
			});
	}

	/**
	 * Take in acccount the measures retrieved from Sonar into the array of metricsValue.
	 * @param project given project
	 * @param sonarKey the Sonar server key
	 * @param metricValues the metrics value array to be informed of the new measures.
	 * @param measures the measures read.
	 * @param eventEmitter is any error occurs.
	 */
	loadMesures(
		project: Project,
		sonarKey: string,
		metricValues: ProjectSonarMetricValue[],
		measures: ResponseComponentMeasures,
		messageErrorEmitter: EventEmitter<MessageGravity>) {

		measures.component.measures.forEach(measure => {
			const psmv = metricValues.find(mv => mv.key === measure.metric);
			if (!isNaN(Number(measure.value))) {
				psmv.value = Number(measure.value);
			} else {
				if (measure.value === 'OK') {
					psmv.value = 1;
				} else {
					if (measure.value === 'ERROR') {
						psmv.value = 0;
					} else {
						console.error ('Unexpected value of measure', measure.value);
					}
				}
			}
		});

		this.dump(project, 'loadEvaluations');

		//
		// the metricValues is updated with the evaluation returned by Sonar.
		//
		this.saveMetricValues$(project.id, sonarKey, metricValues)
			.pipe(take(1))
			.subscribe (ok => {
				if (ok) {
					messageErrorEmitter.next(
						new MessageGravity(Constants.MESSAGE_INFO,
						'Metrics weights and values have been saved for the Sonar project ' + sonarKey));
				} else {
					messageErrorEmitter.next(
						new MessageGravity(Constants.MESSAGE_ERROR,
						'Error when saving weights and values for the Sonar project ' + sonarKey));
				}
			});
	}

	/**
	* Save the evaluation for a project.
	*
	* Return an observable emitting a boolean if the operation succeeds.
	*
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param evaluation the evaluation processed for this Sonar project
	* @param totalNumberLinesofCode the number of lines of code detected for this Sonar project
	*/
	saveSonarEvaluation$(idProject: number, key: string, evaluation: number, totalNumberLinesOfCode: number): Observable<Boolean> {
		if (traceOn()) {
			console.groupCollapsed('Saving the evaluation for Sonar entry %s project identifier %d', key, idProject);
			console.log ('Evaluation obtained', evaluation);
			console.log ('Total number of lines of code', totalNumberLinesOfCode);
			console.groupEnd();
		}

		return this.httpClient.put<boolean>(
			 `${this.backendSetupService.url()}/project/${idProject}/sonar/${key}/evaluation`, 
			{evaluation: evaluation, totalNumberLinesOfCode: totalNumberLinesOfCode}, httpOptions);
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idProject the given project identifier
	* @param idTopic the topic identifier
	* @param evaluation the evaluation given for that topic
	*/
	saveAuditTopicEvaluation$(idProject: number, idTopic: number, evaluation: number): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed(
				'Saving the evaluation for the topic identified by %d, within the project identified by %d',
				idTopic, idProject);
			console.log ('Evaluation given', evaluation);
			console.groupEnd();
		}
		const auditTopic = new AuditTopic(idTopic, evaluation, null);

		return this.httpClient
			.put<boolean>( 
				`${this.backendSetupService.url()}/project/${idProject}/audit/${idTopic}/evaluation/${evaluation}`, 
				httpOptions)
			.pipe(take(1));
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idTopic the topic identifier
	* @param report the audit report given by the expert
	*/
	saveAuditTopicReport$(idTopic: number, report: string): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed(
				'Saving the audit report for the topic identified by %d, within the project identified by %d',
				idTopic, this.project.id);
			console.log ('Report given', report);
			console.groupEnd();
		}

		return this.httpClient
			.put<boolean>(
				`${this.backendSetupService.url()}/project/${this.project.id}/audit/${idTopic}/report`, 
				report, httpOptions)
			.pipe(take(1));
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idProject the given project identifier
	* @param auditTopics the topic identifier
	*/
	saveAuditTopicWeights$(idProject: number, auditTopics: AuditTopic[]): Observable<boolean> {
		if (traceOn()) {
			console.groupCollapsed('Saving the weights for the topic identified by %d', idProject);
			auditTopics.forEach(element => console.log (element.idTopic, element.weight));
			console.groupEnd();
		}
		
		return this.httpClient
			.put<boolean>(`${this.backendSetupService.url()}/project/${this.project.id}/audit/weights`, 
					auditTopics, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Process the Audit global evaluation for a project.
	 */
	processGlobalAuditEvaluation(): void {

		// Nothing to do.
		if (!this.project.audit) {
			this.project.auditEvaluation = 0;
			return;
		}

		let result = 0;
		Object.keys(this.project.audit).forEach(key => {
			result += this.project.audit[key].evaluation * this.project.audit[key].weight;
		});
		this.project.auditEvaluation = Math.floor(result / 100);
		this.dump(this.project, 'processGlobalAuditEvaluation');
	}

	/**
	 * Onboard a developer in the given project.
	 * @param idProject Project identifier
	 * @param idStaff Staff identifier
	 */
	public onBoardStaffInProject(idProject: number, idStaff: number): void {
		this.httpClient
			.get<boolean>(this.backendSetupService.url() + '/project/analysis/onboard/' + idProject + '/' + idStaff)
			.pipe(take(1))
			.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						if (traceOn()) {
							console.log ('onBoard staff %d in project %d', idStaff, idProject);
						}
					}
				}
		});
	}

	/**
     * GET : Download an attachment file previously uploaded.
	 * @param idProject the current project identifier
	 * @param idTopic the current topic identifier
	 * @param attachmentFile the given attachmentFile
     */
	downloadAuditAttachment(idProject: number, idTopic: number, attachmentFile: AttachmentFile) {

		if (!attachmentFile.fileName) {
			return;
		}
		if (traceOn()) {
			console.log('Download the audit attachment file : ' + attachmentFile.fileName);
		}

		this.fileService.downloadFile(
			attachmentFile.fileName,
			this.backendSetupService.url() +
			'/project/' + idProject + '/audit/' + idTopic + '/attachment/' + attachmentFile.idFile);
	}

	/**
     * Delete an attachment file
	 * @param idProject the current project identifier
	 * @param idTopic the current topic identifier
	 * @param attachmentFile the attachmentFile to be deleted
     */
	deleteAuditAttachment(idProject: number, idTopic: number, attachmentFile: AttachmentFile) {

		if (!attachmentFile.fileName) {
			return;
		}
		if (traceOn()) {
			console.log('Delete the audit attachment file : ' + attachmentFile.fileName);
		}

		const body = { idProject: idProject, idTopic: idTopic, attachmentFile: {idFile: attachmentFile.idFile}};
		return this.httpClient
			.post<boolean>(this.backendSetupService.url() + '/project/audit/removeAttachmentFile', body, httpOptions)
			.pipe(take(1));

	}

	/**
	 * Return the global mean __Sonar__ evaluation processed for all Sonar projects
	 * declared in the techzhì project.
	 *
	 * @param project the given project
	 */
	public calculateSonarEvaluation(project: Project): number {

		let globalSonarEvaluation = 0;

		if ((project.sonarProjects) && (project.sonarProjects.length > 0) && this.allSonarProjectsEvaluated(project)) {
			let totalEvalution = 0;
			let totalNumberLinesOfCode = 0;
			project.sonarProjects.forEach(sonarProject => {
				totalEvalution += sonarProject.sonarEvaluation.evaluation * sonarProject.sonarEvaluation.totalNumberLinesOfCode;
				totalNumberLinesOfCode += sonarProject.sonarEvaluation.totalNumberLinesOfCode;
			});

			// If we did not gather (for any reason) the total number of lines of code from Sonar, we cannot have an evaluation
			if (totalNumberLinesOfCode === 0) {
				return -1;
			}
			globalSonarEvaluation = Math.round(totalEvalution / totalNumberLinesOfCode);
		}

		return globalSonarEvaluation;
	}

	/**
	 * Return `true` if all sonar projects declared in this projet, have been evaluated, false otherwise.
	 *
	 * @param project the given project
	 */
	allSonarProjectsEvaluated(project: Project) {
		let complete = true;
		project.sonarProjects.forEach(sonarP => {
			if (complete && (!sonarP.sonarEvaluation)) {
				complete = false;
			}
		});
		return complete;
	}

	/**
	 * Dump the content of a given project.
	 * @param project the passed project.
	 * @param from: Method which made that call
	 */
	dump(project: Project, from: string): void {
		
		// No dump if we are in 'trace OFF' mode
		if (!traceOn()) {
			return;
		}

		if ((!project) || !(project.id)) {
			console.log (from, 'Project is null dude!');
			return;
		}

		console.log('Dump executed from', from);
		console.groupCollapsed(from, 'Project ' + project.id + ' ' + project.name);
		console.groupCollapsed('Global audit evaluation', project.auditEvaluation);
		if (project.audit) {
			Object.keys(project.audit).forEach(key => {
				console.log ('key: %s evaluation: %d weight: %d', key, project.audit[key].evaluation, project.audit[key].weight);
			});
		} else {
			console.log ('project.audit is null');
		}
		console.groupEnd();

		if (project.mapSkills) {
			console.groupCollapsed(project.mapSkills.size + ' skills declared.');
			for (const [k, v] of project.mapSkills) {
				console.log (k, this.skillService.title(k));
			}
			console.groupEnd();
		} else {
			console.log ('project.mapSkills is null');
		}

		if (project.sonarProjects) {
			console.groupCollapsed(project.sonarProjects.length + ' sonar project declared.');
			project.sonarProjects.forEach(sonarProject => {
				if (sonarProject.projectSonarMetricValues) {
					console.groupCollapsed('Soner project %s', sonarProject.key);
					sonarProject.projectSonarMetricValues.forEach(metricValue =>
						console.log (metricValue.key, 'w' + metricValue.weight + ' v:' + metricValue.value)
					);
					console.groupEnd();
				}
			});
			console.groupEnd();
		} else {
			console.log ('project.sonarProjects is null');
		}

		if (project.audit) {
			console.groupCollapsed(Object.keys(project.audit).length + ' audit topics');
			Object.keys(project.audit).forEach(key => {
				console.groupCollapsed('Audit topic %s', key);
				project.audit[key].attachmentList.forEach((element: AttachmentFile) => {
					console.log(element.fileName, element.label);
				});
				console.groupEnd();
			});
			console.groupEnd();
		} else {
			console.log ('project.audit is null');
		}

		console.groupEnd();
	}

	/**
	 * This function is proceeding 2 tasks :
	 *
	 * - It saves the Sonar URL associated with the project with the given identifier
	 * - It returns an `observable<boolean>` representing the completion, or not, of the remote operation
	 * @param idProject the project identifier
	 * @param urlSonarServer the URL of the Sonar server
	 */
	public saveSonarUrl$(idProject: number, urlSonarServer: string): Observable<boolean> {

		return this.httpClient
			.put<boolean>(`${this.backendSetupService.url()}/project/${idProject}/sonar/url`, urlSonarServer, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Return the array of ecosystems detected inside the given project.
	 * @param project the given project
	 */
	public retrieveEcosystems(project: Project): Ecosystem[] {
		const ecosystems: Ecosystem[] = [];
		project.ecosystems.forEach(idEcosystem => {
			const ecosystem = this.referentialService.ecosystems.find(ecosys => ecosys.id === idEcosystem);
			if (ecosystem) {
				ecosystems.push(ecosystem);
			} else {
				console.error('Internal error (contact the administrator) : there is no ecosystem with id %d', idEcosystem);
			}
		});
		return ecosystems;
	}

	/**
	 * Test if the project is completely empty, and therefore can be fully remove from the portfolio.
	 *
	 * This function is mirroring 2 methods implemented in Java
	 *
	 * - _Project.isEmpty()_
	 * - _StaffHandler.isProjectReferenced(idProject)_  **Not implemenetd yet in Typescript !**
	 *
	 * 2 options are possible :
	 *
	 * * **Remove** : Physically remove the project from the portfolio.
	 * * **Inactivate** : Inactivate a project for the analysis.
	 *
	 */
	public isProjectEmpty() {

		if (this.project.urlRepository !== null) {
			return false;
		}

		if (this.project.mapSkills.size > 0) {
			return false;
		}
		if (this.project.sonarProjects.length > 0) {
			return false;
		}
		if (Object.keys(this.project.audit).length > 0) {
			return false;
		}
		if (this.project.libraries.length > 0) {
			return false;
		}
		if (this.project.ecosystems.length > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Return the title of the tab to activate, or inactivate the project.
	 */
	public tabTitleInactivate() {
		return (this.project.active) ? 'Inactivate' : 'Reactivate';
	}

	/**
	 * This function will execute a Rest call to remove the actual project loaded in **this.project**
	 */
	public removeApiProject$(): Observable<boolean> {

		if (!this.project) {
			throw new Error ('WTF : Should not pass here !');
		}
		if (traceOn()) {
			console.log ('Removing the project %d %s', this.project.id, this.project.name);
		}

		return this.httpClient
			.delete<object>(`${this.backendSetupService.url()}/project/${this.project.id}`)
			.pipe(
				take(1),
				switchMap( () => {
					if (traceOn()) {
						console.log ('Project %s has beeen successfully removed', this.project.name);
					}
					
					// We remove the project from the local collection
					this.removeLocalProject(this.project.id);

					// We reinitialize the forms.
					this.project = new Project();
					this.projectLoaded$.next(true);
					this.cinematicService.projectTabIndex = Constants.PROJECT_IDX_TAB_FORM;
					return of(true)
				}),
				catchError((error) => {
					if (traceOn()) {
						console.log(error.message);
					}
					this.messageService.error(
						'Cannot remove project ' + this.project.name + ', error :' + error.message);
					return of(false);
				})
			);
	}

	/**
	 * Remove a project from the local collection of projects.
	 * 
	 * @param idProject the given project identifier.
	 */
	public removeLocalProject(idProject: number): void {
		// We remove the selected Project from the projects set
		const indexToDelete = this.allProjects.findIndex(prj => (idProject === prj.id));
		if (indexToDelete === -1) {
			throw Error('WTF : Should not pass here !');
		}
		this.allProjects.splice(indexToDelete, 1);
	}

	/**
	 * This function will inactivate the actual project loaded in **this.project**
	 */
	public inactivateProject(): void {
		if (!this.project) {
			throw new Error ('WTF : Should not pass here !');
		}
		if (traceOn()) {
			console.log (`Inactivating the project ${this.project.id} ${this.project.name}`);
		}

		this.httpClient
			.post<object>(`${this.backendSetupService.url()}/project/${this.project.id}/rpc/inactivation`, {})
			.pipe(take(1))
			.subscribe({
				next: () => {
					if (traceOn()) {
						console.log (`Project ${this.project.name} has been successfully inactivated.`);
					}
					// We inactivate the project
					this.project = this.retrieveProject(this.project.id);
					this.project.active = false;
					this.projectLoaded$.next(true);
					this.cinematicService.projectTabIndex = Constants.PROJECT_IDX_TAB_FORM;
				}
			});
	}

	/**
	 * This function will inactivate the actual project loaded in **this.project**
	 */
	public reactivateProject(): void {
		if (!this.project) {
			throw new Error ('WTF : Should not pass here !');
		}
		if (traceOn()) {
			console.log (`Reactivating the project ${this.project.id} ${this.project.name}`);
		}

		this.httpClient
			.post<object>(`${this.backendSetupService.url()}/project/${this.project.id}/rpc/reactivation`, {})
			.pipe(take(1))
			.subscribe({
				next: () => {
					if (traceOn()) {
						console.log (`Project ${this.project.name} has been successfully reactivated.`);
					}
					// We re-activate the project
					this.project = this.retrieveProject(this.project.id);
					this.project.active = true;
					this.projectLoaded$.next(true);
					this.cinematicService.projectTabIndex = Constants.PROJECT_IDX_TAB_FORM;
				}
			});
	}

	/**
	 * Retrieve the project from the set of projects.
	 * @param idProject the project identifier
	 */
	retrieveProject(idProject: number): Project {
		const index = this.allProjects.findIndex(prj => (this.project.id === prj.id));
		if (index === -1) {
			throw new Error ('WTF : Should not pass here !');
		}
		return this.allProjects[index];
	}

}
