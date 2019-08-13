import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { take, switchMap, catchError } from 'rxjs/operators';

import { ProjectService } from '../../service/project.service';
import { CinematicService } from '../../service/cinematic.service';

import { Project } from '../../data/project';
import { ProjectDTO } from '../../data/external/projectDTO';
import { Constants } from '../../constants';
import { LocalDataSource } from 'ng2-smart-table';
import { SkillService } from '../../service/skill.service';
import { MessageService } from '../../message/message.service';
import { BaseComponent } from '../../base/base.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { RiskLegend } from 'src/app/data/riskLegend';
import Tagify from '@yaireo/tagify';
import { getHeapStatistics } from 'v8';
import { Skill } from 'src/app/data/skill';
import { WebDriverLogger } from 'blocking-proxy/built/lib/webdriver_logger';

@Component({
	selector: 'app-project-form',
	templateUrl: './project-form.component.html',
	styleUrls: ['./project-form.component.css']
})
export class ProjectFormComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
	 * The project loaded in the parent component.
	 */
	@Input() project$;

	/**
	 * The risk might have changed due to the last dashboard calculation.
	 */
	@Input() risk$;

	public project: Project;


	public legends: RiskLegend[];
	public DIRECT_ACCESS = 1;
	public REMOTE_FILE_ACCESS = 2;

	sourceSkills = new LocalDataSource([]);

	settings_skills = Constants.SETTINGS_SKILL_SMARTTABLE;

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
	tagify: Tagify;

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

	constructor(
		private cinematicService: CinematicService,
		private messageService: MessageService,
		private skillService: SkillService,
		private projectService: ProjectService,
		private referentialService: ReferentialService,
		private router: Router) {
		super();

		this.subscriptions.add(
			this.referentialService.legends$.subscribe(legends => {
				this.legends = legends;
			}));

		this.boundAddSkill = this.addSkill.bind(this);
		this.boundRemoveSkill = this.removeSkill.bind(this);

}

	ngOnInit() {

		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				this.project = project;
				this.profileProject.get('projectName').setValue(project.name);
				this.connection_settings = String(this.project.connectionSettings);
				this.profileProject.get('urlRepository1').setValue(this.project.urlRepository);
				this.profileProject.get('urlRepository2').setValue(this.project.urlRepository);
				this.profileProject.get('username').setValue(this.project.username);
				this.profileProject.get('password').setValue(this.project.password);
				this.profileProject.get('filename').setValue(this.project.filename);
				this.sourceSkills.load(this.project.skills);

				setTimeout(() => this.updateDotRiskColor(this.project.risk));

				this.tagify.addTags(
					this.project.skills
					.map(function(skill) { return skill.title; }));
			}));

		this.subscriptions.add(
			this.risk$.subscribe((risk: number) => this.updateDotRiskColor(risk)));

		this.project = new Project();
		this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);
	}

	ngAfterViewInit() {
		const input = document.querySelector('textarea[name=skills]');

		this.tagify = new Tagify (input, {
			enforceWhitelist : true,
			whitelist        : [],
			callbacks        : {
				add    : this.boundAddSkill,  // callback when adding a tag, this callback is bound to the main component, instead of the function.
				remove : this.boundRemoveSkill   // callback when removing a tag
			}
		});

		this.skillService.allSkills$
			.subscribe (skills => {
				skills.map(function(skill) { return skill.title; }).forEach(element => {
					this.tagify.settings.whitelist.push(element);
				});
			});
	}

	/**
	 * Add a skill inside the project.
	 * @param event ADD event fired by the tagify component.
	 */
	addSkill(event: CustomEvent) {
		if (Constants.DEBUG) {
			console.log ('Adding the skill', event.detail.data.value);
		}

		// This skills is already registered for this project.
		if ( (this.project.skills !== undefined)
			&& (this.project.skills.find (sk => sk.title === event.detail.data.value) !== undefined)) {
			return;
		}

		const skill = this.skillService.search (event.detail.data.value);
		if (skill === undefined) {
			console.log ('SEVERE ERROR : Unregistered skill', event.detail.data.value);
			return;
		}

		this.project.skills.push(skill);

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
		if ( (this.project.skills === undefined) || (this.project.skills.length === 0)) {
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

		// Log the resulting collection.
		this.logProjectSkills();
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
	 * Update the color of the SCG circle figuring the technical risk of this project.
	 * @param levelOfRisk the evaluated level of risk
	 */
	updateDotRiskColor(levelOfRisk: number) {
		if (Constants.DEBUG) {
			console.log ('updateDotRiskColor for level of risk', levelOfRisk);
		}
		switch (levelOfRisk) {
			case -1:
				this.colorOfRisk = 'whiteSmoke';
				break;
			default:
				this.colorOfRisk = this.legends.find (legend => legend.level === levelOfRisk).color;
				if (Constants.DEBUG) {
					console.log ('the new DOT risk color', this.colorOfRisk);
				}
				break;
			}
	}

	/**
	 * Remove a skill from a project
	 */
	onConfirmRemoveSkillFromProject(event) {
		if (!this.checkProjectExist(event)) {
			event.confirm.reject();
			return;
		}
		if (window.confirm('Are you sure you want to remove the skill '
			+ event.data['title'] + ' from project ' +
			this.project.name
			+ '?')) {
			/*
			 * After the addition of a skill into a project, and before the reloadSkills has been completed,
			 * there is a very little delay with a skill without ID into the skills list.
			 */
			if (typeof event.data['id'] !== 'undefined') {
				this.subscriptions.add(
					this.projectService.removeSkill(this.project.id, event.data['id']).subscribe(
						(projectDTO: ProjectDTO) => {
							this.messageService.info('The project ' + projectDTO.project.name +
								' does not require anymore the skill ' + event.data.title);
							this.reloadSkills(this.project.id);
							event.confirm.resolve();
						},
						response_error => {
							if (Constants.DEBUG) {
								console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
							}
							this.reloadSkills(this.project.id);
							event.confirm.reject();
							this.messageService.error(response_error.error.message);
						}
					));
			}
		} else {
			event.confirm.reject();
		}
	}

	/**
	 * Test if the project exists, before adding a skill into a project.
	 * A project ID is required.
	 */
	checkProjectExist(event): boolean {
		if (this.project.id === null) {
			this.messageService.error('You cannot add, or update a skill of an unregistered project. '
				+ 'Please saved this new project first !');
			return false;
		} else {
			return true;
		}
	}

	onConfirmAddSkillToProject(event) {
		if (Constants.DEBUG) {
			console.log('onConfirmAddProjectSkill for event ' + event.newData.title);
		}
		if (this.checkProjectExist(event)) {
			this.subscriptions.add(
				this.projectService.addSkill(this.project.id, event.newData.title).subscribe(
					(projectDTO: ProjectDTO) => {
						this.messageService.info('The project ' + projectDTO.project.name +
							' requires from now the skill ' + event.newData.title);
						this.reloadSkills(this.project.id);
						event.confirm.resolve();
					},
					response_error => {
						if (Constants.DEBUG) {
							console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
						}
						this.reloadSkills(this.project.id);
						this.messageService.error(response_error.error.message);
						event.confirm.reject();
					}
				));
		} else {
			event.confirm.reject();
		}
	}

	onConfirmEditSkillIntoProject(event) {
		if (Constants.DEBUG) {
			console.log('onConfirmEditProjectSkill for skill from ' + event.data.title + ' to ' + event.newData.title);
		}
		if (this.checkProjectExist(event)) {
			this.subscriptions.add(
				this.skillService.lookup(event.newData.title).subscribe(
					() => {
						this.subscriptions.add(
							this.projectService.changeSkill(this.project.id, event.data.title, event.newData.title).subscribe(
								(projectDTO: ProjectDTO) => {
									this.messageService.info(projectDTO.project.name + ' ' +
										' has now the skill ' + event.newData.title);
									this.reloadSkills(this.project.id);
									event.confirm.resolve();
								},
								response_error => {
									if (Constants.DEBUG) {
										console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
									}
									this.reloadSkills(this.project.id);
									event.confirm.reject();
									this.messageService.error(response_error.error.message);
								}
							));
					},
					response_error => {
						if (Constants.DEBUG) {
							console.error(response_error);
						}
						this.messageService.error(response_error.error.message);
						event.confirm.reject();
					}));
		} else {
			event.confirm.reject();
		}
	}

	/*
	* Refresh the skills of the project.
	*/
	reloadSkills(idProject: number): void {
		if (Constants.DEBUG) {
			console.log('Refreshing skills for the project\'s id ' + idProject);
		}
		this.subscriptions.add(
			this.projectService.loadSkills(idProject).subscribe(
				skills => this.sourceSkills.load(skills),
				error => console.log(error),
			));
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
			console.log('saving the project ');
			console.log(this.project);
		}
		this.projectService.save(this.project).pipe(take(1)).subscribe(
			project => {
				this.project = project;
				this.messageService.info('Project ' + this.project.name + '  saved !');
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
		if (typeof this.project === 'undefined') {
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
		if (typeof this.project === 'undefined') {
			return false;
		}
		// No choice have been made yet. We are the 2 pannels.
		if ((typeof this.project.connectionSettings === 'undefined') || (this.project.connectionSettings === 0)) {
			return true;
		}
		return (this.project.connectionSettings === this.REMOTE_FILE_ACCESS);
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


	evaluationStyle () {
		return { 'fill': this.colorOfRisk };
	}

}
