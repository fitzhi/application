import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { SummarySubjects } from './summary-subjects';

@Injectable({
	providedIn: 'root'
})
export class SummaryService {

	private summary = new SummarySubjects();

	private summarySubject$ = new BehaviorSubject<SummarySubjects>(this.summary);

	private generalAverageSubject$ = new BehaviorSubject<number>(-1);

	public generalAverage$ = this.generalAverageSubject$.asObservable();

	/**
	 * Type of summary to be displayed.
	 */
	public summary$ = this.summarySubject$.asObservable();

	/**
	 * Display the overall average in the fitzhi portfolio.
	 */
	public showGeneralAverage() {
		const score = this.dashboardService.calculateGeneralAverage();
		if (isNaN(score)) {
			return;
		}
		this.summary.overallAverage = true;
		this.summarySubject$.next(this.summary);
		this.generalAverageSubject$.next(score);
	}

	constructor(private dashboardService: DashboardService) { }
}
