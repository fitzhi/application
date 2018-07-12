import {Injectable} from '@angular/core';
import {Collaborater} from './data/collaborater';
import {MOCK_COLLABORATERS} from './mock/mock-collaboraters';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private static theStaff: Collaborater[];

  constructor() {}

  /**
   * Register in a static array the list of collaboraters.
   */
  setDataArrayCollaboraters(theStaff: Collaborater[]) {
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
   * Return the colloborater associated with this id.
   */
  getCollaborater(id: number): Collaborater {
    return DataService.theStaff.find(collab => collab.id ==   id);
  }

  /**
   * Return the list of collaboraters.
   */
  getStaff(): Collaborater[] {
    return DataService.theStaff;
  }


}
