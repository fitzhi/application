import { Component, OnInit, ViewChild, Input, OnDestroy, Output, EventEmitter } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ProjectSonarMetric } from 'src/app/data/sonar/project-sonar-metric';
import { SonarService } from 'src/app/service/sonar.service';
import { switchMap, map, catchError, take } from 'rxjs/operators';
import { Project } from 'src/app/data/project';
import { Observable, EMPTY, of } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { ProjectService } from 'src/app/service/project.service';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';
import { ElementSchemaRegistry } from '@angular/compiler';
import { MessageService } from 'target/classes/app/message/message.service';
import { MessageGravity } from 'src/app/message/message-gravity';
import { ResponseComponentMeasures } from 'src/app/data/sonar/reponse-component-measures';

@Component({
	selector: 'app-sonar-metrics',
	templateUrl: './sonar-metrics.component.html',
	styleUrls: ['./sonar-metrics.component.css']
})
export class SonarMetricsComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	* The project loaded in the parent component.
	*/
	@Input() project$;

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
	 * Current active project.
	 */
	private project: Project;

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
	@ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

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
			this.project$.subscribe(project => this.project = project));

		this.subscriptions.add(
			this.loadMetrics$().subscribe ((data: ProjectSonarMetric[]) => {
				this.sonarService.setProjectSonarMetrics(data);
				this.initDataSource(data);
			}));

		this.subscriptions.add(
			this.panelSwitchTransmitter$.subscribe(
					(panelSwitchEvent: PanelSwitchEvent)  => {
				if (this.project && (panelSwitchEvent.keySonar.length > 0) ) {
					this.sonarKey = panelSwitchEvent.keySonar;
					const sonarProject = this.project.sonarProjects
						.find(sonarP => sonarP.key === panelSwitchEvent.keySonar);
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

	private loadMetrics$(): Observable<ProjectSonarMetric[]> {

		// We have saved the first array of ProjectSonarMetric for caching purpose
		if (this.sonarService.projectSonarMetrics.length > 0) {
			return of(this.sonarService.projectSonarMetrics);
		}

		return this.sonarService.sonarMetrics$.pipe(
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
		const metricValue = this.projectService.getProjectSonarMetricValue(this.project, this.sonarKey, metricKey);
		return (metricValue) ? metricValue.weight : undefined;
	}

	/**
	 * @param metricKey the key of the metric
	 * @returns the Sonar note, or null if no data has been retrieved for the given metric
	 */
	getValueOfSonarProjectMetric (metricKey: string): number {
		const metricValue = this.projectService.getProjectSonarMetricValue(this.project, this.sonarKey, metricKey);
		return (metricValue) ? metricValue.value : undefined;
	}

	/**
	 * Load from Sonar the evaluation for the given metrics.
	 * @param metricValues the array of Metric record to update with the Sonar last evaluation.
	 */
	loadAndSaveEvaluations(metricValues: ProjectSonarMetricValue[]) {
		this.subscriptions.add(
			this.sonarService.loadSonarComponentMeasures(
					this.sonarKey,
					metricValues.map(psmv => psmv.key))
				.subscribe((measures: ResponseComponentMeasures) => {
					measures.component.measures.forEach(measure => {
						const psmv = metricValues.find(mv => mv.key === measure.metric);
						if (!isNaN(Number(measure.value))) {
							psmv.value = Number(measure.value);
						} else {
							if (measure.value === 'OK') {
								psmv.value = 1;
							} else {
								if (measure.value === 'ERROR') {
									psmv.value = 0;
								} else {
									console.error ('Unexpected value of measure', measure.value);
								}
							}
						}
					});

					this.projectService.dump(this.project, 'loadEvaluations');

					//
					// the metricValues is updated with the evaluation returned by Sonar.
					//
					this.projectService.saveMetricValues(this.project.id, this.sonarKey, metricValues)
						.pipe(take(1))
						.subscribe (ok => {
							if (ok) {
								this.throwMessage.next(
									new MessageGravity(Constants.MESSAGE_INFO,
									'Metrics weights and values have been saved for the Sonar project ' + this.sonarKey));
							} else {
								this.throwMessage.next(
									new MessageGravity(Constants.MESSAGE_ERROR,
									'Error when saving weights and values for the Sonar project ' + this.sonarKey));
							}});
				}));
	}

	/**
	 * Initialize the dataSource of the table.
	 * @param projectSonarMetrics the array of metrics as origine of the dataSource
	 */
	private initDataSource(projectSonarMetrics: ProjectSonarMetric[]) {
		if (Constants.DEBUG) {
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
		this.dataSource.paginator = this.paginator;
	}

	/**
	 * Test if the current metric is selected.
	 * @return TRUE if the passed Sonar metric has been elected for the analysis
	 * @param metric current Sonar metric
	 */
	private isAMetricSelected(metric: ProjectSonarMetric): boolean {
		return metric.selected;
	}

	/**
	 * User a selected a metric for his Sonar analysis.
	 * @param metric current Sonar metric
	 */
	private changeSelected(metric: ProjectSonarMetric) {
		if (metric.selected) {
			metric.weight = 0;
		}
	}

	/**
	 * User a selected a metric for his Sonar analysis.
	 * @param metric current Sonar metric
	 */
	private changeWeight(metric: ProjectSonarMetric) {
		const sum = this.dataSource.data.map(met => met.weight).reduce ( (w1, w2) => w1 + w2, 0);
		if (sum !== 100) {
			this.throwMessage.next(
				new MessageGravity(Constants.MESSAGE_WARNING,
				'Distribution of metrics cannot be saved unless the sum reach 100%'));
		} else {
			const sonarProject = this.projectService.getSonarProject(this.project, this.sonarKey);
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
			this.loadAndSaveEvaluations(sonarProject.projectSonarMetricValues);
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
