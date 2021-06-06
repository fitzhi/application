import { AfterViewInit, Component, EventEmitter, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, EMPTY, Observable, of, Subscription } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { ContributorsDTO } from 'src/app/data/external/contributorsDTO';
import { traceOn } from 'src/app/global';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import Sunburst from 'sunburst-chart';
import { BaseComponent } from '../../base/base.component';
import { Constants } from '../../constants';
import { Contributor } from '../../data/contributor';
import { Filename } from '../../data/filename';
import { Project } from '../../data/project';
import { SettingsGeneration } from '../../data/settingsGeneration';
import { MessageBoxService } from '../../interaction/message-box/service/message-box.service';
import { MessageService } from '../../interaction/message/message.service';
import { CinematicService } from '../../service/cinematic.service';
import { ProjectService } from '../../service/project/project.service';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { DialogFilterComponent } from './dialog-filter/dialog-filter.component';
import { ProjectGhostsDataSource } from './project-ghosts/project-ghosts-data-source';
import { SunburstCacheService } from './service/sunburst-cache.service';
import { SunburstCinematicService } from './service/sunburst-cinematic.service';


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
	SUNBURST_PROJECT_READONLY = 'project readonly',
}

@Component({
	selector: 'app-project-sunburst',
	templateUrl: './project-sunburst.component.html',
	styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

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

	// After confirmation, we reload the dashboard chart.
	public RELOAD = 7;

	// Identifier of the panel selected.
	private idPanelSelected = -1;

	public titleSunburst = '';

	private myChart: any;

	/**
     * List of filenames located in a directory of the repository
     * Theses classnames are shared with the NodeDetail component when the user click on a slice.
     */
	public filenames = new MatTableDataSource<Filename>();

	/**
     * List of contributors.
     * Theses contributors are shared with the NodeDetail component when the user click on a slice.
     */
	public contributors = new MatTableDataSource<Contributor>();

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

	public allStaff: Collaborator[];

	constructor(
		private cinematicService: CinematicService,
		public sunburstCinematicService: SunburstCinematicService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private projectStaffService: ProjectStaffService,
		private messageBoxService: MessageBoxService,
		private dialog: MatDialog,
		private cacheService: SunburstCacheService,
		private staffListService: StaffListService,
		private staffService: StaffService,
		public projectService: ProjectService) {
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

		this.subscriptions.add(
			this.staffListService.allStaff$.subscribe({
				next: staff => this.allStaff = staff
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
						this.settings.idProject = this.projectService.project.id;
						if (!this.projectService.project.active) {
							this.messageService.info('This project is readonly. The Sunburst chart cannot be evaluated anymore.');
							this.setActiveContext (PreviewContext.SUNBURST_PROJECT_READONLY);
						} else {
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
				}
			));
	}

	/**
	 * return `true` if it is possible to draw the chart, `false` otherwise.
	 * @param Project the current project
	 */
	private isChartImpossible(project: Project): boolean {
		return ((!project) || (project.id === -1) || (!project.urlRepository) || (project.urlRepository.length === 0));
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
					if (traceOn()) {
						console.log(`Sunburst generation for ${this.projectService.project.id} has returned false`);
					}
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
			.subscribe({
				next: data => {
					this.setActiveContext (PreviewContext.SUNBURST_READY);
					setTimeout(() => {
						this.cacheService.saveResponse(data);
						this.generateChart(data);
					}, 0);
				},
				error: error => {
					if (error instanceof String) {
						this.messageService.error(<string>error);
					}
					if (traceOn()) {
						console.log (error);
					}
				}
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
				this.filenames.data = filenames;
				const contributors = new Map<number, Contributor>();
				nodeClicked.classnames.forEach(file => {
					if ( (file.idStaffs) && (file.idStaffs.length > 0) ) {
						file.idStaffs.filter(idStaff => idStaff !== -1).forEach(idStaff => {
							if (!contributors.has(idStaff)) {
								contributors.set(idStaff, this.projectStaffService.findContributor(idStaff));
							}
						});
					}
				});


				if (traceOn()) {
					console.groupCollapsed('Contributors : ');
					console.log (...contributors.values());
					console.groupEnd();
				}
				this.contributors.data = Array.from(contributors.values());
			} else {
				if (traceOn()) {
					console.log('Content of filenames & contibutors have been reset.');
				}
				this.filenames.data = [];
				this.contributors.data = [];
			}
		}
	}

	handleSunburstData(response: any) {

		if (!this.myChart) {
			this.myChart = Sunburst();
			this.myChart.onNodeClick(nodeClicked => {
				this.onNodeClick(nodeClicked);
				this.myChart.focusOnNode(nodeClicked);
			});
		}

		// This test is a hack necessary for the tests suite.
		if (!document.getElementById('chart')) {
			console.warn ('The application is in an unexpected state');
			console.warn('Chart is not present in the HTML layout for the projet %s', this.projectService.project.name);
		} else {
			this.myChart.data(response.sunburstData)
				.width(500)
				.height(500)
				.minSliceAngle(2)
				.label('location')
				.size('importance')
				.color('color')
				(document.getElementById('chart'));

			const dataSourceGhosts = new ProjectGhostsDataSource(response.ghosts, this.allStaff);
			this.dataSourceGhosts$.next(dataSourceGhosts);
			//
			// We update the underlying project in the projects array
			// because the Sunburst generation has probably updated the skills involved in the project.
			//
			this.projectService.actualizeProject(this.projectService.project.id);
		}

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

		if (traceOn()) {
			console.log ('Showing panel %d', idPanel);
		}
		// If the project is inactive, these buttons are inactive.
		if (!this.projectService.project.active) {
			return;
		}
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
			case this.RELOAD:
				if (traceOn()) {
					console.log ('Request for a reload');
				}
				this.idPanelSelected = idPanel;
				this.reload();
				break;
			case this.RESET:
				if (traceOn()) {
					console.log ('Request for a reset');
				}
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

	/**
	 * This function will erase all intermediate data and reload the chart.
	 */
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
							this.messageService.info('Dashboard reinitialization was just requested. The operation might last a while.');
							this.setActiveContext (PreviewContext.SUNBURST_WAITING);
						});
					}
				this.idPanelSelected = this.SUNBURST;
			});
	}

	/**
	 * This function will reload the chart.
	 */
	reload() {
		if (!this.projectService.project) {
			this.messageService.info('Nothing to reload !');
			this.idPanelSelected = this.SUNBURST;
			return;
		}
		this.messageBoxService.question('Reload the dashboard', 'Please confirm your request')
			.pipe(take(1))
			.subscribe(answer => {
				if (answer) {
					this.cacheService.clearReponse();
					this.projectService
						.reloadSunburst$(this.settings.idProject)
						.pipe(take(1))
						.subscribe(response => {
							if ((!response) && (traceOn())) {
									console.log ('This request was not necessary : no dashboard available.');
							}
							this.messageService.info('Dashboard reload has been requested. The operation might take a while.');
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
					response => this.myChart.data(response),
					error => this.handleErrorData(error),
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
			this.titleSunburst += ' filtered from ' + new Date(this.settings.startingDate).toLocaleDateString('en-EN');
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
	 * * `sunburst_project_readonly` : This project has been inactivated, and therefore is readonly.
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
		this.cinematicService.projectTabIndex = tabIndex;
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
