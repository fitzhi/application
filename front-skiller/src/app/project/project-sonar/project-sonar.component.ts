import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { SonarService } from 'src/app/service/sonar.service';

@Component({
	selector: 'app-project-sonar',
	templateUrl: './project-sonar.component.html',
	styleUrls: ['./project-sonar.component.css']
})
export class ProjectSonarComponent implements OnInit {

	/**
	* The project loaded in the parent component.
	*/
	@Input() project$;

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 * e.g. if the project form is not complete, application will jump to this tab pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	/**
	 * This event is fired if the sunburst is processed to inform the form component that the project might have changed.
	 * At least, the level of risk has changed.
	 */
	@Output() updateRiskLevel = new EventEmitter<number>();

	// The Sonar panel has to displayed.
	public SONAR = 1;

	// The settings panel has to be displayed.
	public SETTINGS = 2;

	// Identifier of the panel selected.
	// By default we begin with the panel SONAR
	private idPanelSelected = 1;

	constructor(private sonarService: SonarService) { }

	ngOnInit() {
		this.sonarService.sonarIsAccessible$
			.subscribe ( isAccessible => {
				if (isAccessible) {
					const bugs = ['bugs'];
					this.sonarService
						.loadSonarComponentMeasures ('Skiller', bugs)
						.subscribe(response => {
							console.log (response.component.key);
						});
				}
			});
	}

	/**
    * Show the panel associated to this id.
    * @param idPanel Panel identifier
    */
	public show(idPanel: number) {
		switch (idPanel) {
			case this.SONAR:
				this.idPanelSelected = idPanel;
				break;
			case this.SETTINGS:
					this.idPanelSelected = idPanel;
				break;
			default:
				console.error ('SHOULD NOT PASS HERE FOR ID ' + idPanel);
				break;
		}
	}

	/**
	* Test if the given panel is activated.
	* @returns TRUE if the passed panel identifier is the selected one
	* @param idPanel panel identifier
	**/
	public isPanelActive(panel: number) {
		return (panel === this.idPanelSelected);
	}

}
