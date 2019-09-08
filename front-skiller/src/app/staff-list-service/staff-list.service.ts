import { Constants } from '../constants';
import { Collaborator } from '../data/collaborator';
import { StaffService } from '../service/staff.service';
import { Injectable } from '@angular/core';

import { Observable, of, Subject, BehaviorSubject } from 'rxjs';
import { tap, take } from 'rxjs/operators';

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

	constructor(private staffService: StaffService) {
	}

	/**
	 * Load the staff members and store them in the array allStaff.
	 */
	loadStaff() {
		this.staffService.getAll()
			.pipe(take(1))
			.subscribe(staff => {
				this.allStaff = [];
				staff.forEach(entry => this.allStaff.push(entry));
				if (Constants.DEBUG) {
					console.groupCollapsed(this.allStaff.length + ' staff members loaded');
					this.allStaff.forEach (item => console.log(item.idStaff + ' ' + item.firstName + ' ' + item.lastName));
					console.groupEnd();
				}
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
			if (Constants.DEBUG) {
				console.log('Direct access for : ' + id);
			}
			return this.staffService.get(id).pipe(tap(
				(collab: Collaborator) => {
					if (Constants.DEBUG) {
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
	 * Return the list of staff members.
	 */
	getStaff(): Collaborator[] {
		return this.allStaff;
	}

}
