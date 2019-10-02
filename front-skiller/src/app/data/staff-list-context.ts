import { Collaborator } from './collaborator';
import { ListCriteria } from './listCriteria';
import { MatSortable } from '@angular/material/sort';
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

	public constructor(criterias: ListCriteria) {
		this.criteria = criterias.criteria;
		this.activeOnly = criterias.activeOnly;
		this.sortingContext = new SortingContext();
	}

	/**
	 * Store the array of collaborators into the underlying collection.
	 * @param collaborators array of collaborators retrieved.
	 */
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
			console.log('For key "' + this.criteria + '" , the method isSorted() returns ' +
				(this.sortingContext.id !== null));
		}
		return (this.sortingContext.id !== null);
	}

	/**
	 * get the sort configuration.
	 */
	public getSortConfiguration(): MatSortable {
		return <MatSortable>({ id: this.sortingContext.id, start: (this.sortingContext.asc ? 'asc' : 'desc') });
	}

}
