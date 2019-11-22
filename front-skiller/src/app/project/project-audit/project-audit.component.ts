import { Component, OnInit, AfterViewInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Topic } from './table-categories/topic';
import { BehaviorSubject, Subject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { BaseComponent } from 'src/app/base/base.component';
import { Project } from 'src/app/data/project';
import { ReferentialService } from 'src/app/service/referential.service';
import { TopicProject } from './topic-project';
import { CinematicService } from 'src/app/service/cinematic.service';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	@Input() project$;

	/**
	 * This `boolean` control the `[hidden]` property of the div `auditTask`.
	 */
	private showDivAuditTask = false;

	private project: Project;

	/**
	 * Array of topics available in our referential.
	 */
	private topics: {[id: number]: string};

	/**
	 * Array of topics involved in the audit.
	 */
	private auditTopics = [];

	/**
	 * This subject emits the updated values present in the array `auditTopics`
	 * to the component `tableCategories`.
	 */
	private auditTopics$ = new BehaviorSubject<any>([]);

	private topicsHidden = true;

	/**
	 * This `boolean` represents the fact that the panel
	 * in charge of create or update a remark for this audit is visible.
	 *
	 * This `boolean` controls the `[hidden]` property of the div `auditTask`.
	 */
	private auditTaskFormModeIsOn = false;

	/**
	 * This subject is used to notify the `audit-task-form` of the current active topic.
	 */
	public topic$ = new Subject<TopicProject>();

	constructor(
		private referentialService: ReferentialService,
		private cinematicService: CinematicService) { super(); }

	ngOnInit() {

		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
				this.subscriptions.add(
					this.referentialService.topics$.subscribe (topics => {
						this.topics = topics;
						Object.keys(this.project.audit).forEach(key => {
							this.auditTopics.push( {id: Number(key), title: this.topics[key]} );
						});
						this.auditTopics$.next(this.auditTopics);
					}));
				}));

		this.subscriptions.add(
			this.cinematicService.auditTopicSelected$.subscribe(idTopic => {

				/**
				 * Use case :
				 * Given,
				 * this audit-task form is displayed for a given topic,
				 * and the end-user clicked on another thumbnail body (whithout clicking on the tasks button)
				 * Then we hide the audit-task form.
				 */
				if ( (idTopic !== this.cinematicService.idTopicTaskAuditFormSelected)
					&&  (-1 !== this.cinematicService.idTopicTaskAuditFormSelected)) {
						this.auditTaskFormModeIsOn = false;
						this.cinematicService.idTopicTaskAuditFormSelected = -1;
				}
		}));
	}

	ngAfterViewInit() {
	}
	/**
	 * Setup the categories involved in the manuel audit evaluation.
	 */
	setupCategories() {
		this.topicsHidden = !this.topicsHidden;
	}

	/**
	 * The user has involved, or removed, a topic from his exam.
	 * @param category the given category.
	 */
	onCategoryUpdated(category: Topic) {
		if (Constants.DEBUG) {
			console.log (
				((category.select) ? 'Selection' : 'Deselection)' + ' of %s'), category.title);
		}
		if (category.select) {
			this.auditTopics.push({id: category.id, title: category.title});
		} else {
			const index = this.auditTopics.findIndex(item => item.id === category.id);
			if (index === -1) {
				throw new Error ('Internal erreur. This index is supposed to be > 0');
			}
			this.auditTopics.splice(index, 1);
		}
		this.auditTopics$.next(this.auditTopics);
	}

	onShowDivAuditTask(idTopic: number) {
		if ((!this.auditTaskFormModeIsOn) && (idTopic !== this.cinematicService.idTopicTaskAuditFormSelected)) {
			this.topic$.next(new TopicProject(this.project.id, idTopic, this.topics[idTopic]));
			this.auditTaskFormModeIsOn = true;
			this.cinematicService.idTopicTaskAuditFormSelected = idTopic;
		} else {
			if (idTopic === this.cinematicService.idTopicTaskAuditFormSelected) {
				this.auditTaskFormModeIsOn = false;
				this.cinematicService.idTopicTaskAuditFormSelected = -1;
			} else {
				this.topic$.next(new TopicProject(this.project.id, idTopic, this.topics[idTopic]));
				this.cinematicService.idTopicTaskAuditFormSelected = idTopic;
			}
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
