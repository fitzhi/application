import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Constants} from './constants';
import {MOCK_COLLABORATERS} from './mock/mock-collaboraters';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * List of collaborators corresponding to the search criteria.
   */
  private static theStaff: Collaborator[] = [];

  /**
   * Current collaborator's identifier previewed on the fom.
   */
  private emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  constructor() {
  }

  /**
	* Reload the collaborators for the passed criteria.
	*/
  reloadCollaborators(myCriteria: string) {

    this.cleanUpCollaborators();

    DataService.theStaff.push(...MOCK_COLLABORATERS.filter(
      collab =>
        (collab.firstName.toLowerCase().indexOf(myCriteria) > -1)
        || (collab.lastName.toLowerCase().indexOf(myCriteria) > -1)
    ));
    if (Constants.DEBUG) {
      console.log('the staff collection is containing now ' + DataService.theStaff.length + ' records');
    }
  }

  /**
   * Cleanup the list of collaborators involved in our service center.
   */
  cleanUpCollaborators() {
    if (Constants.DEBUG) {
      if (DataService.theStaff == null) {
        console.log('INTERNAL ERROR : collection theStaff SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the staff collection containing ' + DataService.theStaff.length + ' records');
      }
    }
    DataService.theStaff.length = 0;
  }

  /**
   * Return the collaborator associated with this id.
   */
  getCollaborator(id: number): Collaborator {

    let result: Collaborator;
    result = MOCK_COLLABORATERS.find(collab => collab.id === id);

    if (typeof result !== 'undefined') {
      this.emitActualCollaboratorDisplay.next(id);
    } else {
      this.emitActualCollaboratorDisplay.next(undefined);
    }

    if (Constants.DEBUG) {
      console.log('Current identifier ' + id);
    }

    return result;
  }


  /**
   * Return the NEXT collaborator's id associated with this id in the staff list.
   */
  nextCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (Constants.DEBUG) {
      console.log ('Current index : ' + index);
      console.log ('Staff size : ' + DataService.theStaff.length);
    }
    if (index < DataService.theStaff.length - 1) {
      return DataService.theStaff[index + 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the PREVIOUS collaborator's id associated with this id in the staff list.
   */
  previousCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (index > 0) {
      return DataService.theStaff[index - 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the list of collaborators.
   */
  getStaff(): Collaborator[] {
    return DataService.theStaff;
  }


}
