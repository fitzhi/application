import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';
import { TopicEvaluation } from '../../topic-evaluation';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { BehaviorSubject } from 'rxjs';
import { CinematicService } from 'src/app/service/cinematic.service';
import { Constants } from 'src/app/constants';
import { take } from 'rxjs/operators';
import { ReferentialService } from 'src/app/service/referential.service';

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
	 * This project is passed to the the component __IN NON EDITABLE ONLY__
	 */
	@Input() project: Project;

	/**
	 * Width of the badge
	 */
	@Input() width: number;

	/**
	 * Height of the badge
	 */
	@Input() height: number;

	/**
	 * The messenger throws the new evaluation givent by the end-user after each change.
	 */
	@Output() messengerEvaluationChange = new EventEmitter<TopicEvaluation>();

	/**
	 * color of the badge
	 */
	private color;

	/**
	 * Size of the badge
	 */
	styleSize: any;

	constructor(
		public projectService: ProjectService,
		private referentialService: ReferentialService,
		private cinematicService: CinematicService) { super(); }

	ngOnInit() {
		this.styleSize = { 'width': this.width + 'px',  'height': + this.height + 'px' };
	}

	ngAfterViewInit() {
		if (this.editable) {
			if (Constants.DEBUG) {
				console.log ('Displaying the graphic badge in editable mode');
			}

			// 1) The project has to be loaded.
			this.projectService.projectLoaded$
				.pipe(take(1))
				.subscribe ({
					next: doneAndOk => {
						if (doneAndOk) {
							this.drawAuditArc();
							this.drawAuditText();
						}
					}
				});
		}

		if (!this.editable) {
			if (Constants.DEBUG) {
				console.log ('Displaying the graphic badge in non-editable mode');
			}
			if (this.project) {
				this.evaluation = this.project.auditEvaluation;
				this.drawNonEditableBadge(this.project);
			} else {
				this.subscriptions.add(
					this.projectService.projectLoaded$.subscribe({
						next: doneAndOk => {
							if (doneAndOk) {
								this.evaluation = this.projectService.project.auditEvaluation;
								this.drawNonEditableBadge(this.projectService.project);
							}
						}
					}));
			}
		}
	}

	private drawNonEditableBadge (project: Project) {
		this.drawAuditArc();
		this.drawAuditText();
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

