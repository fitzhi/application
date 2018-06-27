import { Injectable } from '@angular/core';
import {Collaborater} from './data/collaborater';
import {MOCK_COLLABORATERS} from './mock/mock-collaboraters';

@Injectable({
  providedIn: 'root'
})
export class DataService {

	collaboraters: Collaborater[];

  	constructor() { }
  
	setDataArrayCollaboraters(collaboraters: Collaborater[]) {
		this.collaboraters =  collaboraters;
	}

	/**
	* Reload the collaboraters for the passed criteria.
	*/
  	reloadCollaboraters (myCriteria: string) {
  	
 		this.collaboraters.length = 0;
 
 		console.log (myCriteria);
 		console.log (MOCK_COLLABORATERS.filter( 
 			collab => collab.firstName.indexOf(myCriteria) > -1));
 		console.log (MOCK_COLLABORATERS);
 		
 		this.collaboraters.push(...MOCK_COLLABORATERS.filter( 
 			collab => collab.firstName.indexOf(myCriteria) > -1));
 	}
  
}
