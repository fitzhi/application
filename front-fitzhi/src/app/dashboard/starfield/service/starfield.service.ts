import { Injectable } from '@angular/core';
import { trace } from 'console';
import { BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { DashboardColor } from 'src/app/service/dashboard/dashboard-color';
import { StaffListService } from 'src/app/service/staff-list-service/staff-list.service';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { Constellation } from '../data/constellation';
import { Star } from '../data/star';
import { StarfieldFilter } from '../data/starfield-filter';

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
	 * Actual filters chosen by the end user for the starfield component.
	 */
	public filter = new StarfieldFilter();

	private helpPanelVisible = false;

	private helpPanelVisibleSubject$ = new BehaviorSubject<boolean>(this.helpPanelVisible);

	public helpPanelVisible$ = this.helpPanelVisibleSubject$.asObservable();

	constructor(private staffListService: StaffListService) { }

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
						const constellation = constellations.find(constellation => constellation.idSkill === experience.id);
						if (constellation) {
							constellation.count = constellation.count + experience.level;
						} else {
							constellations.push (new Constellation(experience.id, experience.level));
						}
					})
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
}
