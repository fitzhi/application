import { Component, OnInit, ViewChild, Input, OnDestroy } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ProjectSonarMetric } from 'src/app/data/sonar/project-sonar-metric';
import { SonarService } from 'src/app/service/sonar.service';
import { switchMap, map, catchError } from 'rxjs/operators';
import { Project } from 'src/app/data/project';
import { Observable, EMPTY, of } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { Constants } from 'src/app/constants';
import { ProjectSonarMetricValue } from 'src/app/data/project-sonar-metric-value';
import { ProjectService } from 'src/app/service/project.service';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';
import { ElementSchemaRegistry } from '@angular/compiler';

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

	public editableColumns: string[] = ['name', 'selected', 'weight', 'explanation'];

	constructor(
		private sonarService: SonarService,
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
						}
					});
					if (Constants.DEBUG) {
						this.projectService.dump(this.project, 'SonarMetrics.ngInit');
					}
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
						this.sonarService.CALCULATION_RULES[metric.key]));
					});
					return projectSonarMetrics;
				}));
	}

	/**
	 * @param sonarKey the key of the Sonar project
	 * @param metricKey the key of the metric
	 * @returns the searched weight, or null if no weight has been setup for the given parameters
	 */
	getWeightOfSonarProjectMetric (metricKey: string): number {
		const metricValue = this.projectService.getProjectSonarMetricValue(this.project, this.sonarKey, metricKey);
		return (metricValue) ? metricValue.weight : undefined;
	}

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
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
