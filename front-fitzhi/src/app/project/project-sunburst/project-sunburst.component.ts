import { Component, OnInit, AfterViewInit, Input, OnDestroy, Output, EventEmitter, ɵɵcontainerRefreshEnd } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { Constants } from '../../constants';
import { MessageService } from '../../message/message.service';
import { ProjectService } from '../../service/project.service';
import { ActivatedRoute } from '@angular/router';
import { CinematicService } from '../../service/cinematic.service';
import { Project } from '../../data/project';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { ProjectGhostsDataSource } from './project-ghosts/project-ghosts-data-source';
import { MessageBoxService } from '../../message-box/service/message-box.service';
import { DialogFilterComponent } from './dialog-filter/dialog-filter.component';
import { BaseComponent } from '../../base/base.component';
import { SettingsGeneration } from '../../data/settingsGeneration';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { Filename } from '../../data/filename';
import { FilenamesDataSource } from './node-detail/filenames-data-source';
import { ContributorsDataSource } from './node-detail/contributors-data-source';
import { BehaviorSubject, Subject, Subscription, interval, Observable, of, EMPTY } from 'rxjs';
import { Contributor } from '../../data/contributor';
import { take, switchMap } from 'rxjs/operators';
import { MatTableDataSource } from '@angular/material/table';
import { ContributorsDTO } from 'src/app/data/external/contributorsDTO';
import { traceOn } from 'src/app/global';
import { SunburstCinematicService } from './service/sunburst-cinematic.service';
import { SunburstCacheService } from './service/sunburst-cache.service';


//
// this context is indicating that the sunburst chart is ready to be viewed.
//
export enum PreviewContext {
	SUNBURST_READY = 'chart ready',
	SUNBURST_IMPOSSIBLE = 'chart impossible',
	SUNBURST_WAITING = 'chart under construction',
	SUNBURST = 'chart, the chart',
	SUNBURST_DEPENDENCIES = 'chart dependencies',
	SUNBURST_LEGEND = 'chart legend',
	SUNBURST_GHOSTS = 'chart ghost',
}

@Component({
	selector: 'app-project-sunburst',
	templateUrl: './project-sunburst.component.html',
	styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

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
	 * This datasource of ghosts will be pass and listen in the project-ghosts-component, and table-project-componeet.
	 */
	public dataSourceGhosts$ = new BehaviorSubject<MatTableDataSource<unknown>>(new MatTableDataSource<unknown>([]));

	public PROJECT_IDX_TAB_FORM = Constants.PROJECT_IDX_TAB_FORM;

	/**
     * Parameters passed to the generation method on the back-end.
     */
	private settings = new SettingsGeneration(-1, new Date(0).getTime(), 0);

	// Previous context related to the sunburst construction.
	public lastSunburstContext = '';

	// Active current context
	public activeContext = '';

	// Waiting images previewed during the chart generation.
	public sunburstWaitingImage = './assets/img/sunburst-waiting-image.png';

	// Rules of risks panel has to be displayed.
	public LEGEND_SUNBURST = 1;

	// Settings panel has to be displayed.
	public SETTINGS = 2;

	// Unknown contributors panel has to be displayed.
	public UNKNOWN = 3;

	// Dependencies either detected or declared in the project.
	public DEPENDENCIES = 4;

	// After confirmation, we reset the dashboard data.
	public RESET = 5;

	// We want to preview the chart if ready
	public SUNBURST = 6;

	// Identifier of the panel selected.
	private idPanelSelected = -1;

	public titleSunburst = '';

	private myChart: Sunburst;

	/**
     * List of filenames located in a directory of the repository
     * Theses classnames are shared with the NodeDetail component when the user click on a slice.
     */
	public filenames = new FilenamesDataSource();

	/**
	* `BehaviorSubject` which emits the filenames datasource.
 	*/
	// public filenames$ = new BehaviorSubject<FilenamesDataSource>(new FilenamesDataSource());

	/**
     * List of contributors.
     * Theses contributors are shared with the NodeDetail component when the user click on a slice.
     */
	public contributors = new ContributorsDataSource();


	public location$ = new BehaviorSubject('');

	/**
	 * Subscription active to read the task-activities.
	 */
	subscriptionTaskActivities: Subscription;

	/**
	 * reload interval to display the last log recorded for the asynchronous task.
	 * This interval will periodicaly call the method `projectService.loadTaskActivities`
	 */
	private intervalActivityLoadReload;

	/**
	 * The chart can be generated synchronously, or asynchronously.
	 * if `loadDashboardData$` works asynchronously, a reload has to executued before preview.
	 */
	private shouldReload = false;

	public PreviewContext =  PreviewContext;

	constructor(
		private cinematicService: CinematicService,
		public sunburstCinematicService: SunburstCinematicService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private projectStaffService: ProjectStaffService,
		private messageBoxService: MessageBoxService,
		private dialog: MatDialog,
		private cacheService: SunburstCacheService,
		private projectService: ProjectService) {
			super();
	}

	ngOnInit() {

		this.route.params.pipe(take(1)).subscribe(params => {
			if (traceOn()) {
				console.log('params[\'id\'] ' + params['id']);
			}
			if (params['id'] == null) {
				this.settings.idProject = null;
			} else {
				this.settings.idProject = + params['id']; // (+) converts string 'id' to a number
			}
		});

		this.subscriptions.add(
			this.projectService.projectLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						if (traceOn()) {
							console.log('Project %s %s received in sunburst-component',
								this.projectService.project.id,
								this.projectService.project.name);
						}
						//
						// We postpone the Project updates to avoid the warning
						// ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked.
						//
						setTimeout(() => {
							this.settings.idProject = this.projectService.project.id;

							if (this.isChartImpossible(this.projectService.project)) {
								this.messageService.info('No repository URL available !');
								this.setActiveContext (PreviewContext.SUNBURST_IMPOSSIBLE);
							}
						}, 0);
					}
				}
		}));

		// The child in charge of listening the events can force the chart refresh.
		this.subscriptions.add(
			this.sunburstCinematicService.refreshChart$.subscribe({
				next: forceRefresh => {
					if (forceRefresh) {
						this.show(this.SUNBURST);
					}
				}
			})
		);
	}

	/**
     * After creation treatment.
     */
	ngAfterViewInit() {
		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(
				index => {
					if (index === Constants.PROJECT_IDX_TAB_SUNBURST) {
						if (traceOn()) {
							const today = new Date();
							console.log('Tab selected ' + index + ' @ ' + today.getHours()
								+ ':' + today.getMinutes() + ':' + today.getSeconds());
						}
						if (this.isChartImpossible(this.projectService.project)) {
							if (traceOn()) {
								console.log('No project identifier passed to this tab. No data available to preview !');
							}
							this.setActiveContext (PreviewContext.SUNBURST_IMPOSSIBLE);
						} else {
							setTimeout( () => this.loadDataChart(), 0);
						}
					}
				}
			));
	}

	/**
	 * return `true` if it is possible to draw the chart, `false` otherwise.
	 * @param Project the current project
	 */
	private isChartImpossible(project: Project): boolean {
		return ((!project) || (!project.urlRepository) || (project.urlRepository.length === 0));
	}


	/**
	 * Refresh the Sunburst chart.
	 */
	refreshChart() {
		setTimeout(() => { this.loadDataChart(); }, 0);
	}

	loadData$(): Observable<any> {
		return this.loadContributors$().pipe(
			take(1),
			switchMap((doneAndOk: boolean) => {
				if (doneAndOk) {
					return this.projectService.loadDashboardData$(this.settings);
				} else {
					return of(EMPTY);
				}
			}));
	}

	/**
	 * Return `true` when the loading is complete.
	 * Load the contributors of the project
	 */
	loadContributors$(): Observable<boolean> {
		return this.projectService.contributors$(this.projectService.project.id).pipe(
			take(1),
			switchMap((contributorsDTO: ContributorsDTO) => {
				this.projectStaffService.contributors = [];
				this.projectStaffService.contributors.push(...contributorsDTO.contributors);
				if (traceOn()) {
					this.projectStaffService.dumpContributors();
				}
				return of(true);
			}));
	}


	/**
     * Load the dashboard data in order to produce the sunburst chart.
     */
	loadDataChart() {

		this.idPanelSelected = this.SUNBURST;

		//
		// We use the cache if an entry is present
		//
		if (this.cacheService.hasResponse()) {
			this.setActiveContext (PreviewContext.SUNBURST_READY);
			setTimeout(() => this.generateChart(this.cacheService.getReponse()), 0);
			return;
		}

		if ((!this.projectService.project) || (!this.projectService.project.id)) {
			this.setActiveContext (PreviewContext.SUNBURST_IMPOSSIBLE);
			return;
		} else {
			// We display the waiting panel
			this.setActiveContext (PreviewContext.SUNBURST_WAITING);
			// We starting to listen the events produced by the server
			this.sunburstCinematicService.listenEventsFromServer$.next(true);
		}

		this.shouldReload = false;
		this.loadData$()
			.pipe(take(1))
			.subscribe(
				response => {
					switch (response.code) {
						case 0:
							this.setActiveContext (PreviewContext.SUNBURST_READY);
							setTimeout(() => {
								this.cacheService.saveResponse(response);
								this.generateChart(response);
							}, 0);
							break;
						case 201:
						case -1008:
							//
							// The generation has started in asynchronous mode.
							// We will receive notification, so we do not set taskReportManagement as complete.
							//
							this.messageService.warning(response.message);
							this.shouldReload = true;
							break;
						default:
							console.error('Unknown code message %d for message %s',
								response.code, response.message);
							this.messageService.error(response.message);
							break;
					}
				},
				responseInError => {
					this.handleErrorData(responseInError);
				});
	}

	/**
	 * Generation of the chart based from the data received.
	 * @param response the HTTP response of the server
	 */
	generateChart(response: any) {
		if (traceOn()) {
			console.log ('The risk of the current project is', response.projectRiskLevel);
		}
		this.handleSunburstData(response);
		this.updateRiskLevel.next(response.projectRiskLevel);
		this.projectService.project.staffEvaluation = response.projectRiskLevel;
		this.hackSunburstStyle();
		this.tooltipChart();
	}

	/**
    * End-user click on a node of a chart.
	* @param nodeClicked the node which have been clicked
    **/
	public onNodeClick(nodeClicked: any) {
		if (nodeClicked) {
			this.location$.next(nodeClicked.location);
			if (nodeClicked.classnames) {
				if (traceOn()) {
					console.groupCollapsed('Filenames : ');
					nodeClicked.classnames.forEach(element => {
						console.log(element.filename + ' ' + element.lastCommit);
					});
					console.groupEnd();
				}

				const filenames = [];
				nodeClicked.classnames.forEach(element => {
					filenames.push(new Filename(element.filename, element.lastCommit));
				});
				this.filenames.setClassnames(filenames);

				const contributors = new Set<Contributor>();
				nodeClicked.classnames.forEach(file => {
					if ( (file.idStaffs) && (file.idStaffs.length > 0) ) {
						file.idStaffs.filter(idStaff => idStaff !== -1).forEach(idStaff => {
							contributors.add(this.findContributor(idStaff));
						});
					}
				});
				if (traceOn()) {
					console.groupCollapsed('Contributors : ');
					console.log (...contributors);
					console.groupEnd();
				}
				this.contributors.sendContributors(Array.from(contributors));
			} else {
				if (traceOn()) {
					console.log('Content of filenames & contibutors have been reset.');
				}
				this.filenames.setClassnames([]);
				this.contributors.sendContributors([]);
			}
		}
	}

	/**
	 * Search for a contributor with the same identifier as the given one
	 * @param idStaff the searched staff identifier
	 */
	findContributor(idStaff: number): Contributor {
		const foundContributor = this.projectStaffService.contributors
			.find(contributor => contributor.idStaff === idStaff);
		if (!foundContributor) {
			console.log (idStaff, 'id Staff not found as a contributor.' );
		}
		return foundContributor;
	}

	handleSunburstData(response: any) {

		if (!this.myChart) {
			this.myChart = Sunburst();
			this.myChart.onNodeClick(nodeClicked => {
				this.onNodeClick(nodeClicked);
				this.myChart.focusOnNode(nodeClicked);
			});
		}

		this.myChart.data(response.sunburstData)
			.width(500)
			.height(500)
			.label('location')
			.size('importance')
			.color('color')
			(document.getElementById('chart'));

		const dataSourceGhosts = new ProjectGhostsDataSource(this.projectService.project);
		// Send the unregistered contributors to the panel list
		dataSourceGhosts.sendUnknowns(response.ghosts);
		this.dataSourceGhosts$.next(dataSourceGhosts);

		//
		// We update the underlying project in the projects array
		// because the Sunburst generation has probably updated the skills involved in the project.
		//
		this.projectService.actualizeProject(this.projectService.project.id);
	}

	handleErrorData(response: any) {
		if (traceOn()) {
			console.log('Response returned while retrieving the sunburst data for the project identifier ' +
				this.settings.idProject);
		}
		switch (response.status) {
			case 404: {
				this.messageService.error(
					'Resource not found while retrieving the sunburst data for the project identfier ' +
					this.settings.idProject);
				break;
			}
			case 400: {
				if (traceOn()) {
					console.log ('Response received', response);
				}
				switch (response.error.code) {
					case 201:
						// The generation is not accessible. A dashboard generation is launched asynchronously.
						this.messageBoxService.exclamation('Operation launched', response.error.message);
						break;
					case -1008:
						// Operation already been launched.
						this.messageService.info(response.error.message);
						break;
					case -999:
						// Operation already been launched.
						if (!response.error.message) {
							this.messageBoxService.exclamation('Chart generation failed',
								'Operation failed for an unknown reason : ' +
								'You should reset completly the chart generation. (click on the \'trash\' icon)');
						} else {
							this.messageService.info(response.error.message);
						}
						break;
					default:
						// We display the error generated on the server
						this.messageService.error('ERROR ' + response.error.message);
				}
				break;
			}
			default: {
				// Unattempted error
				this.messageService.error('ERROR ' + response.message);
				break;
			}
		}
	}

	tooltipChart() {
		this.myChart.tooltipContent(function (graph) {
			if (graph.lastUpdate != null) {
				const date = new Date(graph.lastUpdate);
				return 'Last commit ' + date.toLocaleDateString();
			} else {
				return 'No commit here!';
			}
		});
	}

	/*
    * Replace the cssText for rule matching selectorText.
    * 2 rules are changes :
    *   1) We remove the white contour around the label inside the slices (stroke:'none')
    *   2) We put the tooltip on top of the components stack (z-index:'500')
    */
	hackSunburstStyle() {
		const sheets = document.styleSheets;
		let sheet, rules, rule;
		let i, j, k, iLen, jLen, kLen;

		for (i = 0, iLen = sheets.length; i < iLen; i++) {
			sheet = sheets[i];

			// W3C model
			if (sheet.cssRules) {
				rules = sheet.cssRules;
				for (j = 0, jLen = rules.length; j < jLen; j++) {
					rule = rules[j];
					if (typeof rule.selectorText !== 'undefined') {

						if (rule.selectorText.indexOf('sunburst-viz text .text-contour') > 0) {
							rule.style.stroke = 'none';
						}
						if (rule.selectorText.indexOf('sunburst-tooltip') > 0) {
							rule.style.zIndex = '500';
						}
					}
				}

				// IE model
			} else if (sheet.rules) {
				rules = sheet.rules;
				for (k = 0, kLen = rules.length; k < kLen; k++) {
					rule = rules[k];
					if (typeof rule.selectorText !== 'undefined') {
						if (rule.selectorText.indexOf('sunburst-viz text .text-contour') > 0) {
							rule.style.stroke = 'none';
						}
						if (rule.selectorText.indexOf('sunburst-tooltip') > 0) {
							rule.style.zIndex = '500';
						}
					}
				}
			}
		}
	}

	/**
    * Show the panel associated to this id.
    * @param idPanel Panel identifier
    */
	public show(idPanel: number) {
		this.sunburstCinematicService.initActivatedButton();
		switch (idPanel) {
			case this.SUNBURST:
				this.idPanelSelected = idPanel;
				this.refreshChart();
				this.setActiveContext(this.lastSunburstContext);
				break;
			case this.LEGEND_SUNBURST:
				if (!this.isWarningWhenRunning()) {
					this.idPanelSelected = idPanel;
					this.setActiveContext(PreviewContext.SUNBURST_LEGEND);
				}
				break;
			case this.SETTINGS:
				if (!this.isWarningWhenRunning()) {
					this.idPanelSelected = idPanel;
					this.dialogFilter();
				}
				break;
			case this.UNKNOWN:
				if (!this.isWarningWhenRunning()) {
					this.idPanelSelected = idPanel;
					this.setActiveContext(PreviewContext.SUNBURST_GHOSTS);
				}
				break;
			case this.DEPENDENCIES:
				if (!this.isWarningWhenRunning()) {
					this.idPanelSelected = idPanel;
					this.setActiveContext(PreviewContext.SUNBURST_DEPENDENCIES);
				}
				break;
			case this.RESET:
				this.idPanelSelected = idPanel;
				this.reset();
				break;
			default:
				this.idPanelSelected = idPanel;
				break;
		}
	}

	/**
	 * Display a waiting message when processing the chart.
	 */
	isWarningWhenRunning(): boolean {
		if (this.isActiveContext(PreviewContext.SUNBURST_WAITING)) {
			this.messageService.info('Just a second ! Dashboard generation is currently processed.');
			return true;
		} else {
			return false;
		}
	}

	reset() {
		if (!this.projectService.project) {
			this.messageService.info('Nothing to reset !');
			this.idPanelSelected = this.SUNBURST;
			return;
		}
		this.messageBoxService.question('Reset the dashboard', 'Please confirm the dashboard reinitialization')
			.pipe(take(1))
			.subscribe(answer => {
				if (answer) {
					this.cacheService.clearReponse();
					this.projectService
						.resetDashboard(this.settings.idProject)
						.pipe(take(1))
						.subscribe(response => {
							if ((!response) && (traceOn())) {
									console.log ('This request was not necessary : no dashboard available.');
							}
							this.messageService.info('Dashboard reinitialization has been requested. The operation might take a while.');
							this.setActiveContext (PreviewContext.SUNBURST_WAITING);
						});
					}
				this.idPanelSelected = this.SUNBURST;
			});
	}

	dialogFilter() {
		if (!this.projectService.project) {
			this.messageService.info('Nothing to filter !');
			this.idPanelSelected = this.SUNBURST;
			return;
		}

		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.position = { top: '6em', left: '5em' };
		dialogConfig.panelClass = 'default-dialog-container-class';
		const dlg = this.dialog.open(DialogFilterComponent, dialogConfig);
		dlg.afterClosed().pipe(take(1)).subscribe(settings => {
			this.idPanelSelected = this.SUNBURST;
			this.settings.idStaffSelected =
				((typeof settings.idStaffSelected === 'undefined') || (settings.idStaffSelected.length === 0))
					? 0 : settings.idStaffSelected;
			this.settings.startingDate = settings.startingDate;
			this.generateTitleSunburst();
			this.projectService.loadDashboardData$(this.settings)
				.subscribe(
					response => this.myChart.data(response.sunburstData),
					response => this.handleErrorData(response),
					() => {
						this.hackSunburstStyle();
						this.tooltipChart();
				});
		});
	}

	private generateTitleSunburst() {
		this.titleSunburst = 'Chart';
		if (this.settings.idStaffSelected > 0) {
			const selectedDeveloper = this.projectStaffService.contributors
				.find(contributor => contributor.idStaff === this.settings.idStaffSelected).fullname;
			this.titleSunburst += ' for ' + selectedDeveloper;
		}
		if (this.settings.startingDate > 0) {
			const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
			this.titleSunburst += ' filtered from ' + new Date(this.settings.startingDate).toLocaleDateString('en-EN', options);
		}
	}

	/**
    * The button associated to this panel id is activated.
    * @param idPanel panel identifier
    **/
	public buttonActivated(idPanel: number) {
		return (idPanel === this.idPanelSelected);
	}

	/**
	 * Set the new active context inside the form component.
	 */
	public setActiveContext(context: string) {

		// Nothing to do.
		if (context === this.activeContext) {
			return;
		}

		if (traceOn()) {
			console.log ('New active context \'' + context + '\' after \'' + this.lastSunburstContext + '\'');
		}

		this.lastSunburstContext = this.activeContext;
		this.activeContext = context;
	}

	/**
	 * Test if the passed context is the current active context.
	 * There are 4 context possible in this form container :
	 * * `sunburst_waiting` : the graph representing the risk of staff coverage is currently being build</li>
	 * * `sunburst_ready` : the graph is ready to be displayed
	 * * `sunburst_impossible` : either lack of connection information, or lack of internet, or something else : the graph cannot be displayed.
	 * * `sunburst_detail_dependencies` : the table of libraries detected or declared is available in the container.
	 */
	public isActiveContext(context: PreviewContext) {
		return (context === this.activeContext);
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.tabActivationEmitter.next(tabIndex);
	}

	mouseEnter(activeButton: number) {
		this.sunburstCinematicService.activatedButton = activeButton;
	}

	mouseLeave() {
		this.sunburstCinematicService.initActivatedButton();
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
