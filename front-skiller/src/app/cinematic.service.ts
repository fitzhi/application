import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

	private emitActualComponentSource = new Subject<string>();

	newFormDisplayEmitted$ = this.emitActualComponentSource.asObservable();

	/**
	 * Form is in searching mode
	*/
	is_searching: boolean;
	
	constructor() { 
		this.is_searching = false;
	}
  
 	setForm (form: string, is_searching: boolean) {
    	this.is_searching = is_searching;
    	/**
    	* Fire the event. Has to be at the end of the method.
    	*/
    	this.emitActualComponentSource.next(form);
	}

}
