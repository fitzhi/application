import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

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

	/**
	 * Button activated by the mouse.
	 *
	 * This property is a used to show, or hide the help div
	 */
	public activatedButton = 0;

	/**
	 * Initialize the activated button
	 */
	public initActivatedButton() {
		this.activatedButton = 0;
	}

	/**
	 * Return **true** if the given active button has been selected.
	 *
	 * @param activeButton the given active button
	 */
	public isButtonActive(activeButton: number) {
		return (activeButton === this.activatedButton);
	}
}
