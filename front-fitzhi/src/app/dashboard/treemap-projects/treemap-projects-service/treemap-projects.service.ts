import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { isTemplateExpression } from 'typescript';

@Injectable({
	providedIn: 'root'
})
export class TreemapProjectsService {

	distribution: any[];

	private selectedProjectsSubject$ = new BehaviorSubject<number[]>([]);

	public selectedProjects$ = this.selectedProjectsSubject$.asObservable();

	constructor(dashboardService: DashboardService) {
		const distribution = dashboardService.processProjectsDistribution()
		this.selectedProjectsSubject$.next(distribution.map(item => item.id));
	 }

	/**
	 * Inform the treemap projects chart of the selected projects.
	 * @param idProjects array of projects identifier
	 */
	informSelectedProjects (idProjects: number[]) {
		this.selectedProjectsSubject$.next(idProjects);
	}
}
