import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { PanelSwitchEvent } from './panel-switch-event';

@Component({
	selector: 'app-sonar-thumbnails',
	templateUrl: './sonar-thumbnails.component.html',
	styleUrls: ['./sonar-thumbnails.component.css']
})
export class SonarThumbnailsComponent extends BaseComponent implements OnInit, OnDestroy {

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
	@Output() panelSwitchEmitter$ = new EventEmitter<PanelSwitchEvent>();

	/**
	 * Identifier representing the selected panel.
	 */
	private idPanelSelected = -1;

	/**
	 * Key of the Sonar project summary.
	 */
	private keySummarySelected = '';

	private project = new Project();

	public SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;

	public SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;

	constructor() { super(); }

	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.project = project;
				if (this.project.sonarProjects.length > 0) {
					this.idPanelSelected = this.SONAR;
					this.keySummarySelected = this.project.sonarProjects[0].key;
				}
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
	 * @param idPanel the identifier of the panel to be shown.
	 * @param key the project Sonar key whose metrics have to be set up.
	 */
	show(idPanel: number, key: string) {
		console.log (this.SETTINGS);
		if (Constants.DEBUG) {
			console.log ('Displaying panel ID ' + idPanel + ' for the Sonar project ' + key);
		}
		this.panelSwitchEmitter$.next(new PanelSwitchEvent(idPanel, key));
	}

	/**
	* Test if the given panel is activated.
	* @param idPanel panel identifier
	* @param keySonarProject key identifying the Sonar project.
	* @returns TRUE if the passed couple (panel identifier/key Sonar project) is the selected one, FALSE otherwise
	**/
	public isPanelActive(idPanel: number, keySonarProject: string) {
		return ( (idPanel === this.idPanelSelected) && (keySonarProject === this.keySummarySelected));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
