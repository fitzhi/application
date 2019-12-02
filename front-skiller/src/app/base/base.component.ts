import { OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

export class BaseComponent implements OnDestroy {

	/**
	 * Array of subscriptions activated on the child component.
	 */
	subscriptions: Subscription = new Subscription();

	dateConstruction: string;

	constructor() {
		const today = new Date();
		this.dateConstruction = today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds();
	}

	public ngOnDestroy() {
		this.subscriptions.unsubscribe();
	}
}
