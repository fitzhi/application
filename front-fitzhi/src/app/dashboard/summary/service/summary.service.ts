import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { SummarySubjects } from './summary-subjects';

@Injectable({
	providedIn: 'root'
})
export class SummaryService {

	private summary = new SummarySubjects();

	private summarySubject$ = new BehaviorSubject<SummarySubjects>(this.summary);
	
	private generalAverageSubject$ = new BehaviorSubject<number>(0);

	public generalAverage$ = this.generalAverageSubject$.asObservable();

	/**
	 * Type of summary to be displayed.
	 */
	public summary$ = this.summarySubject$.asObservable();

	/**
	 * Display the overall average in the fitzhi portfolio.
	 */
	public showGeneralAverage() {
		this.summary.overallAverage = true;
		this.summarySubject$.next(this.summary);
		this.generalAverageSubject$.next(this.dashboardService.calculateGeneralAverage());
	}

	constructor(private dashboardService: DashboardService) { }
}
