import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { isJSDocPropertyLikeTag, isTemplateExpression } from 'typescript';

@Injectable({
	providedIn: 'root'
})
export class TreemapProjectsService {

	/**
	 * The distribution of projects to be drawn in the chart.
	 */
	distribution: any[];

	private selectedProjectsSubject$ = new BehaviorSubject<number[]>([]);

	public selectedProjects$ = this.selectedProjectsSubject$.asObservable();

	/**
	 * The current array of projects identifiers.
	 */
	public idProjects: number[];

	constructor(dashboardService: DashboardService) {
		const distribution = dashboardService.processProjectsDistribution()
		this.idProjects = distribution.map(p => p.id);
		this.selectedProjectsSubject$.next(distribution.map(item => item.id));
	 }

	/**
	 * Inform the treemap projects chart of the selected projects.
	 * @param idProjects array of projects identifier
	 */
	informSelectedProjects (idProjects: number[]) {
		this.idProjects = idProjects;
		this.selectedProjectsSubject$.next(idProjects);
	}

	/**
	 * Test if the passed id belongs to the selected projects array.
	 * @param id the given identifier
	 * @returns **TRUE** if the id is present, **FALSE** otherwise
	 */
	public isSelected(idProject: number): boolean {
		return (this.idProjects.findIndex(id => id === idProject) >= 0);
	}
}
