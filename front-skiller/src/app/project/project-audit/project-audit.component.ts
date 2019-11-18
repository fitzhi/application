import { Component, OnInit, AfterViewInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';
import { Topic } from './table-categories/topic';
import { BehaviorSubject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { BaseComponent } from 'src/app/base/base.component';
import { Project } from 'src/app/data/project';
import { ReferentialService } from 'src/app/service/referential.service';

@Component({
	selector: 'app-project-audit',
	templateUrl: './project-audit.component.html',
	styleUrls: ['./project-audit.component.css']
})
export class ProjectAuditComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	@Input() project$;

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

	displayCategories = true;

	constructor(private referentialService: ReferentialService) { super(); }

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


	}

	ngAfterViewInit() {
	}

	/**
	 * Setup the categories involved in the manuel audit evaluation.
	 */
	setupCategories() {
		this.displayCategories = !this.displayCategories;
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

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
