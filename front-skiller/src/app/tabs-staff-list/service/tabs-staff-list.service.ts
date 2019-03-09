import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject, Observable, Subscription } from 'rxjs';
import { StaffListCriteria } from './staffListCriteria';
import { Collaborator } from '../../data/collaborator';
import { StaffService } from '../../service/staff.service';
import { Constants } from '../../constants';

@Injectable({
  providedIn: 'root'
})
export class TabsStaffListService {

  /**
   * New search is made by the developer.
   */
  public search$ = new Subject<StaffListCriteria>();

  subscription: Subscription;

  constructor(private staffService: StaffService) { }

  /**
   * Add a tab of results in the tab containter.
   */
  public addTabResult(criteria: string, activeOnly: boolean) {
    this.search$.next(new StaffListCriteria(criteria, activeOnly));
  }

  public search(criteria: string, activeOnly: boolean): Subject<Collaborator[]>  {

    const collaborator$ = new Subject<Collaborator[]>();

    const collaborator = [];

    function testCriteria(collab, index, array) {
        const firstname = (typeof collab.firstName !== 'undefined') ? collab.firstName : '';
        const lastname = (typeof collab.lastName !== 'undefined') ? collab.lastName : '';
        return (
            (       (firstname.toLowerCase().indexOf(criteria) > -1)
                ||  (lastname.toLowerCase().indexOf(criteria) > -1))
            && (activeOnly ? collab.isActive : true)
        );
    }

    this.subscription = this.staffService.getAll().
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
                collaborator$.next(collaborator);
                setTimeout(this.subscription.unsubscribe(), 100);
            });

    return collaborator$;
  }
}
