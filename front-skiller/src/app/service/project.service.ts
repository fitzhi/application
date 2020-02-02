import { Injectable, EventEmitter } from '@angular/core';
import { Project } from '../data/project';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, Subject, BehaviorSubject, EMPTY, of } from 'rxjs';
import { InternalService } from '../internal-service';

import { Constants } from '../constants';
import { Skill } from '../data/skill';
import { ContributorsDTO } from '../data/external/contributorsDTO';
import { SettingsGeneration } from '../data/settingsGeneration';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { take, tap, retryWhen, retry } from 'rxjs/operators';
import { Library } from '../data/library';
import { BooleanDTO } from '../data/external/booleanDTO';
import { ReferentialService } from './referential.service';
import { SonarProject } from '../data/SonarProject';
import { FilesStats } from '../data/sonar/FilesStats';
import { Component } from '@angular/compiler/src/core';
import { ProjectSonarMetricValue } from '../data/project-sonar-metric-value';
import { MessageService } from '../message/message.service';
import { ResponseComponentMeasures } from '../data/sonar/reponse-component-measures';
import { SonarService } from './sonar.service';
import { MessageGravity } from '../message/message-gravity';
import { AuditTopic } from '../data/AuditTopic';
import { Task } from '../data/task';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';
import { AttachmentFile } from '../data/AttachmentFile';
import { FileService } from './file.service';
import { Ecosystem } from '../data/ecosystem';

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
	allProjectsIsLoaded$ = new BehaviorSubject<Boolean>(false);

	/**
	 * Array containing all projects retrieved from Fitzhì backend.
	 */
	allProjects: Project[];


	constructor(
		private httpClient: HttpClient,
		private referentialService: ReferentialService,
		private fileService: FileService,
		private messageService: MessageService,
		private backendSetupService: BackendSetupService) {
			super();
	}

	/**
   	* Load the global list of ALL projects, working for the company.
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
				this.allProjectsIsLoaded$.next(true);
			});
	}

	/**
	* Save the project (it might a creation or an update).
	* @param project the given projet to be saved.
	*/
	save(project: Project): Observable<Project> {
		if (Constants.DEBUG) {
			console.log(((typeof project.id !== 'undefined') ? 'Saving ' : 'Adding ') + 'project ' + project.name);
		}
		return this.httpClient
			.post<Project>(this.backendSetupService.url() + '/project/save', project, httpOptions);
	}

	/**
	 * Add or update a project inside the collection
	 * @param project the given project
	 */
	updateProjectsCollection(project: Project): void {
		this.allProjects = this.allProjects.filter(prj => prj.id !== project.id);
		this.allProjects.push(project);
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
		return this.httpClient
			.post<BooleanDTO>(this.backendSetupService.url() + '/project/skill/add', body, httpOptions)
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
	addSonarProject(idProject: number, sonarProject: SonarProject): Observable<Boolean> {
		return this.handleActionSonarProject$(idProject, sonarProject, 'saveEntry');
	}

	/**
	 * Unlink a Sonar project to 'our project'
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 */
	delSonarProject(idProject: number, sonarProject: SonarProject) {
		return this.handleActionSonarProject$(idProject, sonarProject, 'removeEntry');
	}

	/**
	 * Execute an operation on the Sonar projects of a Techxhì project
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project
	 * @param action the action to be executed on the Sonar projects collection
	 */
	handleActionSonarProject$(idProject: number, sonarProject: SonarProject, action: string): Observable<Boolean> {

		if (Constants.DEBUG) {
			console.log ('Action ' + action + ' for a Sonar project ' + sonarProject.name + ' for project ID ' + idProject);
		}

		const body = {
			'idProject': idProject,
			'sonarProject': {'key': sonarProject.key, 'name': sonarProject.name}
		};

		return this.httpClient
			.post<Boolean>(this.backendSetupService.url() + '/project/sonar/' + action, body, httpOptions)
			.pipe(take(1));
	}

	/**
	 * Add a topic to the audit in the current project.
	 * @param idTopic the topic identifier
	 */
	addAuditTopic(idTopic: number): Observable<Boolean> {
		return this.handleActionAudit$(this.project.id, new AuditTopic(idTopic, 0, 5), 'saveTopic');
	}

	/**
	 * Remove a topic from the audit in the current project.
	 * @param idTopic the topic identifier
	 */
	removeAuditTopic(idTopic: number): Observable<Boolean> {
		return this.handleActionAudit$(this.project.id, new AuditTopic(idTopic, 0, 5), 'removeTopic');
	}

	/**
	 * Execute an operation on the audit of a Techxhì project
	 * @param idProject the project identifier
	 * @param auditTopic the given auditTopic to be taken in account
	 * @param action the action to be executed on the audit collection
	 */
	private handleActionAudit$(idProject: number, auditTopic: AuditTopic, action: string): Observable<Boolean> {

		if (Constants.DEBUG) {
			console.log ('Action ' + action + ' on topic id ' + auditTopic.idTopic + ' for project ID ' + idProject);
		}

		const body = {
			'idProject': idProject,
			'auditTopic': {
				'idTopic': auditTopic.idTopic,
				'evaluation': auditTopic.evaluation,
				'weight': auditTopic.weight,
				'report': auditTopic.report}
		};

		return this.httpClient
			.post<Boolean>(this.backendSetupService.url() + '/project/audit/' + action, body, httpOptions)
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
		const url = this.backendSetupService.url() + '/project/name/' + projectName;
		if (Constants.DEBUG) {
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
		return this.httpClient
			.post<any>(this.backendSetupService.url() + '/project/sunburst', body, httpOptions)
			.pipe(take(1));
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
	contributors(idProject: number): Observable<ContributorsDTO> {

		if (idProject === -1) {
			return EMPTY;
		}

		const url = this.backendSetupService.url() + '/project/contributors/' + idProject;
		if (Constants.DEBUG) {
			console.log('Retrieve the contributors for the project identifier ' + idProject);
		}
		return this.httpClient.get<ContributorsDTO>(url);
	}

	/**
	 * Reset the whole data connected to the dashboard generation.
	 */
	resetDashboard(id: number): Observable<string> {
		const url = this.backendSetupService.url() + '/project/resetDashboard/' + id;
		if (Constants.DEBUG) {
			console.log('Reset the dashboard data on URL ' + url);
		}
		return this.httpClient.get<string>(url, httpOptions);
	}

	/**
	 * Test a connection to GIT on server, in order to validate the connection settings.
	 */
	testConnection(id: number): Observable<boolean> {
		const url = this.backendSetupService.url() + '/project/test/' + id;
		if (Constants.DEBUG) {
			console.log('Testing the connection settings on URL ' + url);
		}
		return this.httpClient.get<boolean>(url, httpOptions);
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
			.subscribe(doneAndOk => {
				if (doneAndOk) {
					this.messageService.info('Libraries detected have been saved');
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
					throw new Error('Unknown risk level ' + risk);
				}
			}
	}

	/**
	* Save the file statistics for a project.
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param filesStats the language file statistics retrieved from the Sonar instance.
	*/
	saveFilesStats(idProject: number, key: string, filesStats: FilesStats[]): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Save the files stats');
			console.log ('idProject', idProject);
			console.log ('key', key);
			filesStats.forEach(fs => console.log (fs.language, fs.numberOfFiles));
			console.groupEnd();
		}
		const body = { idProject: idProject, sonarProjectKey: key, filesStats: filesStats };
		return this.httpClient.post<Boolean>(this.backendSetupService.url() + '/project/sonar/files-stats', body, httpOptions);
	}

	/**
	* Save the file statistics for a project.
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param filesStats the language file statistics retrieved from the Sonar instance.
	*/
	saveMetricValues(idProject: number, key: string, metricValues: ProjectSonarMetricValue[]): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Save the metric values for Sonar entry %s project identifier %d', key, idProject);
			metricValues.forEach(mv => console.log ('Saving %s %d %d', mv.key, mv.weight, mv.value));
			console.groupEnd();
		}
		const body = { idProject: idProject, sonarKey: key, metricValues: metricValues };
		return this.httpClient.post<Boolean>(this.backendSetupService.url() + '/project/sonar/saveMetricValues', body, httpOptions);
	}

	/**
	 * Select and retrieve the SonarProject.
	 * @param project the given project
	 * @param sonarKey the key of the Sonar project.
	 * @returns the Sonar project for the given key
	 */
	getSonarProject(project: Project, sonarKey: String): SonarProject {
		return project.sonarProjects.find(sonarP => (sonarKey === sonarP.key));
	}

	/**
	 * Load the Sonar project for the given parameter.
	 * @param project the current project
	 * @param sonarKey the key of the Sonar project
	 */
	loadSonarProject (project: Project, sonarKey: string): Observable<SonarProject> {
		return this.httpClient.get<SonarProject>(
			this.backendSetupService.url() + '/project/sonar/load/' + project.id + '/' + sonarKey, httpOptions)
			.pipe(tap(
				(sonarProject: SonarProject) => {
					if (Constants.DEBUG) {
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
	getProjectSonarMetricValue(project: Project, sonarKey: String, metricKey): ProjectSonarMetricValue {
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

		sonarService.loadSonarComponentMeasures$(
				project,
				sonarKey,
				metricValues.map(psmv => psmv.key))
			.subscribe((measures: ResponseComponentMeasures) => {
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
				this.saveMetricValues(project.id, sonarKey, metricValues)
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
						}});
			});
	}

	/**
	* Save the evaluation for a project.
	* @param idProject the given project identifier
	* @param key the Sonar key from where the stats are coming from
	* @param evaluation the evaluation processed for this Sonar project
	* @param totalNumberLinesofCode the number of lines of code detected for this Sonar project
	*/
	saveSonarEvaluation(idProject: number, key: string, evaluation: number, totalNumberLinesOfCode: number): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Saving the evaluation for Sonar entry %s project identifier %d', key, idProject);
			console.log ('Evaluation obtained', evaluation);
			console.log ('Total number of lines of code', totalNumberLinesOfCode);
			console.groupEnd();
		}
		const body = { idProject: idProject, sonarKey: key,
			sonarEvaluation: {evaluation: evaluation, totalNumberLinesOfCode: totalNumberLinesOfCode}};
		return this.httpClient.post<Boolean>(this.backendSetupService.url() + '/project/sonar/saveEvaluation', body, httpOptions);
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idProject the given project identifier
	* @param idTopic the topic identifier
	* @param evaluation the evaluation given for that topic
	*/
	saveAuditTopicEvaluation$(idProject: number, idTopic: number, evaluation: number): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed(
				'Saving the evaluation for the topic identified by %d, within the project identified by %d',
				idTopic, idProject);
			console.log ('Evaluation given', evaluation);
			console.groupEnd();
		}
		const auditTopic = new AuditTopic(idTopic, evaluation, null);
		return this.handleActionAudit$(idProject, auditTopic, 'saveEvaluation');
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idTopic the topic identifier
	* @param report the audit report given by the expert
	*/
	saveAuditTopicReport$(idTopic: number, report: string): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed(
				'Saving the audit report for the topic identified by %d, within the project identified by %d',
				idTopic, this.project.id);
			console.log ('Report given', report);
			console.groupEnd();
		}
		const auditTopic = new AuditTopic(idTopic, 0, 0, report);
		return this.handleActionAudit$(this.project.id, auditTopic, 'saveReport');
	}

	/**
	* Save the evaluation for a topic involved in the audit.
	* @param idProject the given project identifier
	* @param auditTopics the topic identifier
	*/
	saveAuditTopicWeights$(idProject: number, auditTopics: AuditTopic[]): Observable<Boolean> {
		if (Constants.DEBUG) {
			console.groupCollapsed('Saving the weights for the topic identified by %d', idProject);
			auditTopics.forEach(element => console.log (element.idTopic, element.weight));
			console.groupEnd();
		}
		const body = { idProject: idProject, dataEnvelope: auditTopics};
		return this.httpClient
			.post<Boolean>(this.backendSetupService.url() + '/project/audit/saveWeights', body, httpOptions)
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
	 * Onboard a staff in the project.
	 * @param idProject Project identifier
	 * @param idStaff Staff identifier
	 */
	public onBoardStaffInProject(idProject: number, idStaff: number): void {
		this.httpClient
			.get<Boolean>(this.backendSetupService.url() + '/project/analysis/onboard/' + idProject + '/' + idStaff)
			.pipe(take(1))
			.subscribe(doneAndOk => {
				if (doneAndOk) {
					if (Constants.DEBUG) {
						console.log ('onBoard staff %d in project %d', idStaff, idProject);
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
		if (Constants.DEBUG) {
			console.log('Download the audit attachment file : ' + attachmentFile.fileName);
		}

		this.fileService.downloadFile(
			attachmentFile.fileName,
			this.backendSetupService.url() +
			'/project/audit/attachmentFile/' + idProject + '/' + idTopic + '/' + attachmentFile.idFile );
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
		if (Constants.DEBUG) {
			console.log('Delete the audit attachment file : ' + attachmentFile.fileName);
		}

		const body = { idProject: idProject, idTopic: idTopic, attachmentFile: {idFile: attachmentFile.idFile}};
		return this.httpClient
			.post<Boolean>(this.backendSetupService.url() + '/project/audit/removeAttachmentFile', body, httpOptions)
			.pipe(take(1));

	}

	/**
	 * Return the global mean __Sonar__ evaluation processed for all Sonar projects
	 * declared in the techzhì project.
	 */
	public calculateSonarEvaluation(): number {

		let globalSonarEvaluation = 0;

		if ((this.project.sonarProjects) && (this.project.sonarProjects.length > 0) && this.allSonarProjectsEvaluated()) {
			let totalEvalution = 0;
			let totalNumberLinesOfCode = 0;
			this.project.sonarProjects.forEach(sonarProject => {
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
	 */
	allSonarProjectsEvaluated() {
		let complete = true;
		this.project.sonarProjects.forEach(sonarP => {
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

		if ((!project) || !(project.id)) {
			console.log (from, 'Project is null dude!');
			return;
		}

		console.groupCollapsed(from, 'Project ' + project.id + ' ' + project.name);
		console.log('Global audit evaluation', project.auditEvaluation);
		Object.keys(project.audit).forEach(key => {
			console.log ('key: %s evaluation: %d weight: %d', key, project.audit[key].evaluation, project.audit[key].weight);
		});

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

		console.groupCollapsed(Object.keys(project.audit).length + ' audit topics');
		Object.keys(project.audit).forEach(key => {
			console.groupCollapsed('Audit topic %s', key);
			project.audit[key].attachmentList.forEach((element: AttachmentFile) => {
				console.log(element.fileName, element.label);
			});
			console.groupEnd();
		});
		console.groupEnd();

		console.groupEnd();
	}

	/**
	 * This function is proceeding 2 tasks :
	 *
	 * - It saves the Sonar URL associated with the project with the given identifier
	 * - It returns an `observable<Boolean>` representing the completion, or not, of the remote operation
	 * @param idProject the project identifier
	 * @param urlSonarServer the URL of the Sonar server
	 */
	public saveSonarUrl$(idProject: number, urlSonarServer: string): Observable<Boolean> {
		const body = { idProject: idProject, urlSonarServer: urlSonarServer};
		return this.httpClient
			.post<Boolean>(this.backendSetupService.url() + '/project/sonar/saveUrl', body, httpOptions)
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
}
