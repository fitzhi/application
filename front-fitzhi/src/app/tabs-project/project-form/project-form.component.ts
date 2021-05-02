import { Component, OnInit, Input, OnDestroy, AfterViewInit, EventEmitter, Output, AfterContentInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { take, map, catchError, switchMap, tap } from 'rxjs/operators';

import { ProjectService } from '../../service/project/project.service';
import { CinematicService } from '../../service/cinematic.service';

import { Project } from '../../data/project';
import { SonarProject } from '../../data/SonarProject';
import { Constants } from '../../constants';
import { SkillService } from '../../skill/service/skill.service';
import { MessageService } from '../../interaction/message/message.service';
import { BaseComponent } from '../../base/base.component';
import { Observable, of, BehaviorSubject, EMPTY, pipe } from 'rxjs';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { SonarService } from 'src/app/service/sonar.service';
import Tagify from '@yaireo/tagify';
import { MessageGravity } from 'src/app/interaction/message/message-gravity';
import { ReferentialService } from 'src/app/service/referential.service';
import { Skill } from 'src/app/data/skill';
import { traceOn } from 'src/app/global';
import { ProjectSkill } from '../../data/project-skill';
import { GitService } from 'src/app/service/git/git.service';
import { Repository } from 'src/app/data/git/repository';

/**
 * ProjectFormComponent
 */
@Component({
	selector: 'app-project-form',
	templateUrl: './project-form.component.html',
	styleUrls: ['./project-form.component.css']
})
export class ProjectFormComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * The risk might have changed due to the last dashboard calculation.
	 */
	@Input() risk$;

	public USER_PASSWORD_ACCESS = 1;
	public REMOTE_FILE_ACCESS = 2;
	public NO_USER_PASSWORD_ACCESS = 3;

	public SONAR = 1;
	public CODEFACTOR = 2;

	public colorOfRisk = 'transparent';

	profileProject = new FormGroup({
		projectName: new FormControl(''),
		urlSonarServer: new FormControl({ value: '', disabled: !this.projectService.project.active }),
		urlCodeFactorIO: new FormControl({ value: '', disabled: !this.projectService.project.active }),
		urlRepository: new FormControl(''),
		usernameRepository: new FormControl(''),
		passwordRepository: new FormControl(''),
		filename: new FormControl('')
	});

	sub: any;

	/**
	 * JS object handling the skills component.
	 */
	tagifySkills: Tagify = null;

	/**
	 * JS object handling the sonar projects component.
	 */
	tagifySonarProjects: Tagify;

	/**
	* Member variable linked to the connection settings toggle.
	*/
	public connection_settings: string;

	/**
	* Member variable linked to the quality solution toggle.
	*/
	public code_quality_solution$ = new BehaviorSubject<number>(this.SONAR);

	/**
	 * Bound addSkill to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundAddSkill: any;

	/**
	 * Bound removeSkill to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundRemoveSkill: any;

	/**
	 * Bound addSonarProject to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundAddSonarProject: any;

	/**
	 * Bound removeSonarProject to the current active component.
	 * The goal of this bind is to access the member variables of this class, such as projet
	 */
	private boundRemoveSonarProject: any;

	/**
	 * Are we creating a new project ? or are we updating an existing one ?
	 */
	public creation = false;

	/**
	 * This boolean caracterize the fact that the Sonar server is reachable.
	 * We can add Sonar project to this internal project.
	 */
	public sonarProjectsLoaded = false;

	/**
	 * This event emitter will throw an error if the method.
	 */
	errorEmitter: EventEmitter<MessageGravity> = new EventEmitter<MessageGravity>();

	private THIS_TAB = Constants.PROJECT_IDX_TAB_FORM;

	/**
	 * This __behaviorSubject__ is emtting a **true** if the given codeFactor.io is unreachable.
	 */
	urlCodeFactorIOUnreachable$ = new BehaviorSubject<Boolean>(false);

	constructor(
		private cinematicService: CinematicService,
		private messageService: MessageService,
		public referentialService: ReferentialService,
		public skillService: SkillService,
		public projectService: ProjectService,
		public gitService: GitService,
		public sonarService: SonarService,
		private router: Router) {
		super();

		this.boundAddSkill = this.addSkill.bind(this);
		this.boundRemoveSkill = this.removeSkill.bind(this);

		this.boundAddSonarProject = this.addSonarProject.bind(this);
		this.boundRemoveSonarProject = this.removeSonarProject.bind(this);

	}

	ngOnInit() {

		this.subscriptions.add(this.projectService.projectLoaded$
			.subscribe({
				next: loaded => {
					if (loaded) {
						this.loadForm();
						if (this.projectService.project.urlRepository) {
							this.projectService.loadBranches();
						}
					}
				}
			}
			));

		this.subscriptions.add(
			this.risk$.subscribe((risk: number) => {
				if (traceOn()) {
					console.log('Catching the risk', risk);
				}
				this.updateDotRiskColor(risk);
			}));

		this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);

		// We hide the help message and we clear the list of branches
		this.projectService.branches$.next([]);
		this.gitService.assistanceMessageGitBranches$.next(false);

	}

	ngAfterViewInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.ngAfterViewInitForm();
					} else {
						this.messageService.error('Referentials cannot be loaded!');
					}
				}
			}));

		this.subscriptions.add(this.projectService.projectLoaded$
			.subscribe({
				next: loaded => {
					// If true the project is loaded,
					// If false,
					//  either we fail to retrieve the data from the server.
					//  or, most probably, we enter in this form in CREATION mode (project.id = -1))
					if (loaded) {
						this.ngAfterViewInitSonarProjectsDeclaredInProject();
						this.ngAfterViewInitSkillsDeclaredInProject();
					} 
				}
			}));
	}



	/**
	 * This function create 2 JavasScript objects inside the form
	 * - the **Skill** Tagify component `tagifySkills`.
	 * - The **Sonar** Tagify component `tagifySonarProjects`.
	 *
	 * and after their creation, it fills the list or available data.
	 */
	ngAfterViewInitForm() {

		this.ngInitSonarAndTagify$().pipe(take(1),
			switchMap(doneAndOk => {
				if (doneAndOk) {
					return this.ngInitContentSonarAndTagify$();
				} else {
					return EMPTY;
				}
			}))
			.subscribe({
				next: (doneAndOk: boolean) => {
					// The Sonar projects have been retrieved, if any...
					// Asynchronous update to avoid ExpressionChangedAfterItHasBeenCheckedError
					setTimeout(() => {
						this.sonarProjectsLoaded = doneAndOk;
					}, 0);

					if ((doneAndOk) && (traceOn())) {
						console.log('ngAfterViewInitForm completed without error');
					}
				}
			});
	}

	loadForm() {
		if (traceOn()) {
			console.log('Loading the project data inside the form');
		}
		const project = this.projectService.project;
		this.profileProject.get('projectName').setValue(project.name);
		this.profileProject.get('urlCodeFactorIO').setValue(project.urlCodeFactorIO);
		// We postpone this 'setValue' to give time to the 'SELECT' html object to fill its content.
		setTimeout(() => {
			this.profileProject.get('urlSonarServer').setValue(project.urlSonarServer);
		}, 0);
		this.connection_settings = String(project.connectionSettings);
		this.profileProject.get('urlRepository').setValue(project.urlRepository);
		this.profileProject.get('usernameRepository').setValue(project.username);
		this.profileProject.get('passwordRepository').setValue(project.password);
		this.profileProject.get('filename').setValue(project.filename);
		// If a username has been setup, we test the connection.
		if ((project.username) && (project.username.length > 0)) {
			this.testConnectionSettings();
		}
		this.risk$.next(project.staffEvaluation);
	}

	ngInitSonarAndTagify$(): Observable<boolean> {

		let input = document.querySelector('textarea[name=skills]');
		if (!input) {
			throw new Error('INTERNAL ERROR : textarea[name=skills] is not found.');
		}
		if (!this.tagifySkills) {
			this.tagifySkills = new Tagify(input, {
				enforceWhitelist: true,
				whitelist: [],
				callbacks: {
					add: this.boundAddSkill,  // callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove: this.boundRemoveSkill   // callback when removing a tag
				}
			});
		}

		// If we re-enter in this method, the do not create twice the tagify component.
		if (!this.tagifySonarProjects) {
			input = document.querySelector('textarea[name=sonarProjects]');
			if (!input) {
				throw new Error('INTERNAL ERROR : textarea[name=sonarProjects] is not found.');
			}

			this.tagifySonarProjects = new Tagify(input, {
				enforceWhitelist: true,
				whitelist: [],
				callbacks: {
					add: this.boundAddSonarProject,
					// callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove: this.boundRemoveSonarProject
					// callback when removing a tag
				}
			});
		}
		return of(true);
	}

	ngInitContentSonarAndTagify$(): Observable<boolean> {

		return this.allSkills$()
			.pipe(
				take(1),
				tap(() => {
					if (traceOn()) {
						console.log('Initializing the skills inside the tagify component');
					}
				}),
				switchMap(skills => {
					this.initComponentTagifySkills(skills);
					return this.sonarProjectsLoaded$();
				}),
				catchError((error) => {
					console.error('Internal error : Skills are not retrieved from back-end', error);
					return this.sonarProjectsLoaded$();
				}));
				/*
			.pipe(
				take(1),
				switchMap(doneAndOk => {
					if (!doneAndOk && !this.creation) {
						this.messageService.warning('Cannot retrieve the declared applications in Sonar');
					}
					// Asynchronous update to avoid ExpressionChangedAfterItHasBeenCheckedError
					setTimeout(() => {
						this.sonarProjectsLoaded = doneAndOk;
					}, 0);
					return of(doneAndOk);
				}));
				*/
	}

	/**
	 * This method is called when the user quits the codeFactor.io url input after a change.
	 */
	public onCodeFactorUrlChange() {
		if (traceOn()) {
			console.log('Testing the url', this.profileProject.get('urlCodeFactorIO').value);
		}
		this.projectService.project.urlCodeFactorIO = this.profileProject.get('urlCodeFactorIO').value;
		if ((this.projectService.project.urlCodeFactorIO) || (this.projectService.project.urlCodeFactorIO.length === 0)) {
			this.urlCodeFactorIOUnreachable$.next(false);
		}
		this.projectService
			.testConnectionCodeFactorIO$()
			.subscribe({
				next: doneAndOk => {
					this.urlCodeFactorIOUnreachable$.next(!doneAndOk);
				}
			});
	}

	ngAfterViewInitSonarProjectsDeclaredInProject() {

		this.sonarProjectsLoaded$().pipe(take(1)).subscribe(doneAndOk => {
			if (doneAndOk) {
				if (this.projectService.project.sonarProjects) {
					if (this.projectService.project.sonarProjects.length > 0) {
						this.tagifySonarProjects.addTags(
							this.projectService.project.sonarProjects
								.map(function (sonarProject) { return sonarProject.name; }));
					}
				}
			}
			// Asynchronous update to avoid ExpressionChangedAfterItHasBeenCheckedError
			setTimeout(() => {
				this.sonarProjectsLoaded = doneAndOk;
			}, 0);
		});

	}

	ngAfterViewInitSkillsDeclaredInProject() {
		this.subscriptions.add(this.allSkills$()
			.subscribe(skills => {
				if (this.projectService.project.mapSkills) {
					for (const [idSkill, profilSkill] of this.projectService.project.mapSkills) {
						this.tagifySkills.addTags(this.skillService.title(idSkill));
					}
				}
			}));
	}

	/**
	 * Return an observable emitting an array containing all skills declared inside the applicaton.
	 */
	private allSkills$(): Observable<Skill[]> {
		return this.skillService.allSkillsLoaded$
			.pipe(switchMap(doneAndOk => {
				return doneAndOk ? of(this.skillService.allSkills) : EMPTY;
			}));
	}

	/**
	 * Initialize the tagify component with an array of skills.
	 * @param skills array of Skill.
	 */
	private initComponentTagifySkills(skills: Skill[]) {
		this.tagifySkills.settings.whitelist = [];
		skills.map(function (skill) { return skill.title; }).forEach(element => {
			this.tagifySkills.settings.whitelist.push(element);
		});
	}

	/**
	 * Load the accessible Sonar projects declared on the Sonar server.
	 * The returned observable emits a TRUE if the loading has been successful.
	 */
	sonarProjectsLoaded$(): Observable<boolean> {

		// If we re-enter in this method, the do not create twice the tagify component.
		if (!this.tagifySonarProjects) {
			const input = document.querySelector('textarea[name=sonarProjects]');

			this.tagifySonarProjects = new Tagify(input, {
				enforceWhitelist: true,
				whitelist: [],
				callbacks: {
					add: this.boundAddSonarProject,
					// callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove: this.boundRemoveSonarProject
					// callback when removing a tag
				}
			});
		}

		return this.projectService.projectLoaded$
			.pipe(switchMap(
				doneAndOk => {
					return (doneAndOk) ?
						this.sonarService.allSonarProjects$(this.projectService.project) :
						EMPTY;
				}))
			.pipe(
				tap(sonarProjects => {
					if (traceOn()) {
						console.log('Receiving ' + sonarProjects.length + ' Sonar projects');
					}
				}),
				map(sonarProjects => {

					// the Sonar server is considered as non 'loaded' if it's an empty server.
					// No project declared
					if (sonarProjects.length === 0) {
						return false;
					}

					this.tagifySonarProjects.settings.whitelist = [];
					sonarProjects.map(function (sonarProject) { return sonarProject.name; })
						.forEach(element => {
							this.tagifySonarProjects.settings.whitelist.push(element);
						});

					return true;
				}),
				catchError(err => {
					console.error(err);
					return of(false);
				}));
	}

	/**
	 * **End-user has change the Sonar servers selection.** Two possibilities :
	 * - either selected a new Sonar server
	 * - deselected the current one
	 * @param $event the event propagated from the HTML `select` element
	 */
	onUrlSonarServerChange($event) {

		// The project has not already been saved. We cannot update an existing record.
		if (this.projectService.project.id === -1) {
			return;
		}

		const urlSonarServer = this.profileProject.get('urlSonarServer').value;
		if (urlSonarServer !== this.projectService.project.urlSonarServer) {
			if (traceOn()) {
				console.log('Sonar URL has changed from %s to %s',
					(this.projectService.project.urlSonarServer) ? this.projectService.project.urlSonarServer : 'none',
					urlSonarServer);
			}
			this.projectService.saveSonarUrl$(this.projectService.project.id, urlSonarServer)
				.subscribe(doneAndOk => {
					if (doneAndOk) {
						this.messageService.success('Saved the URL ' + urlSonarServer + ' for the project ' + this.projectService.project.name);
						this.projectService.project.urlSonarServer = urlSonarServer;
						this.projectService.project.sonarProjects = [];
						this.projectService.projectLoaded$.next(true);
					} else {
						this.messageService.error('Failed to save the URL ' + urlSonarServer + ' for the project ' + this.projectService.project.name);
					}
				});
		}
	}

	/**
	 * @param $event End-user has selected a quality solution.
	 */
	onQualitySolutionChange($event) {
		if (traceOn()) {
			console.log('onQualitySolutionChange', $event);
		}
		this.code_quality_solution$.next(Number($event));
	}

	/**
	 * Add a skill inside the project.
	 * @param event ADD event fired by the tagify component.
	 */
	addSkill(event: CustomEvent) {

		if ((!this.projectService.project.id) || (this.projectService.project.id === -1)) {
			if (traceOn()) {
				console.log ('Adding a skill is impossible for an unregistered project.');
			}
			this.messageService.error('Adding a skill is impossible for an unregistered project!');
			return;
		}

		const idSkill = this.skillService.id(event.detail.data.value);
		if (idSkill === -1) {
			console.log('SEVERE ERROR : Unregistered skill', event.detail.data.value);
		}

		// This skills is already registered for this project.
		if (this.projectService.project.mapSkills.has(idSkill)) {
			return;
		}

		if (traceOn()) {
			console.log('Adding the skill', event.detail.data.value);
		}

		this.projectService.project.mapSkills.set(idSkill, new ProjectSkill(idSkill, 0, 0));

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.projectService.project.id)  {
			this.updateSkill(this.projectService.project.id, idSkill, this.projectService.addSkill.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Remove a skill from the project.
	 * @param event ADD event fired by the tagify component.
	 */
	removeSkill(event: CustomEvent) {

		const idSkill = this.skillService.id(event.detail.data.value);
		if (idSkill === -1) {
			console.log('SEVERE ERROR : Unknown skill %s', event.detail.data.value);
		}

		// This skills is NOT already registered for this project.
		if (!this.projectService.project.mapSkills.has(idSkill)) {
			console.log('SEVERE ERROR : Unregistered skill %s for the project %s',
				event.detail.data.value,
				this.projectService.project.name);
			return;
		}

		if (traceOn()) {
			console.log('Removing the skill', event.detail.data.value);
		}

		this.projectService.project.mapSkills.delete(idSkill);

		// We have already loaded or saved the project, so we can remove each new skill one by one.
		if (this.projectService.project.id) {
			this.updateSkill(this.projectService.project.id, idSkill, this.projectService.delSkill.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Update a skill inside a project. This might be an addition or a removal.
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 * @param callback the callback function, which might be **projectService.addSkill** or **projectService.delSkill**
	 */
	updateSkill(idProject: number, idSkill: number,
		callback: (idProject: number, idSkill: number) => Observable<BooleanDTO>) {
		callback(idProject, idSkill)
			.subscribe({
				next: result => {
					if (!result) {
						this.messageService.error(result.message);
					} else {
						this.projectService.actualizeProject(idProject);
					}
				},
				error: responseInError => {
					if (traceOn()) {
						console.log('Error ' + responseInError.error.code + ' ' + responseInError.error.message);
					}
					this.messageService.error(responseInError.error.message);
				}
			});
	}

	/**
	 * Update a sonar project linked to the application project. This might be an addition or a removal.
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project to update
	 * @param callback the callback function, which might be projectService.addSonarProject or projectService.delSonarProject
	 * @param postCallback the post callback function, which will be executed most probably after the callback "projectService.addSonarProject"
	 */
	updateSonarProject(idProject: number, sonarProject: SonarProject,
		callback: (idProject: number, sonarProject: SonarProject) => Observable<BooleanDTO>,
		postCallbackTreatment?: (idProject: number, sonarProject: SonarProject) => void) {
		callback(idProject, sonarProject)
			.subscribe(doneAndOk => {
				if (!doneAndOk) {
					this.messageService.error(doneAndOk.message);
				} else {
					if (postCallbackTreatment) {
						postCallbackTreatment(idProject, sonarProject);
					}
				}
			},
				response_in_error => {
					if (traceOn()) {
						console.log('Error ' + response_in_error);
					}
				});
	}

	/**
	 * Log the skills of the current project in DEBUG mode.
	 */
	logProjectSkills() {
		if (traceOn()) {
			console.groupCollapsed('list of skills for project ' + this.projectService.project.name);
			for (const [idSkill, profilSkill] of this.projectService.project.mapSkills) {
				console.log(idSkill, this.skillService.title(idSkill));
			}
			console.groupEnd();
		}
	}

	/**
	 * Add an entry declared in Sonar for this project.
	 * @param event ADD event fired by the tagify component.
	 */
	addSonarProject(event: CustomEvent) {
		if (traceOn()) {
			console.log('Adding the Sonar project entry', event.detail.data.value);
		}

		// This sonar project is already associated tp this project.
		if ((this.projectService.project.sonarProjects)
			&& (this.projectService.project.sonarProjects.find(sp => sp.name === event.detail.data.value))) {
			return;
		}

		const sonarComponent = this.sonarService.search(this.projectService.project, event.detail.data.value);
		if (!sonarComponent) {
			console.log('SEVERE ERROR : This Sonar project is unknown.', event.detail.data.value);
			return;
		}
		const sonarProject = new SonarProject();
		sonarProject.key = sonarComponent.key;
		sonarProject.name = sonarComponent.name;
		sonarProject.projectFilesStats = [];

		// For compatibility reason with the previous version.
		if (!this.projectService.project.sonarProjects) {
			this.projectService.project.sonarProjects = [];
		}

		this.projectService.project.sonarProjects.push(sonarProject);

		// We have already loaded or saved the project,
		// so we can add each new Sonar project as they appear, one by one.
		if (this.projectService.project.id) {
			this.updateSonarProject(this.projectService.project.id, sonarProject,
				this.projectService.addSonarProject.bind(this.projectService),
				this.reloadSonarProjectMetrics.bind(this));
		}

		// Log the resulting collection.
		this.logProjectSonarProjects();
	}

	reloadSonarProjectMetrics(idProject: number, sonarProject: SonarProject) {
		this.projectService
			.loadSonarProject(this.projectService.project, sonarProject.key)
			.pipe(take(1))
			.subscribe((sp: SonarProject) => {
				sonarProject.projectSonarMetricValues = sp.projectSonarMetricValues;

				this.projectService.loadAndSaveEvaluations(
					this.sonarService,
					this.projectService.project,
					sp.key,
					sp.projectSonarMetricValues,
					this.errorEmitter);
				if (traceOn()) {
					this.projectService.dump(this.projectService.project, 'addSonarProject');
				}
			});
	}

	/**
	 * Log the skills of the current project in DEBUG mode.
	 */
	logProjectSonarProjects() {
		if (traceOn()) {
			console.groupCollapsed('list of sonar projects for project ' + this.projectService.project.name);
			this.projectService.project.sonarProjects.forEach(sp => console.log(sp.key + ' ' + sp.name));
			console.groupEnd();
		}
	}

	/**
	 * Remove teh reference of a Sonar project declared inside 'our project'.
	 * @param event ADD event fired by the tagify component.
	 */
	removeSonarProject(event: CustomEvent) {
		if (traceOn()) {
			console.log('Removing the Sonar project', event.detail.data.value);
		}

		// This Sonar project HAS TO BE registered inside the project.
		if ((!this.projectService.project.sonarProjects) || (this.projectService.project.sonarProjects.length === 0)) {
			console.log('SHOULD NOT PASS HERE : ' + this.projectService.project.name
				+ ' does not contain any Sonar project. So, we should not be able to remove one of them');
		}

		const sonarProject = this.projectService.project.sonarProjects.find(sp => sp.name === event.detail.data.value);
		if (!sonarProject) {
			console.log('SHOULD NOT PASS HERE : Cannot remove the Sonar project '
				+ event.detail.data.value + ' from project ' + this.projectService.project.name);
			return;
		}

		const indexOfSonarProject = this.projectService.project.sonarProjects.indexOf(sonarProject);
		if (traceOn()) {
			console.log('Index of the Sonar project ' + sonarProject.name, indexOfSonarProject);
		}
		this.projectService.project.sonarProjects.splice(indexOfSonarProject, 1);

		// We have already loaded or saved the project, so we can add each new Sonar project as they appear, one by one.
		if (this.projectService.project.id) {
			this.updateSonarProject(this.projectService.project.id, sonarProject, this.projectService.delSonarProject.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSonarProjects();
	}

	/**
	 * Update the color of the SVG circle figuring the technical risk of this project.
	 * @param risk the evaluated level of risk
	 */
	updateDotRiskColor(risk: number) {
		this.colorOfRisk = this.projectService.getRiskColor(risk);
		if (traceOn()) {
			console.log('Filling the staff dot with the color', this.colorOfRisk);
		}
	}

	/**
	 * Submit the change. The project will be created, or updated.
	 */
	onSubmit() {
		// We create a new project is necessary.
		if (!this.projectService.project) {
			this.projectService.project = new Project();
		}
		this.projectService.project.name = this.profileProject.get('projectName').value;
		this.projectService.project.urlSonarServer = this.profileProject.get('urlSonarServer').value;
		this.projectService.project.urlCodeFactorIO = this.profileProject.get('urlCodeFactorIO').value;
		switch (this.projectService.project.connectionSettings) {
			case this.USER_PASSWORD_ACCESS:
				this.projectService.project.urlRepository = this.profileProject.get('urlRepository').value;
				this.projectService.project.username = this.profileProject.get('usernameRepository').value;
				this.projectService.project.password = this.profileProject.get('passwordRepository').value;
				this.projectService.project.filename = '';
				break;
			case this.REMOTE_FILE_ACCESS:
				this.projectService.project.urlRepository = this.profileProject.get('urlRepository').value;
				this.projectService.project.username = '';
				this.projectService.project.password = '';
				this.projectService.project.filename = this.profileProject.get('filename').value;
				break;
			case this.NO_USER_PASSWORD_ACCESS:
				this.projectService.project.urlRepository = this.profileProject.get('urlRepository').value;
				this.projectService.project.username = '';
				this.projectService.project.password = '';
				this.projectService.project.filename = '';
				break;
		}
		if (traceOn()) {
			console.groupCollapsed('Saving the project');
			console.log(this.projectService.project);
			console.groupEnd();
		}

		if ((this.projectService.project.id) && (this.projectService.project.id !== Constants.UNKNOWN)) {
			this.projectService.updateCurrentProject();
		}
		if ((!this.projectService.project.id) || (this.projectService.project.id === Constants.UNKNOWN)) {
			this.projectService.createNewProject()
				.pipe(take(1))
				.subscribe(project => {
					this.projectService.project = project;
					if (!project.mapSkills) {
						project.mapSkills = new Map<number, ProjectSkill>();
					}
					//
					// If we were in creation (i.e. url = ".../project/"), we leave this mode.
					//
					this.creation = false;

					//
					// We update the array containing the collection of all projects.
					//
					this.projectService.actualizeProject(project.id);

					//
					// We broadcast the fact that a project has been found.
					//
					this.projectService.projectLoaded$.next(true);

					this.messageService.success('Project ' + this.projectService.project.name + '  saved !');

					this.testConnectionSettings();

					this.loadBranchesOnBackend();
				});
		}

	}

	/**
	 * This function is called when end-user change the url of the GIT repository.
	 * We clear the context.
	 */
	clearBranchesContext() {
		if (traceOn()) {
			console.log('Cleaning the GIT branches context');
		}
		this.projectService.branches$.next([]);
		this.gitService.assistanceMessageGitBranches$.next(true);
	}

	onUrlRepositoryChange($event: any) {

		const url = ($event.target) ? $event.target.value : null;

		// Empty URL, nothing to do
		if (!url) {
			return;
		}
		if (this.profileProject.get('urlRepository') !== url) {
			this.projectService.branches$.next(['master']);
			this.profileProject.get('urlRepository').setValue(url);
		}

		const apiUrl = this.gitService.generateUrlApiGithub(url);

		if (traceOn()) {
			console.log(
				'Leaving the field for the URL repository with value %s, replacing the value %s',
				url, this.projectService.project.urlRepository);
		}

		this.gitService.assistanceMessageGitBranches$.next(false);
		this.gitService.connect$(apiUrl)
			.pipe(
				take(1),
				switchMap((repository: Repository) => {
					if (repository) {
						return this.gitService.branches$(apiUrl + '/branches', repository.default_branch);
					} else {
						//
						// We cannot access directly the git repository.
						// We delegate that access to the back-end only important for the analysis
						//
						this.loadBranchesOnBackend();
						return EMPTY;
					}
				}),
				catchError(error => {
					if (traceOn()) {
						console.log('error', error);
					}
					return EMPTY;
				})
			).subscribe({
				next: (branches: string[]) => {
					if (branches) {
						this.projectService.branches$.next(branches);
					} else {
						// If we cannot retrieve the array of branches,
						// We delegate that access to the back-end only important for the analysis
						this.loadBranchesOnBackend();
					}
				}
			});
	}

	/**
	 * We load the branch name from the back-end.
	 *
	 * The project has to be already saved first.
	 */
	loadBranchesOnBackend() {
		// If this project has already been saved (i.e. the project.id > 0)
		if (this.projectService.project.id === -1) {
			this.projectService.branches$.next(['master']);
			this.messageService.info('You need to save, first, the project, to retrieve all available branches');
		} else {
			if (this.profileProject.get('urlRepository').value) {
				this.gitService.assistanceMessageGitBranches$.next(true);
				if (this.profileProject.get('urlRepository').value !== this.projectService.project.urlRepository) {
					this.projectService.loadBranches();
				}
			} else {
				if (traceOn()) {
					console.log ('We do not load the branches when repository url is empty.');
				}
			}
		}
	}

	/**
	 * Test the connection settings.
	 */
	private testConnectionSettings() {
		this.projectService
			.testConnection(this.projectService.project.id)
			.pipe(take(1))
			.subscribe((doneAndOk: boolean) => {
				if (!doneAndOk) {
					this.messageService.warning('Connection to GIT failed : Check your settings!');
				}
			});

	}
	public onConnectionSettingsChange(val: string) {
		this.projectService.project.connectionSettings = +val;
		switch (this.projectService.project.connectionSettings) {
			case this.USER_PASSWORD_ACCESS:
				this.profileProject.get('filename').setValue('');
				this.profileProject.get('urlRepository').setValue(this.profileProject.get('urlRepository').value);
				break;
			case this.REMOTE_FILE_ACCESS:
				this.profileProject.get('usernameRepository').setValue('');
				this.profileProject.get('password').setValue('');
				this.profileProject.get('urlRepository').setValue(this.profileProject.get('urlRepository').value);
				break;
			case this.NO_USER_PASSWORD_ACCESS:
				this.profileProject.get('filename').setValue('');
				if (this.profileProject.get('username')) {
					this.profileProject.get('username').setValue('');
					this.profileProject.get('password').setValue('');
				}
				this.profileProject.get('urlRepository').setValue(this.profileProject.get('urlRepository').value);
				break;
		}
	}

	public onBranchChange(branch: string) {
		if (traceOn()) {
			console.log('New branch has been chosen', branch);
		}
		this.projectService.project.branch = branch;
	}

	/**
	 * Did the user select the direct access connection settings (user/password).
	 */
	public noUserPasswordAccess() {
		if (!this.projectService.project) {
			return false;
		}

		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.projectService.project.connectionSettings === 'undefined') || (this.projectService.project.connectionSettings === 0)) {
			return false;
		}
		return (this.projectService.project.connectionSettings === this.NO_USER_PASSWORD_ACCESS);
	}

	/**
	 * Did the user select the direct access connection settings (user/password).
	 */
	public userPasswordAccess() {
		if (!this.projectService.project) {
			return false;
		}

		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.projectService.project.connectionSettings === 'undefined') || (this.projectService.project.connectionSettings === 0)) {
			return false;
		}
		return (this.projectService.project.connectionSettings === this.USER_PASSWORD_ACCESS);
	}

	/**
	 * Did the user select the undirect access. Indicating a remote file containing the connection settings.
	 */
	public remoteFileAccess() {
		if (!this.projectService.project) {
			return false;
		}
		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.projectService.project.connectionSettings === 'undefined') || (this.projectService.project.connectionSettings === 0)) {
			return false;
		}
		return (this.projectService.project.connectionSettings === this.REMOTE_FILE_ACCESS);
	}

	/**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
	classOkButton() {
		return ((this.profileProject.invalid) || (!this.projectService.project.active)) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

	/**
	 * This method receives the new tab to activate from e.g. the sunburst tab pane child
	 * (but it won't be the only one).
	 * @param tabIndex new tab to activate.
	 */
	public tabActivation(tabIndex: number) {
		if (traceOn()) {
			console.log('Selected index', Constants.TAB_TITLE[tabIndex]);
		}
	}

	get projectName(): any {
		return this.profileProject.get('projectName');
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
