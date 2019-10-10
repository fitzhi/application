import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
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
	 * One of the summary requires to setup its metrics.
	 */
	@Output() settingsEmitter = new EventEmitter<string>();

	/**
	 * Key og the Sonar project selected.
	 */
	private keyPanelSelected = '';

	/**
	 * Key og the Sonar project selected.
	 */
	private idPanelSelected = -1;

	/**
	 * Key of the Sonar project summary.
	 */
	private keySummarySelected = '';

	private project = new Project();

	private SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	private SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
			}));

		this.subscriptions.add(
			this.panelSelected$.subscribe(keyPanelSelected => {
				this.keyPanelSelected = keyPanelSelected;
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
	 * @param the project Sonar key whose metrics have to be set up.
	 */
	showSettings(key: string) {
		this.settingsEmitter.next(key);
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
