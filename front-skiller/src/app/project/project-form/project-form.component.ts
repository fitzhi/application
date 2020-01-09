import { Component, OnInit, Input, OnDestroy, AfterViewInit, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { take, map, catchError, switchMap } from 'rxjs/operators';

import { ProjectService } from '../../service/project.service';
import { CinematicService } from '../../service/cinematic.service';

import { Project } from '../../data/project';
import { SonarProject } from '../../data/SonarProject';
import { Constants } from '../../constants';
import { SkillService } from '../../service/skill.service';
import { MessageService } from '../../message/message.service';
import { BaseComponent } from '../../base/base.component';
import { Observable, of, BehaviorSubject, EMPTY, pipe } from 'rxjs';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { SonarService } from 'src/app/service/sonar.service';
import Tagify from '@yaireo/tagify';
import { MessageGravity } from 'src/app/message/message-gravity';
import { ReferentialService } from 'src/app/service/referential.service';

@Component({
	selector: 'app-project-form',
	templateUrl: './project-form.component.html',
	styleUrls: ['./project-form.component.css']
})
export class ProjectFormComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * The project loaded in the parent component.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * The risk might have changed due to the last dashboard calculation.
	 */
	@Input() risk$;

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	public project: Project;

	public DIRECT_ACCESS = 1;
	public REMOTE_FILE_ACCESS = 2;

	public colorOfRisk = 'transparent';

	profileProject = new FormGroup({
		projectName: new FormControl(''),
		urlRepository1: new FormControl(''),
		urlRepository2: new FormControl(''),
		username: new FormControl(''),
		password: new FormControl(''),
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
	 * Is Sonar accessibke ?
	 */
	private sonarIsAccessible = false;

	/**
	 * Are we creating a new project ? or are we updating an existing one ?
	 */
	private creation = false;

	/**
	 * This boolean caracterize the fact that the Sonar server is reachable.
	 * We can add Sonar project to this internal project.
	 */
	private sonarProjectsLoaded = false;

	/**
	 * This event emitter will throw an error if the method.
	 */
	errorEmitter: EventEmitter<MessageGravity> = new EventEmitter<MessageGravity>();


	constructor(
		private cinematicService: CinematicService,
		private referentialService: ReferentialService,
		private messageService: MessageService,
		private skillService: SkillService,
		private projectService: ProjectService,
		private sonarService: SonarService,
		private route: ActivatedRoute,
		private router: Router) {
		super();

		this.boundAddSkill = this.addSkill.bind(this);
		this.boundRemoveSkill = this.removeSkill.bind(this);

		this.boundAddSonarProject = this.addSonarProject.bind(this);
		this.boundRemoveSonarProject = this.removeSonarProject.bind(this);

		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$.subscribe
				(sonarIsAccessible => this.sonarIsAccessible = sonarIsAccessible)
		);
	}

	ngOnInit() {

		this.route.params.pipe(take(1)).subscribe(params => {
			if (params['id'] == null) {
				if (Constants.DEBUG) {
					console.log('No project ID is given. We are in creation mode');
				}
				this.creation = true;
			}
		});

		this.subscriptions.add(
			this.risk$.subscribe((risk: number) => {
				if (Constants.DEBUG) {
					console.log ('Catching the risk', risk);
				}
				this.updateDotRiskColor(risk);
		}));

		this.project = new Project();
		this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);

	}

	ngAfterViewInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$.subscribe(
				(doneAndOk: boolean) => {
					if (doneAndOk) {
						this.ngAfterViewInitForm();
					}}));
	}

	ngAfterViewInitForm() {

		this.ngInitSonarAndTagify$()
			.pipe(
				take(1),
				switchMap(doneAndOk => {
					if (doneAndOk) {
						return this.ngInitContentSonarAndTagify$();
					} else {
						return of(EMPTY);
					}
				}))
			.pipe(
				take(1),
				switchMap(doneAndOk => {
					return this.project$;
				}))
			.subscribe(
				(project: Project) => {
					if (Constants.DEBUG) {
						this.projectService.dump(project, 'ngAfterViewInitForm');
					}

					// The behaviorSubject project$ is initialized with a null.
					if (!project) {
						return;
					}
					//
					// We postpone the Project updates to avoid the warning
					// ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked.
					//
					setTimeout(() => {
						this.project = project;
						this.testConnectionSettings();
						this.profileProject.get('projectName').setValue(project.name);
						this.connection_settings = String(this.project.connectionSettings);
						this.profileProject.get('urlRepository1').setValue(this.project.urlRepository);
						this.profileProject.get('urlRepository2').setValue(this.project.urlRepository);
						this.profileProject.get('username').setValue(this.project.username);
						this.profileProject.get('password').setValue(this.project.password);
						this.profileProject.get('filename').setValue(this.project.filename);
						this.ngInitSonarProjectsDeclaredInProject();
						this.ngInitSkillsDeclaredInProject();
						this.risk$.next(this.project.staffEvaluation);
					}, 0);
			});
	}


	ngInitSonarAndTagify$(): Observable<boolean> {

		let input = document.querySelector('textarea[name=skills]');
		if (!input) {
			throw new Error('INTERNAL ERROR : textarea[name=skills] is not found.');
		}
		if (!this.tagifySkills) {
			this.tagifySkills = new Tagify (input, {
				enforceWhitelist : true,
				whitelist        : [],
				callbacks        : {
					add    : this.boundAddSkill,  // callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove : this.boundRemoveSkill   // callback when removing a tag
				}
			});
		}

		// If we re-enter in this method, the do not create twice the tagify component.
		if (!this.tagifySonarProjects) {
			input = document.querySelector('textarea[name=sonarProjects]');
			if (!input) {
				throw new Error('INTERNAL ERROR : textarea[name=sonarProjects] is not found.');
			}

			this.tagifySonarProjects = new Tagify (input, {
				enforceWhitelist : true,
				whitelist        : [],
				callbacks        : {
					add    : this.boundAddSonarProject,
						// callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove : this.boundRemoveSonarProject
						// callback when removing a tag
				}
			});
		}

		return of(true);
	}

	ngInitContentSonarAndTagify$(): Observable<boolean> {

		return this.skillService.allSkills$
			.pipe(
				take(1),
				switchMap (skills => {
					this.tagifySkills.settings.whitelist = [];
					skills.map(function(skill) { return skill.title; }).forEach(element => {
						this.tagifySkills.settings.whitelist.push(element);
					});
					if (Constants.DEBUG) {
						console.log ('Initializing the skills inside the tagify component');
					}
					return this.sonarProjectsLoaded$();
			}), catchError ( (error) => {
				console.error('Internal error : Skills are not retrieved from back-end', error);
				return this.sonarProjectsLoaded$();
			}))
			.pipe(
				take(1),
				switchMap (doneAndOk => {
					if (!doneAndOk && !this.creation) {
						this.messageService.warning('Cannot retrieve the declared applications in Sonar');
					}
					// Asynchronous update to avoid ExpressionChangedAfterItHasBeenCheckedError
					setTimeout(() => {
						this.sonarProjectsLoaded = doneAndOk;
					}, 0);
					return of(doneAndOk);
			}));
	}

	ngInitSonarProjectsDeclaredInProject() {

		this.sonarProjectsLoaded$().pipe(take(1)).subscribe (doneAndOk => {
			if (doneAndOk) {
				if (this.project.sonarProjects) {
					if (this.project.sonarProjects.length > 0) {
						this.tagifySonarProjects.addTags(
							this.project.sonarProjects
							.map(function(sonarProject) { return sonarProject.name; }));
					}
				}
			}
			// Asynchronous update to avoid ExpressionChangedAfterItHasBeenCheckedError
			setTimeout(() => {
				this.sonarProjectsLoaded = doneAndOk;
			}, 0);
		});

	}

	ngInitSkillsDeclaredInProject() {

		this.skillService.allSkills$
			.pipe(take(1))
			.subscribe (skills => {
				if ( (this.project) && (this.project.skills) ) {
					if (this.project.skills.length > 0) {
						this.tagifySkills.addTags(
							this.project.skills
							.map(function(skill) { return skill.title; }));
					}
				}
			});
	}

	/**
	 * Load the accessible Sonar projects declared on the Sonar project.
	 * The returned observable emits a TRUE if the loading has been successful.
	 */
	sonarProjectsLoaded$(): Observable<boolean> {

		// If we re-enter in this method, the do not create twice the tagify component.
		if (!this.tagifySonarProjects) {
			const input = document.querySelector('textarea[name=sonarProjects]');

			this.tagifySonarProjects = new Tagify (input, {
				enforceWhitelist : true,
				whitelist        : [],
				callbacks        : {
					add    : this.boundAddSonarProject,
						// callback when adding a tag, this callback is bound to the main component, instead of the function.
					remove : this.boundRemoveSonarProject
						// callback when removing a tag
				}
			});
		}

		return this.sonarService.allSonarProjects$
			.pipe (
				map (sonarProjects => {
					if (Constants.DEBUG) {
						console.log ('Receiving ' + sonarProjects.length + ' Sonar projects');
					}

					if (sonarProjects.length === 0) {
						return false;
					}

					this.tagifySonarProjects.settings.whitelist = [];
					sonarProjects.map(function(sonarProject) { return sonarProject.name; })
					.forEach(element => {
						this.tagifySonarProjects.settings.whitelist.push(element);
					});
					return true;
				}),
				catchError(err =>  of(false)));
	}

	/**
	 * Add a skill inside the project.
	 * @param event ADD event fired by the tagify component.
	 */
	addSkill(event: CustomEvent) {

		// This skills is already registered for this project.
		if ( (this.project.skills)
			&& (this.project.skills.find (sk => sk.title === event.detail.data.value))) {
			return;
		}

		if (Constants.DEBUG) {
			console.log ('Adding the skill', event.detail.data.value);
		}

		const skill = this.skillService.search (event.detail.data.value);
		if (skill === undefined) {
			console.log ('SEVERE ERROR : Unregistered skill', event.detail.data.value);
			return;
		}

		this.project.skills.push(skill);

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.project.id) {
			this.updateSkill(this.project.id, skill.id, this.projectService.addSkill.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Remove a skill from the project.
	 * @param event ADD event fired by the tagify component.
	 */
	removeSkill(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Removing the skill', event.detail.data.value);
		}

		// This skill HAS TO BE registered inside the project.
		if ( (!this.project.skills) || (this.project.skills.length === 0)) {
			console.log ('SHOULD NOT PASS HERE : ' + this.project.name
			+ ' does not contain any skill. So, we should not be able to remove one');
		}

		const skill = this.project.skills.find (sk => sk.title === event.detail.data.value);
		if (skill === undefined) {
			console.log ('SHOULD NOT PASS HERE : Cannot remove the skill '
			+ event.detail.data.value + ' from project ' + this.project.name);
			return;
		}

		const indexOfSkill = this.project.skills.indexOf(skill);
		if (Constants.DEBUG) {
			console.log ('Index of the skill ' + skill.title, indexOfSkill);
		}
		this.project.skills.splice(indexOfSkill, 1);

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.project.id) {
			this.updateSkill(this.project.id, skill.id, this.projectService.delSkill.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Update a skill inside a project. This might be an addition or a removal.
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 * @param callback the callback function, which might be projectService.addSkill or projectService.delSkill
	 */
	updateSkill(idProject: number, idSkill:  number,
		callback: (idProject: number, idSkill:  number) => Observable<BooleanDTO>) {
		callback(idProject, idSkill)
		.subscribe (result => {
			if (!result) {
				this.messageService.error (result.message);
			}
		},
		response_in_error => {
			if (Constants.DEBUG) {
				console.log('Error ' + response_in_error.error.code + ' ' + response_in_error.error.message);
			}
			this.messageService.error(response_in_error.error.message);
		});
	}

	/**
	 * Update a sonar project linked to the application project. This might be an addition or a removal.
	 * @param idProject the project identifier
	 * @param sonarProject the sonar project to update
	 * @param callback the callback function, which might be projectService.addSonarProject or projectService.delSonarProject
	 * @param postCallback the post callback function, which will be executed most probably after the callback "projectService.addSonarProject"
	 */
	updateSonarProject(idProject: number, sonarProject:  SonarProject,
		callback: (idProject: number, sonarProject:  SonarProject) => Observable<BooleanDTO>,
		postCallbackTreatment?: (idProject: number, sonarProject:  SonarProject) => void) {
		callback(idProject, sonarProject)
		.subscribe (doneAndOk => {
			if (!doneAndOk) {
				this.messageService.error (doneAndOk.message);
			} else {
				if (postCallbackTreatment) {
					postCallbackTreatment(idProject, sonarProject);
				}
			}
		},
		response_in_error => {
			if (Constants.DEBUG) {
				console.log('Error ' + response_in_error);
			}
		});
	}

	/**
	 * Log the skills of the current project in DEBUG mode.
	 */
	logProjectSkills() {
		if (Constants.DEBUG) {
			console.log (this.project.skills);
			console.groupCollapsed ('list of skills for project ' + this.project.name);
			this.project.skills.forEach(sk => console.log (sk.id + ' ' +  sk.title));
			console.groupEnd();
		}
	}

	/**
	 * Add an entry declared in Sonar for this project.
	 * @param event ADD event fired by the tagify component.
	 */
	addSonarProject(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Adding the Sonar project entry', event.detail.data.value);
		}

		// This sonar project is already associated tp this project.
		if ( (this.project.sonarProjects)
			&& (this.project.sonarProjects.find (sp => sp.name === event.detail.data.value))) {
			return;
		}

		const sonarComponent = this.sonarService.search (event.detail.data.value);
		if (!sonarComponent) {
			console.log ('SEVERE ERROR : This Sonar project is unknown.', event.detail.data.value);
			return;
		}
		const sonarProject = new SonarProject();
		sonarProject.key = sonarComponent.key;
		sonarProject.name = sonarComponent.name;
		sonarProject.projectFilesStats = [];

		// For compatibility reason with the previsous version.
		if (!this.project.sonarProjects) {
			this.project.sonarProjects = [];
		}

		this.project.sonarProjects.push(sonarProject);

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.project.id) {
			this.updateSonarProject(this.project.id, sonarProject,
				this.projectService.addSonarProject.bind(this.projectService),
				this.reloadSonarProjectMetrics.bind(this));
		}

		// Log the resulting collection.
		this.logProjectSonarProjects();
	}

	reloadSonarProjectMetrics(idProject: number, sonarProject: SonarProject) {
		this.projectService
			.loadSonarProject(this.project, sonarProject.key)
			.pipe(take(1))
			.subscribe((sp: SonarProject) => {
				sonarProject.projectSonarMetricValues = sp.projectSonarMetricValues;

				this.projectService.loadAndSaveEvaluations(
					this.sonarService,
					this.project,
					sp.key,
					sp.projectSonarMetricValues,
					this.errorEmitter);
				this.projectService.dump(this.project, 'addSonarProject');
			});
	}

	/**
	 * Log the skills of the current project in DEBUG mode.
	 */
	logProjectSonarProjects() {
		if (Constants.DEBUG) {
			console.groupCollapsed ('list of sonar projects for project ' + this.project.name);
			this.project.sonarProjects.forEach(sp => console.log (sp.key + ' ' +  sp.name));
			console.groupEnd();
		}
	}

	/**
	 * Remove teh reference of a Sonar project declared inside 'our project'.
	 * @param event ADD event fired by the tagify component.
	 */
	removeSonarProject(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Removing the Sonar project', event.detail.data.value);
		}

		// This Sonar project HAS TO BE registered inside the project.
		if ( (!this.project.sonarProjects) || (this.project.sonarProjects.length === 0)) {
			console.log ('SHOULD NOT PASS HERE : ' + this.project.name
			+ ' does not contain any Sonar project. So, we should not be able to remove one of them');
		}

		const sonarProject = this.project.sonarProjects.find (sp => sp.name === event.detail.data.value);
		if (!sonarProject) {
			console.log ('SHOULD NOT PASS HERE : Cannot remove the Sonar project '
			+ event.detail.data.value + ' from project ' + this.project.name);
			return;
		}

		const indexOfSonarProject = this.project.sonarProjects.indexOf(sonarProject);
		if (Constants.DEBUG) {
			console.log ('Index of the Sonar project ' + sonarProject.name, indexOfSonarProject);
		}
		this.project.sonarProjects.splice(indexOfSonarProject, 1);

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.project.id) {
			this.updateSonarProject(this.project.id, sonarProject, this.projectService.delSonarProject.bind(this.projectService));
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
		if (Constants.DEBUG) {
			console.log ('Filling the staff dot with the color', this.colorOfRisk);
		}
	}

	/**
	 * Submit the change. The project will be created, or updated.
	 */
	onSubmit() {
		this.project.name = this.profileProject.get('projectName').value;
		switch (this.project.connectionSettings) {
			case this.DIRECT_ACCESS:
				this.project.urlRepository = this.profileProject.get('urlRepository1').value;
				this.project.username = this.profileProject.get('username').value;
				this.project.password = this.profileProject.get('password').value;
				this.project.filename = '';
				break;
			case this.REMOTE_FILE_ACCESS:
				this.project.urlRepository = this.profileProject.get('urlRepository2').value;
				this.project.username = '';
				this.project.password = '';
				this.project.filename = this.profileProject.get('filename').value;
				break;
		}
		if (Constants.DEBUG) {
			console.log('Saving the project ');
			console.log(this.project);
		}
		this.projectService.save(this.project).pipe(take(1)).subscribe(
			project => {
				this.project = project;

				// If we were in creation (i.e. url = ".../project/"), we leave this mode.
				this.creation = false;
				// We update the array containing the collection of all projects.
				this.projectService.updateProjectsCollection(project);
				// We broadcast the new project state.
				this.project$.next(this.project);

				this.messageService.success('Project ' + this.project.name + '  saved !');

				this.testConnectionSettings();
			});
	}

	/**
	 * Test the connection settings.
	 */
	private testConnectionSettings() {
		this.projectService
			.testConnection(this.project.id)
			.pipe(take(1))
			.subscribe((doneAndOk: boolean) => {
				if (!doneAndOk) {
					this.messageService.warning('Connection to GIT failed : Check your settings!');
				}
			});

	}
	public onConnectionSettingsChange(val: string) {
		this.project.connectionSettings = +val;
		switch (this.project.connectionSettings) {
			case this.DIRECT_ACCESS:
				this.profileProject.get('filename').setValue('');
				this.profileProject.get('urlRepository1').setValue(this.profileProject.get('urlRepository2').value);
				break;
			case this.REMOTE_FILE_ACCESS:
				this.profileProject.get('username').setValue('');
				this.profileProject.get('password').setValue('');
				this.profileProject.get('urlRepository2').setValue(this.profileProject.get('urlRepository1').value);
				break;
		}
	}

	/**
	 * Did the user select the direct access connection settings (user/password).
	 */
	public directAccess() {
		if (!this.project) {
			return true;
		}

		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.project.connectionSettings === 'undefined') || (this.project.connectionSettings === 0)) {
			return true;
		}
		return (this.project.connectionSettings === this.DIRECT_ACCESS);
	}

	/**
	 * Did the user select the undirect access. Indicating a remote file containing the connection settings.
	 */
	public undirectAccess() {
		if (!this.project) {
			return true;
		}
		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.project.connectionSettings === 'undefined') || (this.project.connectionSettings === 0)) {
			return true;
		}
		return (this.project.connectionSettings === this.REMOTE_FILE_ACCESS);
	}


	/**
	 * This method receives the nex tab to activate from here.
	 * @param tabIndex new tab to activate.
	 */
	public tabActivation (tabIndex: number) {
		this.tabActivationEmitter.next(tabIndex);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

	/**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
	classOkButton() {
		return (this.profileProject.invalid) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}

}
