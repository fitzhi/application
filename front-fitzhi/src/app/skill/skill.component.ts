import { Component, OnInit, OnDestroy } from '@angular/core';
import { Constants } from '../constants';
import { Skill } from '../data/skill';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { MessageService } from '../interaction/message/message.service';
import { CinematicService } from '../service/cinematic.service';
import { ListSkillService } from './list-skill-service/list-skill.service';
import { SkillService } from './service/skill.service';
import { BaseComponent } from '../base/base.component';
import { traceOn } from '../global';
import { DetectionTemplate } from '../data/detection-template';
import { isNumber } from 'util';
import { isNumeric } from 'rxjs/util/isNumeric';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-skill',
	templateUrl: './skill.component.html',
	styleUrls: ['./skill.component.css']
})
export class SkillComponent extends BaseComponent implements OnInit, OnDestroy {

	skill: Skill;

	/**
	 * Skill form declaration.
	 */
	profileSkill: FormGroup;

	/**
	 * Id parameter received if any;
	 */
	private id: number;

	constructor(
		private cinematicService: CinematicService,
		private route: ActivatedRoute,
		public skillService: SkillService,
		private listSkillService: ListSkillService,
		private messageService: MessageService,
		private router: Router) {
		super();

		this.profileSkill = new FormGroup({
			title: new FormControl('', [Validators.required]),
			detectionType: new FormControl(''),
			pattern: new FormControl('')
		}, { validators: this.patternValidator.bind(this) });

		this.skillService.detectionTemplates$().subscribe ({
			next: rep => this.skillService.detectionTemplatesLoaded$.next(true)
		});

	}

	ngOnInit() {
		this.subscriptions.add(
			this.route.params.subscribe(params => {
				if (traceOn()) {
					console.log('params[\'id\'] = ' + params['id']);
				}
				if (params['id'] == null) {
					this.id = null;
				} else {
					this.id = + params['id']; // (+) converts string 'id' to a number
				}
				// Either we are in creation mode, and we create therefore an empty skill
				// Or we load the skill from the back-end.
				this.skill = new Skill();
				if (this.id !== null) {
					this.subscriptions.add(
						this.listSkillService.getSkill$(this.id).subscribe(
							(skill: Skill) => {
								this.skill = skill;
								this.profileSkill.get('title').setValue(skill.title);
								if (skill.detectionTemplate) {
									this.profileSkill.get('detectionType').setValue(skill.detectionTemplate.detectionType);
									this.profileSkill.get('pattern').setValue(skill.detectionTemplate.pattern);
								}
							},
							error => {
								if (error.status === 404) {
									if (traceOn()) {
										console.log('404 : cannot find a skill for the id ' + this.id);
									}
									this.messageService.error('There is no skill for id ' + this.id);
									this.skill = new Skill();
								} else {
									console.error(error.message);
								}
							},
							() => {
								if (this.skill.id === 0) {
									console.log('No skill found for the id ' + this.id);
								}
								if (traceOn()) {
									console.log('Loading comlete for id ' + this.id);
								}
							}
						));
				}
			}));
		this.cinematicService.setForm(Constants.SKILLS_CRUD, this.router.url);
	}

	/**
	 * Submit the change. 
	 * The current SKILL will be created, or updated. 
	 */
	onSubmit() {
		this.skillService.fillSkill(this.skill, this.profileSkill);
		if (traceOn()) {
			console.log('Saving the skill ' + this.skill.title + ' with id ' + this.skill.id);
		}
		this.skillService.save$(this.skill).pipe(take(1)).subscribe({
			next: skill => {
				this.messageService.info('The skill ' + skill.title + ' has been succesfully saved !');
				this.skill = skill;
			},
			error: error => {
				console.error(error);
				this.messageService.error(error.message);
			},
			complete: () => this.skillService.loadSkills()
		});
	}

	get title(): any {
		return this.profileSkill.get('title');
	}

	get detectionType(): any {
		return this.profileSkill.get('detectionType');
	}

	get pattern(): any {
		return this.profileSkill.get('pattern');
	}

	/**
	 * @param $event the event figuring that the detection template has changed.
	 */
	onDetectionTemplateChange($event) {
		if (!this.skill.detectionTemplate) {
			this.profileSkill.get('pattern').setValue('');
			return;
		}
		const type = this.profileSkill.get('detectionType').value;
		if (!isNumeric(type) || (type !== this.skill.detectionTemplate.detectionType)) {
			this.profileSkill.get('pattern').setValue('');
		}
	}

	/**
	 * This function is a validator for the field `pattern`.
	 * @param control the control `pattern`
	 */
	public patternValidator(control: FormControl) {
		if (!this.profileSkill) {
			return null;
		}

		const detectionType: string = this.profileSkill.get('detectionType').value;
		const pattern: string = this.profileSkill.get('pattern').value;
		if (isNumeric(detectionType) && (!pattern)) {
			return { 'patternRequired': true};
		}

		return null;
	}

	/**
     * Class of the button corresponding to the 3 possible states of the "Ok" button.
     */
	classOkButton() {
		return (this.profileSkill.invalid) ?
			'okButton okButtonInvalid' : 'okButton okButtonValid';
	}


	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}


}
