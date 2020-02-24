import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { SonarService } from 'src/app/service/sonar.service';
import { Constants } from 'src/app/constants';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ProjectService } from 'src/app/service/project.service';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { Subject, BehaviorSubject } from 'rxjs';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';
import * as d3 from 'd3';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-sonar-dashboard',
	templateUrl: './sonar-dashboard.component.html',
	styleUrls: ['./sonar-dashboard.component.css']
})
export class SonarDashboardComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	constructor(
		private sanitize: DomSanitizer,
		private projectService: ProjectService,
		private sonarService: SonarService) { super(); }

	public isSonarAccessible = false;

	/**
	 * Equal to TRUE if the current Sonar version is 7.x or higher
	 */
	public isSonarVersion71x = false;

	/**
	 * Key of the current selected Sonar project.
	 */
	private sonarKey = '';

	/**
	 * Actual Sonar metrics value corresponding to the selected sonarKey.
	 */
	private projectSonarMetricValues: ProjectSonarMetricValue[];

	/**
	 * SVG badge to be displayed for the selected metrics.
	 */
	private safeBadge: SafeHtml[] = [];

	/**
	 * Observable to this array.
	 */
	private safeBadge$ = new Subject<SafeHtml[]>();

	safeBadgeLength = 0;

	ngOnInit() {

		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$(this.projectService.project).subscribe(isSonarAccessible => {
				if (isSonarAccessible) {
					this.isSonarAccessible = isSonarAccessible;
					const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
					if (sonarServer) {
						const version = parseFloat(sonarServer.sonarVersion.substring(0, 3));
						this.isSonarVersion71x = (version > 7.1);
						if (traceOn()) {
							if (this.isSonarVersion71x) {
								console.log('Sonar version 7.1x');
							} else {
								console.log('Sonar version < 7.x');
							}
						}
					}
				}
		}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe((panelSwitchEvent: PanelSwitchEvent) => {
				if (!panelSwitchEvent.keySonar) {
					if (traceOn()) {
						console.log ('No Sonar project declared.');
					}
					return;
				}
				if (this.projectService.project && (panelSwitchEvent.keySonar) ) {
					this.sonarKey = panelSwitchEvent.keySonar;
					this.safeBadge = [];
					const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
					if (sonarServer && sonarServer.projectSonarMetrics) {
						this.loadBadge(0);
					}
				}
			}));
	}

	/**
	 * This method is recurcive !!
	 * Load the Sonar badge corresponding to the numero of badge.
	 * @param badgeNumero the numero of badge
	 */
	loadBadge(badgeNumero: number) {
		const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
		if ((sonarServer) && (badgeNumero === sonarServer.projectSonarMetrics.length)) {
			this.safeBadge$.next(this.safeBadge);
			return;
		}
		this.subscriptions.add(
			this.sonarService
				.loadBadge(this.projectService.project, this.sonarKey, sonarServer.projectSonarMetrics[badgeNumero].key)
				.subscribe(svg => {
					if (svg) {
						this.safeBadge.push(this.sanitize.bypassSecurityTrustHtml(svg));
						this.safeBadgeLength = this.safeBadge.length;
						this.loadBadge(badgeNumero + 1);
					} else {
						throw new Error('INTERNAL ERROR : loadBadge did not generate a badge.');
					}
				}));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
