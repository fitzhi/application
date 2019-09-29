import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { SonarService } from 'src/app/service/sonar.service';
import { Constants } from 'src/app/constants';

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

	private project = new Project();

	constructor(private sonarService: SonarService) { super(); }

	private isSonarAccessible = false;

	/**
	 * Equal to TRUE if the current Sonar version is 7.x or higher
	 */
	private isSonarVersion7x = false;

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
			}));
		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$.subscribe(isSonarAccessible => {
				if (isSonarAccessible) {
					this.isSonarAccessible = isSonarAccessible;
					this.isSonarVersion7x = (this.sonarService.sonarVersion.substring(0, 1) === '7');
					if (Constants.DEBUG) {
						if (this.isSonarVersion7x) {
							console.log('Sonar version 7.x');
						} else {
							console.log('Sonar version < 7.x');
						}
					}
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
