import { Injectable } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class StaffListListenerService {

	/**
	 * Interval to reload the staff from the backend.
	 */
	public intervalLoadStaff$ = interval(60000);

	/**
	 * The subscription reading the interval.
	 */
	public sub: Subscription = null;

	/**
	 * Interrupt the **interval** listener in charge of loading periodicaly the staff collection.
	 */
	public interruptStaffListener() {
		if (traceOn()) {
			console.log('interruptStaffListener');
		}
		// We might fall in error, before the subscription on 'intervalLoadStaff$' has been started.
		if (this.sub) {
			this.sub.unsubscribe();
			// and we initialize the subscriber.
			this.sub = null;
		}
	}
}
