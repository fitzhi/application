import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { traceOn } from 'src/app/global';
import { StarfieldService } from '../service/starfield.service';

@Component({
	selector: 'app-starfield-header',
	templateUrl: './starfield-header.component.html',
	styleUrls: ['./starfield-header.component.css']
})
export class StarfieldHeaderComponent extends BaseDirective implements OnInit, OnDestroy {

	public displayHelp = false;

	private todaySubject$ = new BehaviorSubject<Date>(new Date())
	
	/**
	 * Month to be displayed.
	 */
	public today$ = this.todaySubject$.asObservable();

	public nextMonthAvailable = false;

	public previousMonthAvailable = false;

	constructor(public starfieldService: StarfieldService) {
		super();
	}

	ngOnInit(): void {
		this.starfieldService.retrieveActiveStatePrevious();
		this.starfieldService.retrieveActiveStateNext();

		this.subscriptions.add(
			this.starfieldService.next$.subscribe({
				next: next => this.nextMonthAvailable = next
			})
		);

		this.subscriptions.add(
			this.starfieldService.previous$.subscribe({
				next: previous => this.previousMonthAvailable = previous
			})
		);
	}

	next(): void {
		if (this.nextMonthAvailable) {
			if (traceOn()) {
				console.log ('next Month from %s', this.starfieldService.selectedMonth.toString());
			}
			this.starfieldService.broadcastNextConstellations();
			this.todaySubject$.next(this.starfieldService.selectedMonth.firstDateOfMonth());
		}
	}

	previous(): void {
		if (this.previousMonthAvailable) {
			if (traceOn()) {
				console.log ('previous Month for %s', this.starfieldService.selectedMonth.toString());
			}
			this.starfieldService.broadcastPreviousConstellations();
			this.todaySubject$.next(this.starfieldService.selectedMonth.firstDateOfMonth());
		}
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
