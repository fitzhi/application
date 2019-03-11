import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable, Subscription } from 'rxjs';
import { StaffListCriteria } from './staffListCriteria';
import { Collaborator } from '../../data/collaborator';
import { StaffService } from '../../service/staff.service';
import { Constants } from '../../constants';
import { StaffListContext } from '../../data/staff-list-context';
import { MessageService } from '../../message/message.service';

@Injectable({
    providedIn: 'root'
})
export class TabsStaffListService {

    /**
     * List of criterias dispplay on the tab staff lists dashboard.
     */
    public staffListContext: Map<String, StaffListContext> = new Map<String, StaffListContext>();

    /**
     * Index of the active tab.
     */
    public activeTab = 0;

    /**
     * The tab content is used in master/detail way. The prev and the next button are visible.
     */
    public inMasterDetail = false;

    /**
     * New search is made by the developer.
     */
    public search$ = new Subject<StaffListCriteria>();

    constructor(
        private staffService: StaffService,
        private messageService: MessageService) {

        this.search$.subscribe(criterias => {
            if (Constants.DEBUG) {
                console.log ('Adding criterias ' + criterias.criteria);
            }

            if (!this.staffListContext.has(this.key(criterias))) {
                this.staffListContext.set (
                    this.key(criterias),
                    new StaffListContext(criterias)
                );
           }
        });
     }

     /**
      * Construct the key of the criterias.
      */
     key (criterias: StaffListCriteria): string {
        return criterias.criteria + '-' + (criterias.activeOnly ? '1' : '0');
     }

    /**
     * Add a tab of results in the tab containter.
     */
    public addTabResult(criteria: string, activeOnly: boolean) {

        const myCriteria = new StaffListCriteria(criteria, activeOnly);
        if (this.staffListContext.has(this.key(myCriteria))) {
            this.messageService.info('This criteria is already present!');
            return;
        }
        this.search$.next(myCriteria);
    }

    /**
     * Searching staff members corresponding to the 2 passed criterias.
     * @param criteria the criteria
     * @param activeOnly active only Yes/No
     */
    public search(criteria: string, activeOnly: boolean): Subject<Collaborator[]> {

       const collaborator = [];

       const collaborator$ = new Subject<Collaborator[]>();

        const key = this.key(new StaffListCriteria(criteria, activeOnly));

        if (this.staffListContext.has(key)) {
            const context = this.staffListContext.get(key);
            if (context.staffSelected.length > 0) {
                if (Constants.DEBUG) {
                    console.log('Using cache for key ' + key + ' ' + context.staffSelected.length + ' records');
                }
                setTimeout(() => {
                    context.staffSelected.forEach(c => collaborator.push(c));
                    collaborator$.next(collaborator);
                }, 0);
                return collaborator$;
            }
        }

        function testCriteria(collab, index, array) {
            const firstname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
            const lastname = (typeof collab.lastName !== 'undefined') ? collab.lastName : '';
            return (
                ((firstname.toLowerCase().indexOf(criteria) > -1)
                    || (lastname.toLowerCase().indexOf(criteria) > -1))
                && (activeOnly ? collab.isActive : true)
            );
        }

        this.staffService.getAll().
            subscribe((staff: Collaborator[]) =>
                collaborator.push(...staff.filter(testCriteria)),
                error => console.log(error),
                () => {
                    if (Constants.DEBUG) {
                        console.log('The staff collection is containing now ' + collaborator.length + ' records');
                        console.groupCollapsed('Staff members found : ');
                        collaborator.forEach(collab => console.log(collab.firstName + ' ' + collab.lastName));
                        console.groupEnd();
                    }
                    if (this.staffListContext.has(key)) {
                        if (Constants.DEBUG) {
                            console.log('Saving collaborators for key ' + key);
                        }
                        this.staffListContext.get(key).store(collaborator);
                    }
                    collaborator$.next(collaborator);
                });
        return collaborator$;
    }

    /**
     * Remove from history the search entry corresponding to this key.
     */
    public removeHistory (key: string) {
        this.staffListContext.delete(key);
        if (Constants.DEBUG) {
            console.groupCollapsed ('Remaining tabs');
            this.staffListContext.forEach(criterias => console.log (criterias.criteria));
            console.groupEnd();
        }
    }
}
