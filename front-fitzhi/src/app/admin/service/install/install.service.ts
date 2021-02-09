import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

/**
 * This service is handling the fact that the installation on this desktop has been completed.
 */
@Injectable({
  providedIn: 'root'
})
export class InstallService {

	/**
	 * This behaviorSubject will emit a **true** if the installation has been completed on this desktop.
	 */
	public installComplete$ = new BehaviorSubject<boolean>(false);

  	constructor() { 
		this.installComplete$.next(("1" === localStorage.getItem("installation"))); 
  	}

	/**
	 * Save the fact that the install is complete.
	 */
  	public installComplete() {
		localStorage.setItem("installation", "1");
		this.installComplete$.next(true);
	  }

}
