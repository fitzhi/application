import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { SonarService } from 'src/app/service/sonar.service';
import { Constants } from 'src/app/constants';
import { CinematicService } from 'src/app/service/cinematic.service';
import { SonarThumbnailsComponent } from '../sonar-thumbnails/sonar-thumbnails.component';
import { ThrowStmt } from '@angular/compiler';

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
	* Observable emitting the panel selected.
	*/
	@Input() panelSelected$;

	private project: Project;

	constructor(
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

	private badge = '';

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
			this.panelSelected$.subscribe(idPanel => {
				if (this.project && (idPanel >= 0) ) {
					this.sonarKey = this.project.sonarProjects[idPanel].key;
					this.badge = 'Metric for ' + this.sonarKey;
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
