import { Component, OnInit, ViewEncapsulation, Input, AfterViewInit, OnDestroy } from '@angular/core';
import * as d3 from 'd3';
import { Subject, BehaviorSubject } from 'rxjs';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { Constants } from 'src/app/constants';
import { SonarService } from 'src/app/service/sonar.service';
import { Project } from 'src/app/data/project';
import { ProjectService } from 'src/app/service/project.service';
import { BadgeQuotation } from './badge-quotation';
import { BaseComponent } from 'src/app/base/base.component';
import { SonarProject } from 'src/app/data/SonarProject';

@Component({
	selector: 'app-sonar-quotation',
	templateUrl: './sonar-quotation.component.html',
	styleUrls: ['./sonar-quotation.component.css']
})
export class SonarQuotationComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	/**
	 * Array of Project evaluations.
	 */
	public evaluations: BadgeQuotation[] = [];

	/**
	 * Title of the quotation badge
	 */
	private title: string;

	constructor(
		private sonarService: SonarService,
		private projectService: ProjectService) { super(); }

	ngOnInit() {

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe(
				panelSwitchEvent => {
					if (panelSwitchEvent.idPanel === Constants.PROJECT_SONAR_PANEL.SONAR) {
						this.evaluations = [];
						// Project should have been already loaded for this component.
						if (this.projectService.project) {
							this.evaluateProject(panelSwitchEvent.keySonar);
						}
					}
				}
			));
	}

	/**
	 * Fill the array of relevant metrics for a given Sonar project.
	 * @param keySonar the project Sonar key
	 */
	public evaluateProject(keySonar: string) {

		this.evaluations.push(
			new BadgeQuotation(
				'Global quotation',
				this.sonarService.evaluateSonarProject(this.projectService.project, keySonar),
				100));

		const sonarProject = this.projectService.getSonarProject(this.projectService.project, keySonar);
		if (!sonarProject) {
			throw new Error ('Cannot retrieve the Sonar project ' + keySonar + ' for project ' + this.projectService.project.name);
		}

		const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
		if (sonarServer) {
			sonarProject.projectSonarMetricValues.forEach( metricValue => {

				const emptyProject = new Project();
				emptyProject.urlSonarServer = this.projectService.project.urlSonarServer;
				emptyProject.sonarProjects = [];
				const sonar = new SonarProject();
				sonar.key = keySonar;
				sonar.projectSonarMetricValues = [];
				sonar.projectSonarMetricValues.push(metricValue);
				emptyProject.sonarProjects.push (sonar);

				this.evaluations.push(
					new BadgeQuotation(
						sonarServer.getMetricTitle (metricValue.key),
						this.sonarService.evaluateSonarProject(emptyProject, keySonar),
						metricValue.weight));
				});
		}
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
