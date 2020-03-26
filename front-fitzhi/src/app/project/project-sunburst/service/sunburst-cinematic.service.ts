import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class SunburstCinematicService {

	/**
	 * This `observable` will emit a `TRUE` to start the listening of server side events.
	 */
	public listenEventsFromServer$ = new BehaviorSubject<boolean>(false);

	/**
	 * This `observable` will emit a `TRUE` to force the refresh of the chart.
	 */
	public refreshChart$ = new BehaviorSubject<boolean>(false);

}
