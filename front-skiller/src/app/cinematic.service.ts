import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { SearchFunction } from "./SearchFunction";

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

	private emitActualComponentSource = new Subject<Number>();

	newFormDisplayEmitted$ = this.emitActualComponentSource.asObservable();

	/*
	* Interface of searching
	*/
	search: SearchFunction;

	constructor() { 
	}
  
 	setForm (form: Number) {
    	/**
    	* Fire the event. Has to be at the end of the method.
    	*/
    	this.emitActualComponentSource.next(form);
	}
	
	/**
	* Setting the search engine <i>(if any)</i> for this form
	*/
	setSearch(search: SearchFunction) {
		this.search = search;
	}

}
