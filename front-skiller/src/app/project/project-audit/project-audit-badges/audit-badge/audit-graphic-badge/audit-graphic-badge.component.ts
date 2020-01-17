import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';
import { TopicEvaluation } from '../../topic-evaluation';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { BehaviorSubject } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Constants } from 'src/app/constants';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-audit-graphic-badge',
	templateUrl: './audit-graphic-badge.component.html',
	styleUrls: ['./audit-graphic-badge.component.css']
})
export class AuditGraphicBadgeComponent extends BaseComponent implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * Topic identifier.
	 */
	@Input() id;

	/**
	 * Quotation given to this category.
	 */
	@Input() evaluation;

	/**
	 * if this boolean is equal to __true__, there will be an input field in the middle of the badge __editable__.
	 */
	@Input() editable;

	/**
	 * This observable informs the component __IN NON EDITABLE ONLY__
	 * that the underlying project has changed and that the badge needs to be updated.
	 */
	@Input() project$: BehaviorSubject<Project>;

	/**
	 * This project is passed to the the component __IN NON EDITABLE ONLY__
	 */
	@Input() project: Project;

	/**
	 * The messenger throws the new evaluation givent by the end-user after each change.
	 */
	@Output() messengerEvaluationChange = new EventEmitter<TopicEvaluation>();

	/**
	 * color of the badge
	 */
	private color;

	constructor(
		private projectService: ProjectService,
		private cinematicService: CinematicService) { super(); }

	ngOnInit() {
		if (((this.project$) || (this.project)) && this.editable) {
			console.error ('SHOULD NOT PASS HERE : Usage of the observable project$ or the object project is reserved to the non editable mode');
		}
	}

	ngAfterViewInit() {

		if (this.editable) {
			if (Constants.DEBUG) {
				console.log ('Displaying the graphic badge in editable mode');
			}
			this.drawAuditArc();
			this.drawAuditText();
		}

		if (!this.editable) {
			if (Constants.DEBUG) {
				console.log ('Displaying the graphic badge in non-editable mode');
			}
			if (this.project) {
				this.drawNonEditableBadge(this.project);
			} else {
				this.subscriptions.add(
					this.project$.subscribe(project => {
						if (project) {
							this.drawNonEditableBadge(project);
						}
					})
				);
			}
		}
	}

	private drawNonEditableBadge (project: Project) {
		this.cinematicService.tabProjectActivated$
			.pipe(take(1))
			.subscribe(idxTabForm => {
				if (idxTabForm === Constants.PROJECT_IDX_TAB_FORM) {
					if (project.auditEvaluation) {
						this.evaluation = project.auditEvaluation;
						// We release the treatment flow in order to end the creation of the SVG elements
						setTimeout( () => {
							this.drawAuditArc();
							this.drawAuditText();
						}, 0);
					}
				}
			});
	}

	drawAuditArc() {

		const angleInRadians = angleInDegrees => (angleInDegrees - 90) * (Math.PI / 180.0);

		const polarToCartesian = (centerX, centerY, radius, angleInDegrees) => {
				const a = angleInRadians(angleInDegrees);
				return {
						x: centerX + (radius * Math.cos(a)),
						y: centerY + (radius * Math.sin(a)),
				};
		};

		const arc = (x, y, radius, startAngle, endAngle) => {
				const start = polarToCartesian(x, y, radius, endAngle - 0.01);
				const end = polarToCartesian(x, y, radius, startAngle);
				const arcSweep = endAngle - startAngle <= 180 ? '0' : '1';

				const d = [
						'M', start.x, start.y,
						'A', radius, radius, 0, arcSweep, 0, end.x, end.y,
				].join(' ');

				return d;
		};

		const endAngleEvaluation = this.evaluation * 3.6 - 180;
		if (endAngleEvaluation === -180) {
			this.color = 'none';
		} else {
			this.color = this.projectService.getEvaluationColor(this.evaluation);
		}
		const htmlElement = document.getElementById('topic-arc-' + this.id);
		if (!htmlElement) {
			console.error('Cannot reach %s', 'topic-arc-' + this.id);
		}
		if (htmlElement) {
			htmlElement.setAttribute('d', arc(60, 60, 50, -180, endAngleEvaluation));
			htmlElement.setAttribute('stroke', this.color);
		}

	}

	drawAuditText() {
		const htmlElement = document.getElementById('topic-note-' + this.id);
		if (!htmlElement) {
			console.error('Cannot reach %s', 'topic-note-' + this.id);
		}

		if (!this.editable) {
			htmlElement.setAttribute('x', '40');
			htmlElement.setAttribute('y', '70');
			htmlElement.textContent = '' + this.evaluation;
		} else {
			htmlElement.setAttribute('x', '30');
			htmlElement.setAttribute('y', '40');
		}
	}

	onInput() {
		if (!isNaN(this.evaluation)) {
			if (this.evaluation > 100) {
				this.evaluation = 100;
			}
			this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 1));
			this.drawAuditArc();
		}
	}

	onChange() {
		if (!isNaN(this.evaluation)) {
			this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 2));
		} else {
			this.evaluation = 0;
		}
	}
}

