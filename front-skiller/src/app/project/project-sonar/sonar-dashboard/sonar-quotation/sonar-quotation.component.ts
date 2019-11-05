import { Component, OnInit, ViewEncapsulation, Input, AfterViewInit } from '@angular/core';
import * as d3 from 'd3';
import { Subject } from 'rxjs';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { Constants } from 'src/app/constants';
import { SonarService } from 'src/app/service/sonar.service';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-sonar-quotation',
	templateUrl: './sonar-quotation.component.html',
	styleUrls: ['./sonar-quotation.component.css']
})
export class SonarQuotationComponent implements OnInit {

	@Input() project$;

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	/**
	 * Project loaded and received by the host component.
	 */
	private project: Project;

	/**
	 * Project evaluation
	 */
	private evaluation = -1;

	constructor(
		private sonarService: SonarService,
		private projectService: ProjectService) { }

	ngOnInit() {

		this.project$.subscribe(project => this.project = project);

		this.panelSwitchTransmitter$.subscribe(
			panelSwitchEvent => {
				if (panelSwitchEvent.idPanel === Constants.PROJECT_SONAR_PANEL.SONAR) {
					this.evaluateProject(panelSwitchEvent.keySonar);
				}
			}
		);
	}

	public evaluateProject(keySonar: string) {

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

		this.evaluation = this.sonarService.evaluateSonarProject(this.project, keySonar);

		document.getElementById('arc1').setAttribute('d', arc(50, 60, 40, 0, 90));
		document.getElementById('arc2').setAttribute('d', arc(50, 60, 48, 15, 75));
		document.getElementById('arc3').setAttribute('d', arc(50, 60, 56, 30, 60));


	}


	/**
	 * @param evaluation evaluation processed for the selected Sonar project
	 * @returns thee classnames to draw the Sonar-liked arcs
	 */
	arcStyle(evaluation: number) {

		const risk = Math.ceil(evaluation / 10);
		const styles = {
			'fill': 'none',
			'stroke-width': '4px',
			'stroke': this.projectService.getRiskColor(risk)
		};
		return styles;
	}

}
