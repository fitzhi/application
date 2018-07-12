import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Constants} from './constants';
import {MOCK_COLLABORATERS} from './mock/mock-collaboraters';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * List of collaborators corresponding to the search criteria.
   */
  private static theStaff: Collaborator[];

  /**
   * Current collaborator's identifier previewed on the formular.
   */
  private static currentId: number;

  constructor() {}

  /**
   * Register in a static array the list of collaboraters.
   */
  setDataArrayCollaboraters(theStaff: Collaborator[]) {
    DataService.theStaff = theStaff;
  }

  /**
   * Test if the static array of collaboraters has been set.
   */
  hasDataArrayCollaboratersAlreadySet(): boolean {
    return (DataService.theStaff != null);
  }

  /**
	* Reload the collaboraters for the passed criteria.
	*/
  reloadCollaboraters(myCriteria: string) {

    this.cleanUpCollaboraters();

    DataService.theStaff.push(...MOCK_COLLABORATERS.filter(
      collab =>
        (collab.firstName.toLowerCase().indexOf(myCriteria) > -1)
        || (collab.lastName.toLowerCase().indexOf(myCriteria) > -1)
    ));
  }

  /**
   * Cleanup the list of collaboraters involved in our service center.
   */
  cleanUpCollaboraters () {
    DataService.theStaff.length = 0;
  }

  /**
   * Return the collaborator associated with this id.
   */
  getCollaborater(id: number): Collaborator {
    let result: Collaborator;
    result = DataService.theStaff.find(collab => collab.id === id);
    if (typeof result !== 'undefined') {
      DataService.currentId = id;
    } else {
      DataService.currentId = null;
    }
    if (Constants.DEBUG) {
      console.log('Current identifier ' + DataService.currentId);
    }
    return result;
  }

  /**
   * Return the collaborator associated with this id.
   */
  nextCollaborater(id: number): Collaborator {
    return DataService.theStaff.find(collab => collab.id === id);
  }

  /**
   * Return the list of collaborators.
   */
  getStaff(): Collaborator[] {
    return DataService.theStaff;
  }


}
