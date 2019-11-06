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

	/**
	 * Title of the quotation badge
	 */
	private title: string;

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

		this.evaluation = this.sonarService.evaluateSonarProject(this.project, keySonar);
		this.title = 'Global quotation';
	}

}
