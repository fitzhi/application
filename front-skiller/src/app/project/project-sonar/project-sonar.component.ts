import { Component, OnInit, Output, Input, EventEmitter, OnDestroy, ViewChild } from '@angular/core';
import { SonarService } from 'src/app/service/sonar.service';
import { Subject, BehaviorSubject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { PanelSwitchEvent } from './sonar-thumbnails/panel-switch-event';
import { CinematicService } from 'src/app/service/cinematic.service';
import { SonarThumbnailsComponent } from './sonar-thumbnails/sonar-thumbnails.component';
import { Project } from 'src/app/data/project';

@Component({
	selector: 'app-project-sonar',
	templateUrl: './project-sonar.component.html',
	styleUrls: ['./project-sonar.component.css']
})
export class ProjectSonarComponent extends BaseComponent implements OnInit, OnDestroy {

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

	/**
	 * The Sonar Thumnbails composant hosted on the top of the dashboard
	 */
	@ViewChild(SonarThumbnailsComponent, {static: false}) thumbNails: SonarThumbnailsComponent;

	/**
	 * Observable emitting the panel identifier.
	 */
	panelSelected$ = new BehaviorSubject<number>(-1);

	// Identifier of the panel selected.
	// By default we begin with the panel SONAR
	private idPanelSelected = 1;

	/**
	 * Current project.
	 */
	private project: Project;

	SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;

	constructor(
		private sonarService: SonarService,
		private cinematicService: CinematicService) { super(); }


	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => this.project = project));

		this.subscriptions.add(
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
				}));

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(tabSelected => {
				if (tabSelected === Constants.PROJECT_IDX_TAB_SONAR) {
					if (Constants.DEBUG) {
						if (!this.project) {
							console.log ('Sonar dashboard Activated');
						} else {
							console.log ('Sonar dashboard Activated for project %s', this.project.name);
						}
					}
					if (this.project && this.project.sonarProjects.length > 0) {
						this.panelSelected$.next(0);
					}
					if (this.thumbNails) {
						this.thumbNails.loadFilesNumber();
					}
				}
			}));
			}

	/**
    * Show the panel associated to this id.
    * @param idPanel Panel identifier
    */
	public show(idPanel: number) {
		switch (idPanel) {
			case Constants.PROJECT_SONAR_PANEL.SONAR:
				this.idPanelSelected = idPanel;
				this.panelSelected$.next(idPanel);
				break;
			case Constants.PROJECT_SONAR_PANEL.SETTINGS:
				this.idPanelSelected = idPanel;
				this.panelSelected$.next(idPanel);
				break;
			default:
				console.error ('SHOULD NOT PASS HERE FOR ID ' + idPanel);
				break;
		}
	}

	/**
	 * @param event Data sent from the SonarThumbNails instance to inform the parent component of the panel and sonar project selected.
	 */
	onPanelSwitch(event: PanelSwitchEvent) {
		this.show(event.idPanel);
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
