import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { trace } from 'console';
import { NOT_FOUND } from 'http-status-codes';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { catchError, take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { DashboardColor } from 'src/app/service/dashboard/dashboard-color';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { Constellation } from '../data/constellation';
import { Star } from '../data/star';
import { StarfieldFilter } from '../data/starfield-filter';
import { StarfieldMonth } from '../data/starfield-month';

@Injectable({
	providedIn: 'root'
})
export class StarfieldService {

	private constellationsSubject$ = new BehaviorSubject<Constellation[]>([]);

	/**
	 * The constellations of skills to be drawn in the starfield component.
	 */
	public constellations$ = this.constellationsSubject$.asObservable();


	private starsSubject$ = new BehaviorSubject<Star[]>([]);

	/**
	 * The serie of stars (<span>&#x2605;</span>) to be assembled in the catterpilar .
	 */
	public stars$ = this.starsSubject$.asObservable();

	/**
	 * Selected month displayed on the starfield.
	 * Default value is the current month.
	 */
	public selectedMonth = new StarfieldMonth(new Date().getMonth(), new Date().getFullYear());

	private nextSubject$ = new BehaviorSubject<boolean>(false);

	/**
	 * This observable reflects the fact that the button "next" should be active or not.
	 */
	public next$ = this.nextSubject$.asObservable();

	private previousSubject$ = new BehaviorSubject<boolean>(false);

	/**
	 * This observable reflects the fact that the button "next" should be active or not.
	 */
	public previous$ = this.previousSubject$.asObservable();

	/**
	 * Actual filters chosen by the end user for the starfield component.
	 */
	public filter = new StarfieldFilter();

	private helpPanelVisible = false;

	private helpPanelVisibleSubject$ = new BehaviorSubject<boolean>(this.helpPanelVisible);

	public helpPanelVisible$ = this.helpPanelVisibleSubject$.asObservable();

	constructor(
		private staffListService: StaffListService,
		private backendSetupService: BackendSetupService,
		private messageService: MessageService,
		private httpClient: HttpClient) { }

	/**
	 * Reverve the value of the external boolean filter.
	 */
	public switchExternalFilter(): void {
		this.filter.external = !this.filter.external;
		if (traceOn()) {
			console.log ('External filter is %s', this.filter.external);
		}
		this.generateAndBroadcastConstellations();
	}

	/**
	 * Emit the skills constellation to be drawn in the starfield component.
	 * @param constellations array of constellations
	 */
	public broadcastConstellations(constellations: Constellation[]) {
		if (traceOn()) {
			console.log ('Broadcasting %d constellations', constellations.length);
		}
		this.constellationsSubject$.next(constellations);
	}

	/**
	 * Emit the skills constellation to be drawn in the starfield component.
	 * @param stars array of stars
	 */
	public broadcastStars(stars: Star[]) {
		if (traceOn()) {
			console.log ('Broadcasting %d stars', stars.length);
		}
		this.starsSubject$.next(stars);
	}

	/**
	 * Assemble the array of constellations into one **unique** serie of <span>&#x2605;</span>.
	 * *We aggregate the constallations into one single serie of <span>&#x2605;</span>
	 * in order to be able to wrap a serie in the middle of a skill.*
	 * @param stars the array of skills data
	 */
	public assembleTheStars(constellations: Constellation[]) {

		if (traceOn()) {
			console.log ('%d constellations to be assembled.', constellations.length);
		}
		const stars = [];
		constellations.forEach(constellation => {
			for (let i = 0; i < constellation.count; i++) {
				stars.push(new Star(constellation.idSkill, constellation.count, constellation.color, constellation.backgroundColor));
			}
		});
		if (traceOn()) {
			console.log ('%d stars have been assembled.', stars.length);
		}
		this.broadcastStars(stars);
	}

	/**
	 * **GENERATE** and **BROADCAST** the constellations based on the actual active staff members.
	 */
	public generateAndBroadcastConstellations() {
		this.staffListService.allStaff$.pipe(take(1)).subscribe({
			next: allStaff => {
				const constellations = this.takeStaffInAccount(allStaff);
				this.broadcastConstellations(constellations);
			}
		});
	}

	/**
	 * Take in account the staff collection and *GENERATE* a new array of constellations.
	 * *This generation will take account as well, the filters chosen by the user.*
	 *
	 * @param allStaff the staff collections registered in Fitzhi
	 * @returns the newly array of constellations
	 */
	public takeStaffInAccount(allStaff: Collaborator[]): Constellation[] {
		const constellations: Constellation[] = [];
		allStaff.forEach(staff => {
			if (staff.active) {
				// Either we take in account the external staff, which means we involved all staff members
				// Or the take in account only the 'internal' staff.
				if ((this.filter.external) || (!staff.external)) {
					staff.experiences.forEach(experience => {
						const constellation = constellations.find(cnst => cnst.idSkill === experience.id);
						if (constellation) {
							constellation.count = constellation.count + experience.level;
						} else {
							constellations.push (new Constellation(experience.id, experience.level));
						}
					});
				}
			}
		});
		for (let i = 0; i < constellations.length; i++) {
			constellations[i].backgroundColor = 'white';
			constellations[i].color = DashboardColor.rgb(i, constellations.length - 1);
		}
		return constellations;
	}

	/**
	 * Switch the visibility of the Help panel. If the panel is hidden, it will be shown.
	 * Otherwise, if visible, the help panel will be hidden.
	 */
	switchHelpPanelVisibility() {
		if (traceOn()) {
			console.log('switchHelpPanelVisibility to', !this.helpPanelVisible);
		}
		this.helpPanelVisible = !this.helpPanelVisible;
		this.helpPanelVisibleSubject$.next (this.helpPanelVisible);
	}

	/**
	 * switch the state of the next button.
	 * @param state the new state for the **NEXT** button
	 */
	switchActiveStateNext(state: boolean) {
		this.nextSubject$.next(state);
	}

	/**
	 * switch the state of the previous button.
	 * @param state the new state for the **PREVIOUS** button
	 */
	 switchActiveStatePrevious(state: boolean) {
		this.previousSubject$.next(state);
	}

	/**
	 * Retrieve the active state for the previous month.
	 */
	public retrieveActiveStatePrevious() {
		const date = new Date(this.selectedMonth.year, this.selectedMonth.month, 1);
		date.setDate(0);

		// Months range is from 0 to 11.
		const month = date.getMonth() + 1;
		const year = date.getFullYear();

		if (traceOn()) {
			console.log ('Retrieving constellations for %d/%d', month, year);
		}
		this.httpClient
			.get<Constellation[]>(`${this.backendSetupService.url()}/staff/constellation/${year}/${month}`)
			.pipe(
				take(1),
				catchError(error => {
					if  (error.status === NOT_FOUND) {
						this.switchActiveStatePrevious(false);
					}
					return EMPTY;
				})
			).subscribe({
				next: constellations => {
					this.switchActiveStatePrevious(true);
				}
			});
	}

	/**
	 * Retrieve the active state for the previous month.
	 */
	 public retrieveActiveStateNext() {

		const nextMonth = this.nextMonth(new Date(this.selectedMonth.year, this.selectedMonth.month, 1));

		// Months range is from 0 to 11.
		const month = nextMonth.getMonth() + 1;
		const year = nextMonth.getFullYear();

		if (traceOn()) {
			console.log ('Retrieving constellations for %d/%d', month, year);
		}
		this.httpClient
			.get<Constellation[]>(`${this.backendSetupService.url()}/staff/constellation/${year}/${month}`)
			.pipe(
				take(1),
				catchError(error => {
					if  (error.status === NOT_FOUND) {
						this.switchActiveStateNext(false);
					}
					return EMPTY;
				})
			).subscribe({
				next: constellations => {
					this.switchActiveStateNext(true);
				}
			});
	}

	public nextMonth(currentMonth: Date): Date {
		if (currentMonth.getMonth() == 11) {
			return new Date(currentMonth.getFullYear() + 1, 0, 1);
		} else {
			return new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1);
		}
	}
	
}
