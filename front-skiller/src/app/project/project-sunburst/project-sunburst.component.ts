import { Component, OnInit, AfterViewInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
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
import { BehaviorSubject, Subject, Subscription } from 'rxjs';
import { Contributor } from '../../data/contributor';
import { take, retryWhen } from 'rxjs/operators';
import { WebDriverLogger } from 'blocking-proxy/built/lib/webdriver_logger';
import { Task } from 'src/app/data/task';
import { TaskLog } from 'src/app/data/task-log';

/**
 * Internal class in charge of the display of log messages reported by the asynchronous task.
 */
class TaskReportManagement {

	/**
	 * This subject is emetting activity informations received by the method `projectService.loadTaskActivities$`
	 * The content of this observable is displayed in the `div taskReport`
	 */
	taskReport$ = new BehaviorSubject<string>('');

	/**
	 * This `boolean` is informing 2 `*ngIf` located inside a div of class `taskReport`
	 * There 2 div are excluding each other.
	 *  * if `true`, this log message is a successfull message (with a font color in __green__)
	 *  * if `false`, this is an error message (with a font color in __red__)
	 */
	taskOk: boolean;

	/**
	 * The last number of messages.
	 * We test if we receive the same collection of messages by simply testing the number of records.
	 */
	public taskLogsCount = 0;

	/**
	 * Number of useless calls.
	 * (successive calls which return the same number of records).
	 */
	public numberOfUselessCall = 0;

	/**
	 * Starting delay elapse time between each execution of the `intervalActivityLoadReload`
	 */
	private DEFAULT_DELAY_INTERVAL = 1000;

	/**
	 * Starting delay elapse time between each execution of the `intervalActivityLoadReload`
	 */
	public adaptativeDelay: number = this.DEFAULT_DELAY_INTERVAL;

	/**
	 * Dump the content of the task.
	 * @param task the given task.
	 */
	private dump(task: Task) {
		task.logs.forEach(log => {
			if (Constants.DEBUG) {
				console.groupCollapsed('Activities recorded');
				console.log (log.message);
				console.groupEnd();
		}});
	}

	/**
	 * Return the latest log message or `null` if there is no new message.
	 * @param task the task read from the back-end.
	 */
	public lastLog(task: Task): TaskLog {
		//
		// Operation is completed.
		// We return the last breath of the task
		//
		if (task.complete) {
			this.taskLogsCount = 0;
			this.numberOfUselessCall = 0;
			this.dump(task);
			return task.lastBreath;
		}
		if (task.logs.length !== this.taskLogsCount) {
			this.taskLogsCount = task.logs.length;
			// We reinitialize the adaptative delay.
			this.adaptativeDelay = this.DEFAULT_DELAY_INTERVAL;
			this.dump(task);
			//
			// We return the last recorded log.
			//
			return task.logs[task.logs.length - 1];
		} else {
			this.numberOfUselessCall++;
			// After 5 successive useless calls, we increment the adaptativeDelay by the `DEFAULT_DELAY_INTERVAL`.
			if (this.numberOfUselessCall === 5) {
				this.numberOfUselessCall = 0;
				this.adaptativeDelay += this.DEFAULT_DELAY_INTERVAL;
			}
			return null;
		}
	}

	/**
	 * Initialization.
	 */
	init(): void {
		this.taskLogsCount = 0;
		this.numberOfUselessCall = 0;
	}
}


@Component({
	selector: 'app-project-sunburst',
	templateUrl: './project-sunburst.component.html',
	styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
     * The project loaded in the parent component.
     */
	@Input() project$: BehaviorSubject<Project>;

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
	public dataSourceGhosts$ = new Subject<ProjectGhostsDataSource>();

	public PROJECT_IDX_TAB_FORM = Constants.PROJECT_IDX_TAB_FORM;

	/**
    * Project loaded on the parent component.
    */
	private project: Project;

	/**
     * Parameters passed to the generation method on the back-end.
     */
	private settings = new SettingsGeneration(-1, new Date(0).getTime(), 0);

	// Previous context related to the sunburst construction.
	public lastSunburstContext = 0;


	// Active current context
	public activeContext = 0;

	// this context is indicating that the sunburst chart is ready to be viewed.
	public CONTEXT = {
		SUNBURST_READY: 1,
		SUNBURST_IMPOSSIBLE: 2,
		SUNBURST_WAITING: 3,
		SUNBURST_DEPENDENCIES: 4,
		SUNBURST: 5,
		SUNBURST_LEGEND: 6,
		SUNBURST_GHOSTS: 7
	};

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

	public projectName: string;

	public titleSunburst = '';

	private myChart: Sunburst;

	/**
     * List of filenames located in a directory of the repository
     * Theses classnames are shared with the NodeDetail component when the user click on a slice.
     */
	public filenames = new FilenamesDataSource();

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
	 * Internal class in charge of the display of log messages reported by the asynchronous task.
	 */
	private taskReportManagement = new TaskReportManagement();

	constructor(
		private cinematicService: CinematicService,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private projectStaffService: ProjectStaffService,
		private messageBoxService: MessageBoxService,
		private dialog: MatDialog,
		private projectService: ProjectService) {
			super();
	}

	ngOnInit() {

		this.route.params.pipe(take(1)).subscribe(params => {
			if (Constants.DEBUG) {
				console.log('params[\'id\'] ' + params['id']);
			}
			if (params['id'] == null) {
				this.settings.idProject = null;
			} else {
				this.settings.idProject = + params['id']; // (+) converts string 'id' to a number
			}
		});

		if (this.project$) {
			this.subscriptions.add(
				this.project$.subscribe((project: Project) => {

					// The behaviorSubject project$ is initialized with a null.
					if (!project) {
						return;
					}

					if (Constants.DEBUG) {
						console.log('Project ' + project.id + ' ' + project.name + ' reveived in sunburst-component');
					}
					this.project = project;
					this.projectName = this.project.name;
					if ((!this.project.urlRepository) || (this.project.urlRepository.length === 0)) {
						this.messageService.info('No repository URL avalaible !');
						this.setActiveContext (this.CONTEXT.SUNBURST_IMPOSSIBLE);
					}
			}));
		}

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated$.subscribe(
				index => {
					if (index === Constants.PROJECT_IDX_TAB_SUNBURST) {
						if (Constants.DEBUG) {
							const today = new Date();
							console.log('Tab selected ' + index + ' @ ' + today.getHours()
								+ ':' + today.getMinutes() + ':' + today.getSeconds());
						}
						this.loadSunburst();
					}
				}
			));

		if (this.settings.idProject == null) {
			if (Constants.DEBUG) {
				console.log('No project identifier passed to this tab. No data available for preview !');
			}
			this.setActiveContext (this.CONTEXT.SUNBURST_IMPOSSIBLE);
			return;
		}
	}

	loadTaskActivities() {
		if (Constants.DEBUG) {
			console.log ('Starting the adaptative interval with ' + this.taskReportManagement.adaptativeDelay);
		}
		this.taskReportManagement.init();
		this.intervalActivityLoadReload = setInterval( () => {
			return this.projectService
				.loadTaskActivities$(this.project.id)
				.pipe( take(1))
				.subscribe(task => {
					const log = this.taskReportManagement.lastLog(task);
					if (log) {
						this.taskReportManagement.taskReport$.next(log.message);
						this.taskReportManagement.taskOk = (log.code === 0) ? true : false;
					}
					//
					// Task is complete without error. We turn-off the waiting div in favor of Sunburst.
					//
					if (task.complete && (task.lastBreath.code === 0)) {
						if (Constants.DEBUG) {
							console.log ('After completion, we reloadSunburst');
						}
						this.setActiveContext (this.CONTEXT.SUNBURST_READY);
						this.clearInterval();
					}
				},
				error => {
					//
					// In case of error, we the log tracking.
					//
					setTimeout(() => clearInterval(), 0);
				});
		}, this.taskReportManagement.adaptativeDelay);
	}

	/**
	 * Clearing the interval.
	 */
	private clearInterval() {
		if (Constants.DEBUG) {
			console.log ('Halting the intervalActivityLoadReload');
		}
		clearInterval(this.intervalActivityLoadReload);
	}

	/**
     * Load the dashboard data in order to produce the sunburst chart.
     */
	loadSunburst() {

		this.idPanelSelected = this.SUNBURST;

		if ((typeof this.project === 'undefined') || (typeof this.project.id === 'undefined')) {
			this.setActiveContext (this.CONTEXT.SUNBURST_IMPOSSIBLE);
			return;
		} else {
			this.setActiveContext (this.CONTEXT.SUNBURST_WAITING);
		}

		if (!this.myChart) {
			this.myChart = Sunburst();
			this.myChart.onNodeClick(nodeClicked => {
				this.onNodeClick(nodeClicked);
				this.myChart.focusOnNode(nodeClicked);
			});
		}

		this.loadTaskActivities();

		this.projectService.loadDashboardData$(this.settings)
			.subscribe(
				response => {
					this.handleSunburstData(response);
					if (Constants.DEBUG) {
						console.log ('The risk of the current project is', response.projectRiskLevel);
					}
					this.updateRiskLevel.next(response.projectRiskLevel);
				},
				response => {
					this.handleErrorData(response);
					this.taskReportManagement.taskReport$.next(response);
					this.taskReportManagement.taskOk = false;
				},
				() => {
					this.hackSunburstStyle();
					this.tooltipChart();
					this.setActiveContext (this.CONTEXT.SUNBURST_READY);
					this.clearInterval();
				});
	}

	/**
    * End-user click on a node of a chart.
	* @param nodeClicked the node which have been clicked
    **/
	public onNodeClick(nodeClicked: any) {
		if (nodeClicked) {
			this.location$.next(nodeClicked.location);
			if (nodeClicked.classnames !== null) {
				if (Constants.DEBUG) {
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
				this.filenames.sendClassnames(filenames);

				const contributors = new Set<Contributor>();
				nodeClicked.classnames.forEach(file => {
					if ( (file.idStaffs) && (file.idStaffs > 0) ) {
						file.idStaffs.forEach(element => {
							contributors.add(this.findContributor(element));
						});
					}
				});
				this.contributors.sendContributors(Array.from(contributors));

			} else {
				this.filenames.sendClassnames([]);
				this.contributors.sendContributors([]);
			}
		}
	}

	findContributor(idStaff: number): Contributor {
		const foundContributor = this.projectStaffService.contributors
			.find(contributor => contributor.idStaff === idStaff);
		if (!foundContributor) {
			console.log (idStaff, 'id Staff not found as a contributor.' );
		}
		return foundContributor;
	}

	handleSunburstData(response: any) {

		// Removing the last chart generated from the DOM.
		const node = document.getElementById('chart');
		while (node.hasChildNodes()) {
			node.removeChild(node.lastChild);
		}

		if (this.myChart !== null) {
			this.myChart.data(response.sunburstData).width(500).height(500).label('location').size('importance').color('color')
				(document.getElementById('chart'));
			const dataSourceGhosts = new ProjectGhostsDataSource(this.project);
			// Send the unregistered contributors to the panel list
			dataSourceGhosts.sendUnknowns(response.ghosts);
			this.dataSourceGhosts$.next(dataSourceGhosts);
		}

	}

	handleErrorData(response: any) {
		if (Constants.DEBUG) {
			console.log('Response returned while retrieving the sunburst data for the project identfier ' +
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
				switch (response.error.code) {
					case 201:
						// The generation is not accessible. A dashboard generation is launched asynchronously.
						this.messageBoxService.exclamation('Operation launched', response.error.message);
						break;
					case -999:
						// Operation already been launched.
						this.messageBoxService.exclamation('Operation already launched', response.error.message);
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

	/**
     * After creation treatment.
     */
	ngAfterViewInit() {
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
		switch (idPanel) {
			case this.SUNBURST:
					this.idPanelSelected = idPanel;
					this.setActiveContext(this.lastSunburstContext);
				break;
			case this.LEGEND_SUNBURST:
					this.idPanelSelected = idPanel;
					this.setActiveContext(this.CONTEXT.SUNBURST_LEGEND);
				break;
			case this.SETTINGS:
					this.idPanelSelected = idPanel;
					this.dialogFilter();
				break;
			case this.UNKNOWN:
				if (document.getElementById('chart').childElementCount === 0) {
					this.messageService.info('Just a second !   Dashboard generation is currently processed.');
					break;
				}
				this.idPanelSelected = idPanel;
				this.setActiveContext(this.CONTEXT.SUNBURST_GHOSTS);
				break;
			case this.DEPENDENCIES:
				this.idPanelSelected = idPanel;
				this.setActiveContext(this.CONTEXT.SUNBURST_DEPENDENCIES);
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

	reset() {
		if (!this.project) {
			this.messageService.info('Nothing to reset !');
			this.idPanelSelected = this.SUNBURST;
			return;
		}
		this.messageBoxService.question('Reset the dashboard',
			'Please confirm the dashboard reinitialization')
				.pipe(take(1))
				.subscribe(answer => {
				if (answer) {
					this.setActiveContext (this.CONTEXT.SUNBURST_WAITING);
					this.loadTaskActivities();
					this.projectService
						.resetDashboard(this.settings.idProject)
						.pipe(take(1))
						.subscribe(response => {
							if (response) {
								this.messageBoxService.exclamation('Operation complete',
									'Dashboard reinitialization has been requested. The operation might last a while.');
							} else {
								this.messageBoxService.exclamation('Operation failed',
									'This request was not necessary : no dashboard available.');
							}
						});
					}
				this.idPanelSelected = this.SUNBURST;
			}
		);
	}

	dialogFilter() {
		if (typeof this.project === 'undefined') {
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
			this.loadTaskActivities();
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
	 * Set the new active context insoide the form component.
	 */
	public setActiveContext(context: number) {

		if (Constants.DEBUG) {
			console.log ('New active context ' + context + ' after ' + this.lastSunburstContext);
		}

		// We keep away the previous context related to the construction of the sunburst chart.
		if ( 	(context === this.CONTEXT.SUNBURST_READY)
			|| (context === this.CONTEXT.SUNBURST_IMPOSSIBLE)
			|| (context === this.CONTEXT.SUNBURST_WAITING)) {
			this.lastSunburstContext = context;
		}
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
	public isActiveContext(context: number) {
		return (context === this.activeContext);
	}

	/**
	 * Change the current active tab.
	 * @param tabIndex index of tab requested.
	 */
	public jumpToTab(tabIndex: number) {
		this.tabActivationEmitter.next(tabIndex);
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
