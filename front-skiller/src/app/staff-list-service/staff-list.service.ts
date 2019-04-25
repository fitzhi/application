import { Constants } from '../constants';
import { Collaborator } from '../data/collaborator';
import { StaffService } from '../service/staff.service';
import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class StaffListService {

    /**
     * List of collaborators corresponding to the search criteria.
     */
    private static theStaff: Collaborator[] = [];

    constructor(
        private staffService: StaffService) {
    }

    /**
     * Return the collaborator associated with this id.
     */
    getCollaborator(id: number): Observable<Collaborator> {

        let foundCollab: Collaborator = null;
        foundCollab = StaffListService.theStaff.find(collab => collab.idStaff === id);

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
        return StaffListService.theStaff;
    }

}
