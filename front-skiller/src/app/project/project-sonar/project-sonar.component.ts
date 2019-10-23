import { Component, OnInit, Output, Input, EventEmitter, OnDestroy, ViewChild } from '@angular/core';
import { SonarService } from 'src/app/service/sonar.service';
import { Subject, BehaviorSubject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { PanelSwitchEvent } from './sonar-thumbnails/panel-switch-event';
import { CinematicService } from 'src/app/service/cinematic.service';
import { SonarThumbnailsComponent } from './sonar-thumbnails/sonar-thumbnails.component';
import { Project } from 'src/app/data/project';
import { MessageGravity } from 'src/app/message/message-gravity';
import { Message } from 'src/app/message/message';
import { MessageService } from 'src/app/message/message.service';

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
	 * Observable emitting the panel identifier.
	 */
	panelSwitchTransmitter$ = new Subject<PanelSwitchEvent>();

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
	 * This Output EventEmitter is in charge of the propagation of info/warning/error messages to the host container
	 * For an unknown reason the messageService.* is failing outsite the host panel
	 */
	@Output() throwMessage = new EventEmitter<MessageGravity>();

	/**
	 * The Sonar Thumnbails composant hosted on the top of the dashboard
	 */
	@ViewChild(SonarThumbnailsComponent, {static: false}) thumbNails: SonarThumbnailsComponent;

	// Identifier of the panel selected.
	// By default we begin with the panel SONAR
	private idPanelSelected = 1;

	/**
	 * Current project.
	 */
	private project: Project;

	private SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	private SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;
	private NONE = Constants.PROJECT_SONAR_PANEL.NONE;

	constructor(
		private sonarService: SonarService,
		private messageService: MessageService,
		private cinematicService: CinematicService) { super(); }


	ngOnInit() {
		this.subscriptions.add(
			this.project$.subscribe(project => this.project = project));

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

					// If there is no (more) SonarProject, we cleanup the child containers.
					if (this.project && this.project.sonarProjects.length === 0) {
						// We send a null as SonarKey to force the initialization of the children data (such as i.e. rhe metrics dataSource)
						this.panelSwitchTransmitter$.next(
							new PanelSwitchEvent(
								this.NONE,
								null));
					}

					if (this.project && this.project.sonarProjects.length > 0) {
						this.panelSwitchTransmitter$.next(
							new PanelSwitchEvent(
								this.SONAR,
								this.project.sonarProjects[0].key));
					}

					if (this.thumbNails) {
						this.thumbNails.loadFilesNumber();
					}
				}
			}));

		this.panelSwitchTransmitter$.subscribe((panelSwitchEvent: PanelSwitchEvent) => {
			this.idPanelSelected = panelSwitchEvent.idPanel;
		});
	}

	/**
	 * Propagation of the message to the host panel
	 * @param messageGravity the message and its gravity
	 */
	catchMessage(messageGravity: MessageGravity) {
		this.throwMessage.next(messageGravity);
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
