import { CinematicService } from '../service/cinematic.service';
import { Constants } from '../constants';
import { Collaborator } from '../data/collaborator';
import { StaffService } from '../service/staff.service';
import { Injectable } from '@angular/core';

import { Observable, of, BehaviorSubject, Subject } from 'rxjs';
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
     * Reload the collaborators for the passed criteria.
     */
    reloadCollaborators(myCriteria: string, activeOnly: boolean) {

        function testCriteria(collab: Collaborator, index, array) {
            const firstname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
            const lastname = (typeof collab.lastName !== 'undefined') ? collab.lastName : '';
            return (
                (       (firstname.toLowerCase().indexOf(myCriteria) > -1)
                    ||  (lastname.toLowerCase().indexOf(myCriteria) > -1))
                && (activeOnly ? collab.active : true)
            );
        }

        this.cleanUpCollaborators();
        this.staffService.getAll().
            subscribe((staff: Collaborator[]) => StaffListService.theStaff.push(...staff.filter(testCriteria)),
                error => console.log(error),
                () => {
                    if (Constants.DEBUG) {
                        console.log('The staff collection is containing now ' + StaffListService.theStaff.length + ' records');
                    }
                });
    }

    /**
     * Cleanup the list of collaborators involved in our service center.
     */
    cleanUpCollaborators() {
        if (Constants.DEBUG) {
            if (StaffListService.theStaff == null) {
                console.log('INTERNAL ERROR : collection theStaff SHOULD NOT BE NULL, dude !');
            } else {
                console.log('Cleaning up the staff collection containing ' + StaffListService.theStaff.length + ' records');
            }
        }
        StaffListService.theStaff.length = 0;
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
     * Return the NEXT collaborator's id associated with this id in the staff list.
     */
    nextCollaboratorId(id: number): number {
        const index = StaffListService.theStaff.findIndex(collab => collab.idStaff === id);
        if (Constants.DEBUG) {
            console.log('Current index : ' + index);
            console.log('Staff size : ' + StaffListService.theStaff.length);
        }
        if (index < StaffListService.theStaff.length - 1) {
            return StaffListService.theStaff[index + 1].idStaff;
        } else {
            return undefined;
        }
    }

    /**
     * Return the PREVIOUS collaborator's id associated with this id in the staff list.
     */
    previousCollaboratorId(id: number): number {
        const index = StaffListService.theStaff.findIndex(collab => collab.idStaff === id);
        if (index > 0) {
            return StaffListService.theStaff[index - 1].idStaff;
        } else {
            return undefined;
        }
    }

    /**
     * Return the list of staff members.
     */
    getStaff(): Collaborator[] {
        return StaffListService.theStaff;
    }

}
