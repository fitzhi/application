import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Subject } from 'rxjs';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project/project.service';
import { SonarService } from 'src/app/service/sonar/sonar.service';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';

@Component({
	selector: 'app-sonar-dashboard',
	templateUrl: './sonar-dashboard.component.html',
	styleUrls: ['./sonar-dashboard.component.css']
})
export class SonarDashboardComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	constructor(
		private sanitizer: DomSanitizer,
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
	public safeBadge$ = new Subject<SafeHtml[]>();

	safeBadgeLength = 0;

	ngOnInit() {

		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$(this.projectService.project).subscribe(isSonarAccessible => {
				if (isSonarAccessible) {
					this.isSonarAccessible = isSonarAccessible;
					const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
					if (sonarServer) {
						const version = parseFloat(sonarServer.sonarVersion.substring(0, 3));
						// The releases of Sonar prior to version 7.1 do not support the API Rest with these metrics
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
	 * _**This method is recurcive !!**_
	 *
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
				.loadProjectBadge$(this.projectService.project, this.sonarKey, sonarServer.projectSonarMetrics[badgeNumero].key)
				.subscribe(svg => {
					if (svg) {
						// The SVG has been sanitized.
						this.safeBadge.push(this.sanitizer.bypassSecurityTrustHtml(svg)); // tslint:disable-line:comment-format  //NOSONAR
						this.safeBadgeLength = this.safeBadge.length;
						this.loadBadge(badgeNumero + 1);
					} else {
						throw new Error('INTERNAL ERROR : loadBadge(' + badgeNumero + ') did not generate a badge.');
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
