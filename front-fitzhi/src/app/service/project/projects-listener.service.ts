import { Injectable } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class ProjectsListenerService {

	/**
	 * Interval to reload the projects from the backend.
	 */
	public intervalLoadProjects$ = interval(60000);

	/**
	 * The subscription reading the interval.
	 */
	public sub: Subscription = null;

	/**
	 * Interrupt the **interval** listener in charge of loading periodicaly the projects.
	 */
	public interruptProjectsListener() {
		if (traceOn()) {
			console.log('interruptProjectsListener');
		}
		// We might fall in error, before the subscription on 'intervalLoadProjects$' has been started.
		if (this.sub) {
			this.sub.unsubscribe();
			// and we initialize the subscriber.
			this.sub = null;
		}
	}
}
