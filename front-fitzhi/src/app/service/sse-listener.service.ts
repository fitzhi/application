import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { traceOn } from '../global';

/**
 * This listener listen the events emited by a server and returns them in ab obserbale
 */
@Injectable({
	providedIn: 'root'
})
export class SseListenerService {

	constructor(private _zone: NgZone) { }

	getServerSentEvent$(url: string): Observable<any> {
		if (traceOn()) {
			console.log ('Starting to listen the server %s', url);
		}
		return new Observable(observer => {
			const eventSource = this.getEventSource(url);

			eventSource.onmessage = event => {
				this._zone.run(() => {
					observer.next(event);
				});
			};

			eventSource.onerror = error => {
				this._zone.run(() => {
					observer.error(error);
				});
			};
		});
	}

	private getEventSource(url: string): EventSource {
		return new EventSource(url);
	}
}
