import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { SummarySubjects } from './summary-subjects';

@Injectable({
	providedIn: 'root'
})
export class SummaryService {


	private summary = new SummarySubjects();

	private summarySubject$ = new BehaviorSubject<SummarySubjects>(this.summary);
	
	/**
	 * Type of summary to be displayed.
	 */
	public summary$ = this.summarySubject$.asObservable();

	/**
	 * Display the overall average in the fitzhi portfolio.
	 */
	public showOverallAverage() {
		this.summary.overallAverage = true;
		this.summarySubject$.next(this.summary);
	}

	constructor() { }
}
