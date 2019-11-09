import { Component, OnInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { PanelSwitchEvent } from './panel-switch-event';
import { SonarProject } from 'src/app/data/SonarProject';
import { SonarService } from 'src/app/service/sonar.service';
import { ILanguageCount } from 'src/app/service/ILanguageCount';
import { FilesStats } from 'src/app/data/sonar/FilesStats';
import { ProjectService } from 'src/app/service/project.service';
import { MessageService } from 'src/app/message/message.service';

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
	 * Observable emitting a PanelSwitchEvent
	 * when the user switch to another Sonar project or a new panel.
	 */
	@Input() panelSwitchTransmitter$;

	/**
	 * Identifier representing the selected panel.
	 */
	private idPanelSelected = -1;

	/**
	 * Key of the Sonar project summary.
	 */
	private keySummarySelected = '';

	private project = new Project();

	private languageCounts: SonarThumbnailsComponent.LanguageCount;

	public SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	public SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;
	public NONE = Constants.PROJECT_SONAR_PANEL.NONE;

	public languageFilesNumber = new Map<string, FilesStats[]>();

	/**
	 * Map containting the evaluations for each Sonar project.
	 */
	private evaluations = new Map<string, number>();

	constructor(
		private sonarService: SonarService,
		private projectService: ProjectService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit() {

		this.subscriptions.add(
			this.project$.subscribe(project => {
				this.evaluations.clear();
				this.project = project;
				if (this.project.sonarProjects.length > 0) {
					this.idPanelSelected = this.SONAR;
					this.keySummarySelected = this.project.sonarProjects[0].key;

					this.project.sonarProjects.forEach (sonarProject =>
						this.evaluations.set (
							sonarProject.key,
							this.sonarService.evaluateSonarProject(this.project, sonarProject.key))
					);
				}
				this.loadFilesNumber();
			}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe( (panelSwitchEvent: PanelSwitchEvent) => {
				if (!panelSwitchEvent.keySonar) {
					if (Constants.DEBUG) {
						console.log ('No Sonar project declared!');
					}
				} else {
					this.idPanelSelected = panelSwitchEvent.idPanel;
					this.keySummarySelected = panelSwitchEvent.keySonar;
				}
		}));
	}

	loadFilesNumber() {
		this.subscriptions.add(
			this.project.sonarProjects.forEach((sonar: SonarProject) => {
				this.languageFilesNumber.set(sonar.key, sonar.projectFilesStats);
				if (Constants.DEBUG) {
					console.groupCollapsed('Files statistics for the Sonar instance %s.', sonar.key);
					sonar.projectFilesStats.forEach(fs => console.log (fs.language, fs.numberOfFiles));
					console.groupEnd();
				}
			}));
		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$.subscribe( connected => {
				if (connected) {
					this.project.sonarProjects.forEach (
						sonarP => this.retrieveAndUpdateFilesSummary(sonarP));
				}
			}));
	}
	/**
	 * Retrieve and save (IF NECESSARY) the number of files per language examined by the passed sonar component
	 * @param keyComponentSonar : the component key
	 */
	retrieveAndUpdateFilesSummary(componentSonar: SonarProject) {
		this.sonarService.loadFiles(componentSonar.key).subscribe( filesCount => {
			componentSonar.projectFilesStats = this.keepTop3TypesOfFile(filesCount);
			if (Constants.DEBUG) {
				console.groupCollapsed('Top 3 languages for Sonar project %s', componentSonar.key);
				componentSonar.projectFilesStats.forEach(counting => {
					console.log (counting.language, counting.numberOfFiles);
				});
				console.groupEnd();
			}
			this.languageFilesNumber.set(componentSonar.key, componentSonar.projectFilesStats);
			this.subscriptions.add(
				this.projectService
					.saveFilesStats(this.project.id, componentSonar.key, componentSonar.projectFilesStats)
					.subscribe(doneAndOk => {
						if (doneAndOk) {
							this.messageService.info('Saving Files source statistics for the key ' + componentSonar.key);
						}
					}));
				});
	}

	/**
	 * Keep the top 3 types of files.
	 * @param filesCount number of files per language
	 */
	private keepTop3TypesOfFile(filesCount: ILanguageCount): FilesStats[] {
		const mapAll = new Map<string, number>();

		Object.entries(filesCount).forEach( ([language, count]) => {
				mapAll.set(language, count);
		});
		const mapAllSorted = new Map([...mapAll.entries()].sort((a, b) => b[1] - a[1]));


		const top3: FilesStats[] = [];
		for (const key of mapAllSorted.keys()) {
			if (top3.length < 3) {
				top3.push (new FilesStats(key, mapAll.get(key)));
			}
		}
		return top3;
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
	 * A thumbnail panel had been clicked.
	 * @param idPanel the identifier of the panel to be shown.
	 * @param key the active project Sonar key.
	 */
	switchSonarContext(idPanel: number, key: string) {

		// We ckick on the same panel without selecting a specific detail button (Quotation vs metric)
		// We do not change anything at all
		if ((key === this.keySummarySelected) && (idPanel === this.NONE)) {
			return;
		}

		if (Constants.DEBUG) {
			console.log ('Displaying ' + Constants.TITLE_PANELS[idPanel] + ' for the ' + key);
		}

		// If the user clicks on the body of the thumbnail; but not on a button,
		// we continue to work with the same panel, but maybe on a different Sonar project.
		if (idPanel !== this.NONE) {
			this.idPanelSelected = idPanel;
		}

		this.keySummarySelected = key;
		// We broadcast this change (if any) to all children panel.
		this.panelSwitchTransmitter$.next( new PanelSwitchEvent(this.idPanelSelected, key));

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
	 * @param key the language key retrieved from the Sonar instance
	 * @returns the label related to the passed key
	 */
	labelOfLanguage(key: string): string {

		const labels = {
			'css': 'CSS',
			'ts': 'TypeScript',
			'java': 'Java',
			'web': 'HTML',
			'js': 'JavaScript',
			'xml': 'XML',
			'c#': 'C#',
		};
		return labels[key];
	}
	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}

export namespace SonarThumbnailsComponent {
	export class LanguageCount {
		constructor (public language: string, public count: number) {}
	}
}
