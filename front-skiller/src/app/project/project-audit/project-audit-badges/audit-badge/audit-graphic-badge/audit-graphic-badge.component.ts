import { Component, OnInit, Input, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';
import { TopicEvaluation } from '../../topic-evaluation';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-audit-graphic-badge',
	templateUrl: './audit-graphic-badge.component.html',
	styleUrls: ['./audit-graphic-badge.component.css']
})
export class AuditGraphicBadgeComponent implements OnInit, AfterViewInit {

	/**
	 * Index of the badge
	 */
	@Input() index;

	/**
	 * Topic identifier.
	 */
	@Input() id;

	/**
	 * Quotation given to this category.
	 */
	@Input() evaluation: number;

	/**
	 * if this boolean is equal to __true__, there will be an input field in the middle of the badge __editable__.
	 */
	@Input() editable;

	/**
	 * The messenger throws the new evaluation givent by the end-user after each change.
	 */
	@Output() messengerEvaluationChange = new EventEmitter<TopicEvaluation>();

	/**
	 * color of the badge
	 */
	private color;

	constructor(private projectService: ProjectService) { }

	ngOnInit() {
	}

	ngAfterViewInit() {
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
		document.getElementById('topic-arc-' + this.index).setAttribute('d', arc(60, 60, 50, -180, endAngleEvaluation));
		document.getElementById('topic-arc-' + this.index).setAttribute('stroke', this.color);

	}

	drawAuditText() {
		if (!this.editable) {
			document.getElementById('topic-note-' + this.index).setAttribute('x', '40');
			document.getElementById('topic-note-' + this.index).setAttribute('y', '70');
		} else {
			document.getElementById('topic-note-' + this.index).setAttribute('x', '30');
			document.getElementById('topic-note-' + this.index).setAttribute('y', '40');
		}
	}

	onInput() {
		if (this.evaluation > 100) {
			this.evaluation = 100;
		}
		this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 1));
		this.drawAuditArc();
	}

	onChange() {
		this.messengerEvaluationChange.emit(new TopicEvaluation(this.id, this.evaluation, 2));
	}
}

