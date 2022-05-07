import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EMPTY, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Constants } from 'src/app/constants';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { ProjectSonarMetric } from 'src/app/data/sonar/project-sonar-metric';
import { traceOn } from 'src/app/global';
import { MessageGravity } from 'src/app/interaction/message/message-gravity';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { SonarService } from 'src/app/service/sonar/sonar.service';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';

@Component({
	selector: 'app-sonar-metrics',
	templateUrl: './sonar-metrics.component.html',
	styleUrls: ['./sonar-metrics.component.css']
})
export class SonarMetricsComponent extends BaseDirective implements OnInit, OnDestroy, AfterViewInit {

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$;

	/**
	 * This Output EventEmitter is in charge of the propagation of info/warning/error messages to the host container
	 * For an unknown reason the messageService.* is failing outsite the host panel
	 */
	@Output() throwMessage = new EventEmitter<MessageGravity>();

	/**
	 * Is The Sonar server accessible ?
	 */
	public isSonarAccessible = false;

	/**
	 * The datasource that contains the filtered projects;
	 */
	dataSource: MatTableDataSource<ProjectSonarMetric>;

	/**
	 * The projects list will be a sorted table.
	 */
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	/**
	 * The paginator of the ghosts data source.
	 */
	@ViewChild(MatPaginator) paginator: MatPaginator;

	/**
	 * Key of the current selected Sonar project.
	 */
	private sonarKey = '';

	public editableColumns: string[] = ['name', 'selected', 'weight', 'value', 'explanation'];

	constructor(
		private sonarService: SonarService,
		private messageService: MessageService,
		private projectService: ProjectService) {
		super();
	}

	ngOnInit() {

		this.subscriptions.add(
			this.projectService.projectLoaded$
				.pipe(
					switchMap( doneAndOk => {
						return (doneAndOk) ? this.sonarService.sonarIsAccessible$(this.projectService.project) : EMPTY;
					}),
					switchMap(isSonarAccessible => {
						this.isSonarAccessible = isSonarAccessible;
						return  (this.isSonarAccessible) ? this.loadMetrics$() : EMPTY;
					}))
				.subscribe ((data: ProjectSonarMetric[]) => {
					this.sonarService.getSonarServer(this.projectService.project).setProjectSonarMetrics(data);
					this.initDataSource(data);
			}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe(
					(panelSwitchEvent: PanelSwitchEvent)  => {
				if ( (traceOn()) && (panelSwitchEvent.idPanel === Constants.PROJECT_SONAR_PANEL.SETTINGS) ) {
					console.log('Updating the metrics for the Sonar project %s', panelSwitchEvent.keySonar);
				}
				if (!panelSwitchEvent.keySonar) {
					if (traceOn()) {
						console.log ('No Sonar project declared. We reinitialize the dataSource');
					}
					return;
				}
				if (!this.isSonarAccessible) {
					if (traceOn()) {
						console.log ('Sonar cannot be reach. Nothing to update.');
					}
					return;
				}
				if (this.projectService.project && (panelSwitchEvent.keySonar.length > 0) ) {
					this.sonarKey = panelSwitchEvent.keySonar;
					this.dataSource.data.forEach(element => {
						const weight = this.getWeightOfSonarProjectMetric(element.key);
						if (weight) {
							element.selected = true;
							element.weight = weight;
							element.value = this.getValueOfSonarProjectMetric(element.key);
						} else {
							element.selected = false;
							element.weight = 0;
							element.value = 0;
						}
					});
				}
			}));
	}

	/**
	 * ngAfterViewInit.
	 */
	ngAfterViewInit() {
		if (this.dataSource) {
			this.dataSource.paginator = this.paginator;
		}
	}

	private loadMetrics$(): Observable<ProjectSonarMetric[]> {

		if (traceOn()) {
			console.log ('loadMetrics$() for %s', this.projectService.project.name);
		}

		const sonarServer = this.sonarService.getSonarServer(this.projectService.project);
		if (!sonarServer) {
			return of(null);
		}

		// We have saved the first array of ProjectSonarMetric for caching purpose
		if (sonarServer.projectSonarMetrics.length > 0) {
			return of(sonarServer.projectSonarMetrics);
		}

		return sonarServer.sonarMetrics$.pipe(
			map (metrics => {
				const projectSonarMetrics: ProjectSonarMetric[] = [];
				metrics.forEach( metric => {
					projectSonarMetrics.push(new ProjectSonarMetric(
						metric.key,
						metric.name,
						false,
						0,
						0,
						this.sonarService.CALCULATION_RULES[metric.key]));
					});
					return projectSonarMetrics;
				}));
	}

	/**
	 * @param metricKey the key of the metric
	 * @returns the searched weight, or null if no weight has been setup for the given metric
	 */
	getWeightOfSonarProjectMetric (metricKey: string): number {
		const metricValue = this.projectService.getProjectSonarMetricValue(this.projectService.project, this.sonarKey, metricKey);
		return (metricValue) ? metricValue.weight : undefined;
	}

	/**
	 * @param metricKey the key of the metric
	 * @returns the Sonar note, or null if no data has been retrieved for the given metric
	 */
	getValueOfSonarProjectMetric (metricKey: string): number {
		const metricValue = this.projectService.getProjectSonarMetricValue(this.projectService.project, this.sonarKey, metricKey);
		return (metricValue) ? metricValue.value : undefined;
	}

	/**
	 * Initialize the dataSource of the table.
	 * @param projectSonarMetrics the array of metrics as origine of the dataSource
	 */
	private initDataSource(projectSonarMetrics: ProjectSonarMetric[]) {
		if (traceOn()) {
			console.groupCollapsed(projectSonarMetrics.length + ' records in projectSonarMetrics');
			projectSonarMetrics.forEach(projectSonarMetric => {
				console.log(projectSonarMetric.key, projectSonarMetric.name);
			});
			console.groupEnd();
		}
		this.dataSource = new MatTableDataSource<ProjectSonarMetric>(projectSonarMetrics);
		this.dataSource.sortingDataAccessor = (item: ProjectSonarMetric, property: string) => {
			switch (property) {
				case 'name':
					return item.name.toLocaleLowerCase();
				case 'selected':
					return item.selected ? 1 : 0;
				case 'weight':
					return item.weight;
				case 'value':
					return item.value;
			}
		};
		this.dataSource.sort = this.sort;
	}

	/**
	 * Return TRUE if the passed Sonar metric has been elected for the analysis
	 * @param metric current Sonar metric
	 */
	public isAMetricSelected(metric: ProjectSonarMetric): boolean {
		return metric.selected;
	}

	/**
	 * User a selected a metric for his Sonar analysis.
	 * @param metric current Sonar metric
	 */
	public changeSelected(metric: ProjectSonarMetric) {
		if (metric.selected) {
			metric.weight = 0;
		}
	}

	/**
	 * User a selected a metric for his Sonar analysis.
	 * @param metric current Sonar metric
	 */
	public changeWeight(metric: ProjectSonarMetric) {
		const sum = this.dataSource.data.map(met => met.weight).reduce ( (w1, w2) => w1 + w2, 0);
		if (sum !== 100) {
			this.throwMessage.next(
				new MessageGravity(Constants.MESSAGE_WARNING,
				'Distribution of metrics cannot be saved unless the sum reach 100%'));
		} else {
			const sonarProject = this.projectService.getSonarProject(this.projectService.project, this.sonarKey);
			sonarProject.projectSonarMetricValues = [];
			this.dataSource.data.forEach(psm => {
				if (psm.weight > 0) {
					sonarProject.projectSonarMetricValues.push(
						new ProjectSonarMetricValue(
							psm.key,
							psm.weight,
							0
						));
				}
			});
			this.projectService.loadAndSaveEvaluations(
				this.sonarService,
				this.projectService.project,
				sonarProject.key,
				sonarProject.projectSonarMetricValues,
				this.throwMessage);
		}
	}

	/**
	 * Evaluate the CSS classname for the weight input for a given metric record
	 * @param metric the given metric
	 * @returns the class for this weight.
	 */
	cssClassOfWeight(metric: ProjectSonarMetric): string {
		if ((metric.weight < 0) || (metric.weight > 100)) {
			return 'invalid-field';
		}
		return 'valid-field';
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
