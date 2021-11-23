import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Project } from 'src/app/data/project';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project/project.service';
import { TopicEvaluation } from '../../topic-evaluation';

@Component({
	selector: 'app-audit-graphic-badge',
	templateUrl: './audit-graphic-badge.component.html',
	styleUrls: ['./audit-graphic-badge.component.css']
})
export class AuditGraphicBadgeComponent extends BaseDirective implements OnInit, OnDestroy, AfterViewInit {

	/**
	 * Topic identifier.
	 */
	@Input() id;

	/**
	 * Quotation given to this category.
	 */
	@Input() evaluation = null;

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
	 * The messenger throws the new evaluation given by the end-user after each change.
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
		public projectService: ProjectService) { super(); }

	ngOnInit() {
		this.styleSize = { 'width': this.width + 'px',  'height': + this.height + 'px' };
	}

	ngAfterViewInit() {
		if (this.editable) {
			if (traceOn()) {
				console.log ('Displaying the graphic badge in editable mode');
			}

			// 1) The project has to be loaded.
			this.subscriptions.add(
				this.projectService.projectLoaded$.subscribe ({
					next: doneAndOk => {
						if (doneAndOk) {
							// We colorize the Arc and Text after the UI event loop to avoid a transparent arc ('for an unknwon reason' (shame on me)).
							setTimeout(() => {
								this.drawAuditText(this.projectService.project.auditEvaluation);
								this.drawAuditArc(this.projectService.project.auditEvaluation);
							}, 0);
						}
					}
				})
			);
		}

		// In read-only mode, we scenarios are possible
		// 1) the component receives a project (as input parameter) and we draw the auditEvaluation of this project
		// 2) the component receives the evaluation to be written as input parameter
		// 3) the component listen to the project observable and writes its evaluation
		if (!this.editable) {
			if (traceOn()) {
				console.log ('Displaying the graphic badge in non-editable mode');
			}
			if (this.project) {
				if (this.project.auditEvaluation > 0) {
					this.drawBadge(this.project.auditEvaluation);
				}
			} else {
				if (this.evaluation) {
					this.drawBadge(this.evaluation);
				} else {
					this.subscriptions.add(
						this.projectService.projectLoaded$.subscribe({
							next: doneAndOk => {
								if (doneAndOk) {
									this.drawBadge(this.projectService.project.auditEvaluation);
								}
							}
						}));
				}
			}
		}
	}

	/**
	 * Draw the badge for the given evaluation.
	 * @param evaluation the evaluation to be displayed in the graphic
	 */
	drawBadge(evaluation: number) {
		//
		// We postpone the update to avoid an ExpressionChangedAfterItHasBeenCheckedError Warning
		//
		setTimeout(() => {
			this.drawNonEditableBadge(evaluation);
		}, 0);
	}

	/**
	 * Draw the **read only** badge for the given evaluation.
	 * @param evaluation the evaluation to be displayed in the graphic
	 */
	private drawNonEditableBadge (evaluation: number) {
		this.drawAuditArc(evaluation);
		this.drawAuditText(evaluation);
	}

	/**
	 * Display the arc around the evaluation number.
	 * @param evaluation the evaluation to be displayed in the graphic
	 */
	drawAuditArc(evaluation: number) {

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

		const endAngleEvaluation = evaluation * 3.6 - 180;
		if (endAngleEvaluation === -180) {
			this.color = 'none';
		} else {
			this.color = this.projectService.getEvaluationColor(evaluation);
		}
		const htmlElement = document.getElementById('topic-arc-' + this.id);
		if (!htmlElement) {
			console.error(`Cannot reach topic-arc-${this.id}`);
		}
		if (htmlElement) {
			htmlElement.setAttribute('d', arc(60, 60, 50, -180, endAngleEvaluation));
			htmlElement.setAttribute('stroke', this.color);
		}

	}

	/**
	 * Write the evaluation number in the middle of the arc.
	 * @param evaluation the given evaluation to be displayed
	 */
	drawAuditText(evaluation: number) {
		const htmlElement = document.getElementById('topic-note-' + this.id);
		if (!htmlElement) {
			console.error(`Cannot reach topic-note-${this.id}`);
			return;
		}

		if (!this.editable) {
			htmlElement.setAttribute('x', (evaluation === 100) ? '27' : '35');
			htmlElement.setAttribute('y', '70');
			htmlElement.textContent = '' + evaluation;
		} else {
			htmlElement.setAttribute('x', '30');
			htmlElement.setAttribute('y', '40');
		}
	}

	/**
	 * Each key stroke in the **"evaluation input field"** invokes this method.
	 */
	onInput() {
		if (!isNaN(this.evaluation)) {
			if (this.evaluation > 100) {
				this.evaluation = 100;
			}
			this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 1));
			this.drawAuditArc(this.evaluation);
		}
	}

	/**
	 * Each time the content of the **"evaluation input field"** changes, this method is invoked.
	 */
	onChange() {
		if (!isNaN(this.evaluation)) {
			this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 2));
		} else {
			this.evaluation = 0;
		}
	}
}

