import { Component, OnInit, AfterViewInit, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import Sunburst from 'sunburst-chart';
import { Constants } from '../../constants';
import { MessageService } from '../../message/message.service';
import { ProjectService } from '../../service/project.service';
import { ActivatedRoute } from '@angular/router';
import { CinematicService } from '../../service/cinematic.service';
import { Project } from '../../data/project';
import { MatDialogConfig, MatDialog } from '@angular/material';
import { DialogProjectGhostsComponent } from './dialog-project-ghosts/dialog-project-ghosts.component';
import { ProjectGhostsDataSource } from './dialog-project-ghosts/project-ghosts-data-source';
import { DialogLegendSunburstComponent } from './dialog-legend-sunburst/dialog-legend-sunburst.component';
import { MessageBoxService } from '../../message-box/service/message-box.service';
import { DialogFilterComponent } from './dialog-filter/dialog-filter.component';
import { BaseComponent } from '../../base/base.component';
import { SettingsGeneration } from '../../data/settingsGeneration';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';
import { Filename } from '../../data/filename';
import { FilenamesDataSource } from './node-detail/filenames-data-source';
import { ContributorsDataSource } from './node-detail/contributors-data-source';
import { BehaviorSubject } from 'rxjs';
import { Contributor } from '../../data/contributor';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-project-sunburst',
	templateUrl: './project-sunburst.component.html',
	styleUrls: ['./project-sunburst.component.css']
})
export class ProjectSunburstComponent extends BaseComponent implements OnInit, AfterViewInit, OnDestroy {

	/**
     * The project loaded in the parent component.
     */
	@Input('project$') project$;

	/**
	 * This component, hosted in a tab pane, use this emitter to inform its parent to change the active pane.
	 * e.g. if the project form is not complete, application will jump to this tab pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	public PROJECT_IDX_TAB_FORM = Constants.PROJECT_IDX_TAB_FORM;
	
	/**
    * Project loaded on the parent component.
    */
	private project: Project;

	/**
     * Parameters passed to the generation method on the back-end.
     */
	private settings = new SettingsGeneration(-1, new Date(0).getTime(), 0);

	public dataGhosts: ProjectGhostsDataSource;

	// Previous context
	public previousContext = 0;


	// Active current context
	public activeContext = 0;

	// this context is indicating that the sunburst chart is ready to be viewed.
	public CONTEXT = {
		SUNBURST_READY: 1,
		SUNBURST_IMPOSSIBLE: 2,
		SUNBURST_WAITING: 3,
		SUNBURST_DEPENDENCIES: 4,
		SUNBURST: 5
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

		this.subscriptions.add(
			this.project$.subscribe((project: Project) => {
				if (Constants.DEBUG) {
					console.log('Project ' + project.id + ' ' + project.name + ' reveived in sunburst-component');
				}
				this.project = project;
				this.projectName = this.project.name;
				if ((typeof this.project.urlRepository === 'undefined') || (this.project.urlRepository.length === 0)) {
					this.messageService.info('No repository URL avalaible !');
					this.setActiveContext (this.CONTEXT.SUNBURST_IMPOSSIBLE);
				}
			}));

		this.subscriptions.add(
			this.cinematicService.tabProjectActivated.subscribe(
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

		if (typeof this.myChart === 'undefined') {
			this.myChart = Sunburst();
			this.myChart.onNodeClick(nodeClicked => {
				this.onNodeClick(nodeClicked);
				this.myChart.focusOnNode(nodeClicked);
			});
		}

		this.projectService.loadDashboardData(this.settings)
			.pipe(take(1)).subscribe(
				response => this.handleSunburstData(response),
				response => this.handleErrorData(response),
				() => {
					this.hackSunburstStyle();
					this.tooltipChart();
					this.setActiveContext (this.CONTEXT.SUNBURST_READY);
				});
	}

	/**
    * user click on a a node.
    **/
	public onNodeClick(nodeClicked: any) {
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

	findContributor(idStaff: number): Contributor {
		const foundContributor = this.projectStaffService.contributors
			.find(contributor => contributor.idStaff === idStaff);
		if (!foundContributor) {
			console.log (idStaff, 'id Staff not found as a contributor.' );
		}
		return foundContributor;
	}

	handleSunburstData(response: any) {
		if (this.myChart !== null) {
			this.myChart.data(response.sunburstData).width(500).height(500).label('location').size('importance').color('color')
				(document.getElementById('chart'));
			if (typeof this.dataGhosts === 'undefined') {
				this.dataGhosts = new ProjectGhostsDataSource(this.project);
			}
			// Send the unregistered contributors to the panel list
			this.dataGhosts.sendUnknowns(response.ghosts);
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
		this.idPanelSelected = idPanel;
		switch (idPanel) {
			case this.SUNBURST:
				this.setActiveContext(this.previousContext);
				break;
			case this.LEGEND_SUNBURST:
				this.dialogLegend();
				break;
			case this.LEGEND_SUNBURST:
				this.dialogLegend();
				break;
			case this.SETTINGS:
				this.dialogFilter();
				break;
			case this.UNKNOWN:
				this.dialogGhosts();
				break;
			case this.DEPENDENCIES:
				this.setActiveContext(this.CONTEXT.SUNBURST_DEPENDENCIES);
				break;
			case this.RESET:
				this.reset();
				break;
			default:
				break;
		}
	}

	dialogGhosts() {
		if (typeof this.project === 'undefined') {
			this.messageService.info('Nothing to show !');
			return;
		}
		if (typeof this.dataGhosts === 'undefined') {
			this.messageService.info('Please wait !');
			return;
		}

		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.position = { top: '5em', left: '5em' };
		dialogConfig.panelClass = 'default-dialog-container-class';
		dialogConfig.data = this.dataGhosts;
		const dialogReference = this.dialog.open(DialogProjectGhostsComponent, dialogConfig);
		dialogReference.afterClosed()
			.pipe(take(1))
			.subscribe(result => {
				if (result !== null) {
					if (typeof result === 'boolean') {
						this.dataGhosts.ghostsSubject.next(this.dataGhosts.ghostsSubject.getValue());
					} else {
						this.dataGhosts.ghostsSubject.next(result);
					}
				}
				this.idPanelSelected = -1;
			});
	}

	dialogLegend() {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.autoFocus = true;
		dialogConfig.position = { top: '5em', left: '5em' };
		dialogConfig.panelClass = 'default-dialog-container-class';
		const dlg = this.dialog.open(DialogLegendSunburstComponent, dialogConfig);
		dlg.afterClosed().pipe(take(1)).subscribe(() => {
			this.idPanelSelected = this.SUNBURST;
		});

	}

	reset() {
		if (typeof this.project === 'undefined') {
			this.messageService.info('Nothing to reset !');
			this.idPanelSelected = this.SUNBURST;
			return;
		}
		this.messageBoxService.question('Reset the dashboard',
			'Please confirm the dashboard reinitialization')
				.pipe(take(1))
				.subscribe(answer => {
				if (answer) {
					this.projectService.resetDashboard(this.settings.idProject)
						.pipe(take(1))
						.subscribe(response => {
						if (response) {
							this.messageBoxService.exclamation('Operation complete',
								'Dashboard reinitialization has been requested. The operation might last a while.');
						} else {
							this.messageBoxService.exclamation('Operation failed',
								'The request is not necessary : no dashboard available.');
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

		if (typeof this.dataGhosts === 'undefined') {
			this.messageService.info('Please wait !');
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
			this.projectService.loadDashboardData(this.settings)
				.pipe(take(1))
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
			console.log ('New active context ' + context + ' after ' + this.previousContext);
		}

		// We keep away the previous context
		this.previousContext = this.activeContext;

		this.activeContext = context;
	}

	/**
	 * Test if the passed context is the current active context.
	 * There are 4 context possible in this form container
	 * . sunburst_waiting : the graph representing the risk of staff coverage is currently being build</li>
	 * . sunburst_ready : the graph is ready to be displayed
	 * . sunburst_impossible : either lack of connection information, or lack of internet, or something else : the graph cannot be displayed.
	 * . sunburst_detail_dependencies : the table of libraries detected or declared is available in the container.
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
