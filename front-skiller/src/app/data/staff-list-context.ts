import { Collaborator } from './collaborator';
import { StaffListCriteria } from '../tabs-staff-list/service/staffListCriteria';
import { MatSortable } from '@angular/material';
import { Constants } from '../constants';

class SortingContext {
  id: string;
  asc: boolean;
}

export class StaffListContext {
  public criteria: string;
  public activeOnly: boolean;
  public staffSelected: Collaborator[] = [];

  sortingContext: SortingContext;

  public constructor(criterias: StaffListCriteria) {
    this.criteria = criterias.criteria;
    this.activeOnly = criterias.activeOnly;
    this.sortingContext = new SortingContext();
  }

  store(collaborators: Collaborator[]) {
    collaborators.forEach(collaborator => {
      this.staffSelected.push(JSON.parse(JSON.stringify(collaborator)));
    });
  }

  /**
  * Save the sorting context of the table
  * @param id column identified sorted
  * @param direction direction of the sort (ASC vs DESC)
  */
  public storeSortingContext(id: string, direction: string) {
    this.sortingContext.id = id;
    this.sortingContext.asc = (direction === 'asc');
  }

  /**
   * @returns TRUE if a sort request has been executed.
   */
  public isSorted(): boolean {
    if (Constants.DEBUG) {
      console.log ('For key "' + this.criteria + '" , the method isSorted() returns ' +
                  (this.sortingContext.id !== null));
    }
    return (this.sortingContext.id !== null);
  }

   /**
    * get the sort configuration.
    */
   public getSortConfiguration(): MatSortable {
    return  <MatSortable>({id: this.sortingContext.id, start: (this.sortingContext.asc ? 'asc' : 'desc')});
  }

}
