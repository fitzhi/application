import { Injectable } from '@angular/core';
import {Collaborater} from './data/collaborater';
import {MOCK_COLLABORATERS} from './mock/mock-collaboraters';

@Injectable({
  providedIn: 'root'
})
export class DataService {

	private theStaff: Collaborater[];

  	constructor() { }
  
	setDataArrayCollaboraters(theStaff: Collaborater[]) {
		this.theStaff =  theStaff;
	}
	
 	hasDataArrayCollaboratersAlreadySet(): boolean {
 		return (this.theStaff != null);
 	}

	/**
	* Reload the collaboraters for the passed criteria.
	*/
  	reloadCollaboraters (myCriteria: string) {
  	
 		this.theStaff.length = 0;
 
 		this.theStaff.push(...MOCK_COLLABORATERS.filter( 
 			collab => 
 				(collab.firstName.toLowerCase( ).indexOf(myCriteria) > -1) 
 			|| 	(collab.lastName.toLowerCase( ).indexOf(myCriteria) > -1) 
		));
 	}
 	 	
 	getCollaborater(id: number): Collaborater {
 		return this.theStaff.find (collab => collab.id == id);
 	}
 	
 	getStaff(): Collaborater[] {
 		return this.theStaff;
 	}
  
	  
}
