import { Component, OnInit, Input, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-quotation-badge',
	templateUrl: './quotation-badge.component.html',
	styleUrls: ['./quotation-badge.component.css']
})
export class QuotationBadgeComponent implements AfterViewInit {

	/**
	 * Index of the badge
	 */
	@Input() index;

	/**
	 * Title for this metric
	 */
	@Input() title;

	/**
	 * Quotation processed for this metric.
	 */
	@Input() evaluation;

	/**
	 * Quotation ratio in the global result for this metric.
	 */
	@Input() weight;

	private x_viewBox = 150;

	constructor(private projectService: ProjectService) { }

	ngAfterViewInit() {
		setTimeout(() => {
			this.drawBadge();
		}, 0);
	}

	drawBadge() {

		const angleInRadians = angleInDegrees => (angleInDegrees - 90) * (Math.PI / 180.0);

		const polarToCartesian = (centerX, centerY, radius, angleInDegrees) => {
			const a = angleInRadians(angleInDegrees);
			return {
				x: centerX + (radius * Math.cos(a)),
				y: centerY + (radius * Math.sin(a)),
			};
		};

		const arc = (x, y, radius, startAngle, endAngle) => {
			const fullCircle = endAngle - startAngle === 360;
			const start = polarToCartesian(x, y, radius, endAngle - 0.01);
			const end = polarToCartesian(x, y, radius, startAngle);
			const arcSweep = endAngle - startAngle <= 180 ? '0' : '1';

			const d = [
				'M', start.x, start.y,
				'A', radius, radius, 0, arcSweep, 0, end.x, end.y,
			];

			if (fullCircle) {
				d.push('z');
			}
			return d.join(' ');
		};

		document.getElementById('arc1-' + this.index).setAttribute('d', arc(20, 55, 40, 0, 90));
		document.getElementById('arc2-' + this.index).setAttribute('d', arc(20, 55, 48, 15, 75));
		document.getElementById('arc3-' + this.index).setAttribute('d', arc(20, 55, 56, 30, 60));
	}

		/**
	 * @param quotation evaluation processed for the selected Sonar project
	 * @returns thee classnames to draw the Sonar-liked arcs
	 */
	arcStyle(quotation: number, weight: number) {

		//
		// The result received for a single metric is scaled on the absolute weight of this metric
		// We rescale this quotation to a base 100 to obtain the corresponding risk.
		//
		const rescaledQuotation = quotation * 100 / weight;
		const risk = (rescaledQuotation === 100) ? 0 : (10 - Math.ceil(rescaledQuotation / 10));
		const styles = {
			'fill': 'none',
			'stroke-width': '4px',
			'stroke': this.projectService.getRiskColor(risk)
		};
		return styles;
	}

	arc() {


	}
}
