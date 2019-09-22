import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
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

	/**
	* Observable emitting the panel selected.
	*/
	@Input() panelSelected$;

	/**
	 * Identifier representing the selected panel.
	 */
	private idPanelSelected = -1;

	/**
	 * Key of the Sonar project summary.
	 */
	private keySummarySelected = '';

	private project = new Project();

	private SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				console.log('nope');
				this.project = project;
			}));

		this.subscriptions.add(
			this.panelSelected$.subscribe(idPanel => {
				this.idPanelSelected = idPanel;
			}));
	}

	/**
	 * @param key the Project Sonar key
	 * @returns the CSS class to be used for the summary box.
	 */
	classForSonarProjectSummary(key: string): string {
		return ((key === this.keySummarySelected) ?
				'sonarProjectSummary-selected border' :
				'sonarProjectSummary border');
	}

	/**
	 * A Summary panel had been selected
	 * @param key the Sonar project key
	 */
	selectSummary(key: string) {
		if (Constants.DEBUG) {
			console.log('the Sonar key ' + key + ' is selected');
		}
		this.keySummarySelected = key;
	}

	/**
	 * Show the settings for sonar project Sonar.
	 */
	showSettings() {
		console.log ('showSettings()');
	}

	/**
	* Test if the given panel is activated.
	* @returns TRUE if the passed panel identifier is the selected one
	* @param idPanel panel identifier
	**/
	public isPanelActive(panel: number) {
		return (panel === this.idPanelSelected);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
