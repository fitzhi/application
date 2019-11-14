import { Component, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { ReferentialService } from 'src/app/service/referential.service';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-techxhi-medal',
	templateUrl: './techxhi-medal.component.html',
	styleUrls: ['./techxhi-medal.component.css']
})
export class TechxhiMedalComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable of Project received from the parent Form project.
	 */
	@Input() project$;

	/**
	 * the color of the STAFF risk circle
	 */
	@Input() colorOfRisk: string;

	/**
	 * Mean Sonar evaluation.
	 */
	globalSonarEvaluation = 0;

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				if ((project) && (project.sonarProjects)) {
					let totalEvalution = 0;
					let totalNumerberLinesOfCode = 0;
					project.sonarProjects.forEach(sonarProject => {
						totalEvalution += sonarProject.sonarEvaluation.evaluation * sonarProject.sonarEvaluation.totalNumberLinesOfCode;
						totalNumerberLinesOfCode += sonarProject.sonarEvaluation.totalNumberLinesOfCode;
					});
					this.globalSonarEvaluation = Math.round(totalEvalution / totalNumerberLinesOfCode);
					if (Constants.DEBUG) {
						console.log ('globalSonarEvaluation %d for project %s', this.globalSonarEvaluation, project.name);
					}
				}
			}));
	}

	/**
	 * @returns the color figuring the risk evaluation for this project.
	 */
	styleDot () {
		return { 'fill': this.colorOfRisk };
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
