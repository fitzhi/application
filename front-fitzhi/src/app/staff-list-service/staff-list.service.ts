import { Constants } from '../constants';
import { Collaborator } from '../data/collaborator';
import { StaffService } from '../service/staff.service';
import { Injectable } from '@angular/core';

import { Observable, of, Subject, BehaviorSubject } from 'rxjs';
import { tap, take, delay } from 'rxjs/operators';
import { Commit } from '../data/commit';
import { traceOn } from '../global';

@Injectable({
	providedIn: 'root'
})
export class StaffListService {

	/**
	 * List of collaborators corresponding to the search criteria.
	 */
	public allStaff$ = new BehaviorSubject<Collaborator[]>([]);

	/**
	 * List of collaborators corresponding to the search criteria.
	 */
	public allStaff: Collaborator[] = [];

	/**
	 * Constructor.
	 * @param staffService service un charge of accessing the backend to retrive the staff members.
	 */
	constructor(
		private staffService: StaffService) {
	}

	/**
	 * Load the staff members and store them in the array allStaff.
	 */
	loadStaff() {
		this.staffService.getAll()
			.pipe(
				take(1),
				tap(staff => {
					if (traceOn()) {
						console.groupCollapsed(this.allStaff.length + ' staff members loaded');
						this.allStaff.forEach (item => console.log(item.idStaff + ' ' + item.firstName + ' ' + item.lastName));
						console.groupEnd();
					}
				}))
			.subscribe(staff => {
				this.allStaff = staff;
				this.allStaff$.next(this.allStaff);
			});
	}


	/**
	 * Return the collaborator associated with this id.
	 */
	getCollaborator(id: number): Observable<Collaborator> {

		let foundCollab: Collaborator = null;
		foundCollab = this.allStaff.find(collab => collab.idStaff === id);

		if (typeof foundCollab !== 'undefined') {
			// We create an observable for an element of the cache in order to be consistent with the direct reading.
			return of(foundCollab);
		} else {
			// The collaborator's id is not, or no more, available in the cache
			// We try a direct access
			if (traceOn()) {
				console.log('Direct access for : ' + id);
			}
			return this.staffService.get(id).pipe(tap(
				(collab: Collaborator) => {
					if (traceOn()) {
						console.log('Direct access for : ' + id);
						if (typeof collab !== 'undefined') {
							console.log('Staff member found : ' + collab.firstName + ' ' + collab.lastName);
						} else {
							console.log('No staff found for id ' + id);
						}
					}
				}));
		}
	}

	/**
	 * Update the staff collection with a new or update staff data filled in the form-staff component.
	 * @param staff the given staff
	 */
	setFormStaff(staff: Collaborator) {
		const collaborator = this.allStaff.find(collab => (staff.idStaff === collab.idStaff));
		if (!collaborator) {
			this.allStaff.push(staff);
			return;
		}
		collaborator.firstName = staff.firstName;
		collaborator.lastName = staff.lastName;
		collaborator.nickName = staff.nickName;
		collaborator.login = staff.login;
		collaborator.email = staff.email;
		collaborator.level = staff.level;
		collaborator.active = staff.active;
		collaborator.dateInactive = staff.dateInactive;
		collaborator.external = staff.external;
	}

	/**
	 * Return the list of staff members.
	 */
	getStaff(): Collaborator[] {
		return this.allStaff;
	}

	/**
	 * @param idProject the project identifier
	 * @returns the last registered commit.
	 */
	retrieveLastCommit(idProject: number): Commit {

		const latest = new Commit(-1, '', '', '1970-01-01');
		this.allStaff.forEach(staff => {
			staff.missions.forEach(mission => {
				if (mission.idProject === idProject) {
					if (mission.lastCommit) {
						if (new Date(mission.lastCommit) > new Date(latest.dateCommit)) {
							latest.idStaff = staff.idStaff;
							latest.firstName = staff.firstName;
							latest.lastName = staff.lastName;
							latest.dateCommit = mission.lastCommit.toString();
						}
					}
				}});
			});
		return latest;
	}
}
