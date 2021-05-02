import { Component, OnInit, Output, Input, EventEmitter, OnDestroy, ViewChild } from '@angular/core';
import { SonarService } from 'src/app/service/sonar.service';
import { Subject, BehaviorSubject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { PanelSwitchEvent } from './sonar-thumbnails/panel-switch-event';
import { CinematicService } from 'src/app/service/cinematic.service';
import { SonarThumbnailsComponent } from './sonar-thumbnails/sonar-thumbnails.component';
import { Project } from 'src/app/data/project';
import { MessageGravity } from 'src/app/interaction/message/message-gravity';
import { Message } from 'src/app/interaction/message/message';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-project-sonar',
	templateUrl: './project-sonar.component.html',
	styleUrls: ['./project-sonar.component.css']
})
export class ProjectSonarComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Observable emitting the panel identifier.
	 */
	panelSwitchTransmitter$ = new Subject<PanelSwitchEvent>();

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
	@ViewChild(SonarThumbnailsComponent) thumbNails: SonarThumbnailsComponent;

	// Identifier of the panel selected.
	// By default we begin with the panel SONAR
	private idPanelSelected = 1;

	SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;
	NONE = Constants.PROJECT_SONAR_PANEL.NONE;

	constructor(
		public projectService: ProjectService,
		private cinematicService: CinematicService) { super(); }


	ngOnInit() {

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe((
				panelSwitchEvent: PanelSwitchEvent) => {
					if (traceOn()) {
						console.log ('Being informed that the panel %s has been selected', Constants.TITLE_PANELS[panelSwitchEvent.idPanel]);
					}
					this.idPanelSelected = panelSwitchEvent.idPanel;
			}));

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(tabSelected => {
				if (tabSelected === Constants.PROJECT_IDX_TAB_SONAR) {
					if (traceOn()) {
						if (!this.projectService.project) {
							console.log ('Sonar dashboard Activated');
						} else {
							console.log ('Sonar dashboard Activated for project %s', this.projectService.project.name);
						}
					}

					// If there is no (more) SonarProject, we cleanup the child containers.
					if (this.projectService.project && this.projectService.project.sonarProjects.length === 0) {
						// We send a null as SonarKey to force the initialization of the children data (such as i.e. the metrics dataSource)
						this.panelSwitchTransmitter$.next(
							new PanelSwitchEvent(
								this.NONE,
								null));
					}

					if (this.projectService.project && this.projectService.project.sonarProjects.length > 0) {
						if (traceOn()) {
							console.log ('By default, we starts on the %s dashboard with %s.',
								Constants.TITLE_PANELS[this.SETTINGS],
								this.projectService.project.sonarProjects[0].key);
						}

						setTimeout(() => {
							this.panelSwitchTransmitter$.next(
								new PanelSwitchEvent(
									this.SETTINGS,
									this.projectService.project.sonarProjects[0].key));
						}, 0);
					}

					if (this.thumbNails) {
						this.thumbNails.loadFilesNumber();
					}
				}
			}));

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
	* @param idPanel the Panel identifier
	* @returns TRUE if the passed panel identifier is the selected one
	**/
	public isPanelActive(panel: number) {
		return (panel === this.idPanelSelected);
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.cinematicService.projectTabIndex = tabIndex;
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
