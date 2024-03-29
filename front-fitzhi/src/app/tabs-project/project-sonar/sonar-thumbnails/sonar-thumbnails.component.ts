import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { take } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Constants } from 'src/app/constants';
import { SonarEvaluation } from 'src/app/data/sonar-evaluation';
import { FilesStats } from 'src/app/data/sonar/FilesStats';
import { SonarProject } from 'src/app/data/sonar-project';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ILanguageCount } from 'src/app/service/ILanguageCount';
import { ProjectService } from 'src/app/service/project/project.service';
import { SonarService } from 'src/app/service/sonar/sonar.service';
import { PanelSwitchEvent } from './panel-switch-event';
import { ThumbnailQuotationBadge } from './thumbnail-quotation-badge';

@Component({
	selector: 'app-sonar-thumbnails',
	templateUrl: './sonar-thumbnails.component.html',
	styleUrls: ['./sonar-thumbnails.component.css']
})
export class SonarThumbnailsComponent extends BaseDirective implements OnInit, OnDestroy {

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

	private languageCounts: SonarThumbnailsComponent.LanguageCount;

	SETTINGS = Constants.PROJECT_SONAR_PANEL.SETTINGS;
	SONAR = Constants.PROJECT_SONAR_PANEL.SONAR;
	NONE = Constants.PROJECT_SONAR_PANEL.NONE;

	public languageFilesNumber = new Map<string, FilesStats[]>();

	/**
	 * Map containting the evaluations for each Sonar project.
	 */
	evaluations = new Map<string, ThumbnailQuotationBadge>();

	constructor(
		private sonarService: SonarService,
		public projectService: ProjectService,
		private messageService: MessageService) {
		super();
	}

	ngOnInit() {

		this.subscriptions.add(
			this.projectService.projectLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.evaluations.clear();
						if (this.projectService.project.sonarProjects.length > 0) {
							this.idPanelSelected = this.SONAR;
							this.keySummarySelected = this.projectService.project.sonarProjects[0].key;
							this.updateDisplayConsolidationBadge();
						}
						this.loadFilesNumber();
					}
				}
			}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe( (panelSwitchEvent: PanelSwitchEvent) => {
				// I comment the line below because the method updateDisplayConsolidationBadge() is already invoked above
				// this.updateDisplayConsolidationBadge();
				if (!panelSwitchEvent.keySonar) {
					if (traceOn()) {
						console.log ('No Sonar project declared!');
					}
				} else {
					this.idPanelSelected = panelSwitchEvent.idPanel;
					this.keySummarySelected = panelSwitchEvent.keySonar;
				}
		}));
	}

	/**
	 * Update the Sonar badge on each thumbnail.
	 */
	updateDisplayConsolidationBadge() {
		this.projectService.project.sonarProjects.forEach (sonarProject => {
			const quotation = this.sonarService.evaluateSonarProject(this.projectService.project, sonarProject.key);
			const risk = (quotation === 100) ? 0 : (10 - Math.ceil(quotation / 10));
			this.sonarService
				.loadProjectTotalNumberLinesOfCode$(this.projectService.project, sonarProject.key)
				.pipe(take(1))
				.subscribe (totalNumberLinesOfCode => {
					sonarProject.sonarEvaluation = new SonarEvaluation(quotation, totalNumberLinesOfCode);
					this.projectService.saveSonarEvaluation$(
						this.projectService.project.id, sonarProject.key, quotation, totalNumberLinesOfCode)
						.pipe(take(1))
						.subscribe(doneAndOk => {
							if (doneAndOk) {
								if (traceOn()) {
									console.log ('Saving the quotation for project ' + sonarProject.name);
								}
								this.messageService.info('Saving the quotation for project ' + sonarProject.name);
							} else {
								if (traceOn()) {
									console.log('Error when saving the quotation for project ' + sonarProject.name);
								}
								this.messageService.error('Error when saving the quotation for project ' + sonarProject.name);
							}
						});
					this.evaluations.set (sonarProject.key,
							new ThumbnailQuotationBadge(
								quotation,
								this.projectService.getRiskColor(risk),
								totalNumberLinesOfCode,
								'Lines of code'));
					});
		});
	}

	loadFilesNumber() {
		this.projectService.project.sonarProjects.forEach((sonar: SonarProject) => {
			this.languageFilesNumber.set(sonar.key, sonar.projectFilesStats);
			if (traceOn()) {
				console.groupCollapsed('Files statistics for the Sonar instance %s.', sonar.key);
				sonar.projectFilesStats.forEach(fs => console.log (fs.language, fs.numberOfFiles));
				console.groupEnd();
			}
		});
		this.subscriptions.add(
			this.sonarService.sonarIsAccessible$(this.projectService.project).subscribe( connected => {
				if (connected) {
					this.projectService.project.sonarProjects.forEach (
						sonarP => this.retrieveAndUpdateFilesSummary(sonarP));
				}
			}));
	}
	/**
	 * Retrieve and save (IF NECESSARY) the number of files per language examined by the passed sonar component
	 * @param keyComponentSonar : the component key
	 */
	retrieveAndUpdateFilesSummary(componentSonar: SonarProject) {
		this.sonarService.loadProjectFiles(this.projectService.project, componentSonar.key).subscribe( filesCount => {
			componentSonar.projectFilesStats = this.keepTop3TypesOfFile(filesCount);
			if (traceOn()) {
				console.groupCollapsed('Top 3 languages for Sonar project %s', componentSonar.key);
				componentSonar.projectFilesStats.forEach(counting => {
					console.log (counting.language, counting.numberOfFiles);
				});
				console.groupEnd();
			}
			this.languageFilesNumber.set(componentSonar.key, componentSonar.projectFilesStats);
			this.subscriptions.add(
				this.projectService
					.saveFilesStats$(this.projectService.project.id, componentSonar.key, componentSonar.projectFilesStats)
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

		if (traceOn()) {
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
			'cs': 'C#',
		};
		return labels[key];
	}

	/**
	 * This function is conditionning the preview of thumbnails.
	 *
	 * All evaluations has to be retrieved before preview.
	 * And we know that all evaluations are complete
	 * when the evaluations map has the same size than the Sonar project.
	 */
	allEvaluationsCompleted(): boolean {
		if (!this.projectService.project) {
			return false;
		}
		return (this.evaluations.size === this.projectService.project.sonarProjects.length);
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
