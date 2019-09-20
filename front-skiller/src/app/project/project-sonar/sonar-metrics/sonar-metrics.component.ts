import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { ProjectSonarMetric } from 'src/app/data/sonar/project-sonar-metric';
import { SonarService } from 'src/app/service/sonar.service';
import { switchMap, map, catchError } from 'rxjs/operators';
import { Metrics } from 'target/classes/app/data/sonar/metrics';
import { Project } from 'src/app/data/project';
import { of, Observable, EMPTY } from 'rxjs';
import { Metric } from 'src/app/data/sonar/metric';

@Component({
	selector: 'app-sonar-metrics',
	templateUrl: './sonar-metrics.component.html',
	styleUrls: ['./sonar-metrics.component.css']
})
export class SonarMetricsComponent implements OnInit {

	/**
	* The project loaded in the parent component.
	*/
	@Input() project$;

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

	public editableColumns: string[] = ['name', 'selected', 'weight'];

	constructor(private sonarService: SonarService) {
	}

	private loadMetrics (project: Project): Observable<ProjectSonarMetric[]> {
		return this.sonarService.allSonarMetrics$.pipe(
			map (metrics => {
				const projectSonarMetrics: ProjectSonarMetric[] = [];
				metrics.forEach( metric => {
					projectSonarMetrics.push(new ProjectSonarMetric(
						metric.key,
						metric.name,
						false,
						77));
					});
					return projectSonarMetrics;
				}));
	}

	ngOnInit() {
		this.project$.pipe (
			switchMap( (project: Project) => {
				return this.loadMetrics (project);
			}))
			.subscribe (data => {
				this.initDataSource(data);
			});
	}

	private initDataSource(projectSonarMetrics: ProjectSonarMetric[]) {
		console.groupCollapsed(projectSonarMetrics.length + ' recors in projectSonarMetrics');
		projectSonarMetrics.forEach(projectSonarMetric => {
			console.log(projectSonarMetric.key, projectSonarMetric.name);
		});
		console.groupEnd();
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

}
