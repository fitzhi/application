import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { SonarService } from 'src/app/service/sonar.service';
import { Constants } from 'src/app/constants';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ProjectService } from 'src/app/service/project.service';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { Subject } from 'rxjs';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';
import * as d3 from 'd3';

@Component({
	selector: 'app-sonar-dashboard',
	templateUrl: './sonar-dashboard.component.html',
	styleUrls: ['./sonar-dashboard.component.css']
})
export class SonarDashboardComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	* The project loaded in the parent component.
	*/
	@Input() project$;

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	/**
	 * The current project loaded.
	 */
	private project: Project;

	constructor(
		private sanitize: DomSanitizer,
		private projectService: ProjectService,
		private sonarService: SonarService) { super(); }

	private isSonarAccessible = false;

	/**
	 * Equal to TRUE if the current Sonar version is 7.x or higher
	 */
	private isSonarVersion71x = false;

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
			this.project$.subscribe((project: Project) => {
				if (Constants.DEBUG) {
					console.log ('Receiving project %s', project.name);
				}
				this.project = project;
			}));

		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$.subscribe(isSonarAccessible => {
				if (isSonarAccessible) {
					this.isSonarAccessible = isSonarAccessible;
					const version = parseFloat(this.sonarService.sonarVersion.substring(0, 3));
					this.isSonarVersion71x = (version > 7.1);
					if (Constants.DEBUG) {
						if (this.isSonarVersion71x) {
							console.log('Sonar version 7.1x');
						} else {
							console.log('Sonar version < 7.x');
						}
					}
				}
		}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe((panelSwitchEvent: PanelSwitchEvent) => {
				if (!panelSwitchEvent.keySonar) {
					if (Constants.DEBUG) {
						console.log ('No Sonar project declared.');
					}
					return;
				}
				if (this.project && (panelSwitchEvent.keySonar) ) {
					this.sonarKey = panelSwitchEvent.keySonar;
					this.safeBadge = [];
					if (this.sonarService.projectSonarMetrics) {
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
		if (badgeNumero === this.sonarService.projectSonarMetrics.length) {
			this.safeBadge$.next(this.safeBadge);
			return;
		}
		this.subscriptions.add(
			this.sonarService
				.loadBadge(this.sonarKey, this.sonarService.projectSonarMetrics[badgeNumero].key)
				.subscribe(svg => {
					this.safeBadge.push(this.sanitize.bypassSecurityTrustHtml(svg));
					this.safeBadgeLength = this.safeBadge.length;
					this.loadBadge(badgeNumero + 1);
				}));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
