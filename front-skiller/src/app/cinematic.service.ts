import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

	private emitActualComponentSource = new Subject<Number>();

	newFormDisplayEmitted$ = this.emitActualComponentSource.asObservable();

	constructor() { 
	}
  
 	setForm (form: Number) {
    	/**
    	* Fire the event. Has to be at the end of the method.
    	*/
    	this.emitActualComponentSource.next(form);
	}

}
