import { Component, OnInit, OnDestroy } from '@angular/core';
import { Constants } from '../../constants';
import { ActivatedRoute } from '@angular/router';
import { BaseDirective } from '../../base/base-directive.directive';
import { traceOn } from '../../global';

@Component({
	selector: 'app-error',
	templateUrl: './error.component.html',
	styleUrls: ['./error.component.css']
})
export class ErrorComponent extends BaseDirective implements OnInit, OnDestroy {

	error: string;

	constructor(private route: ActivatedRoute) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(this.route.queryParams.subscribe(params => {
			if (traceOn()) {
				console.log('params[\'error\'] ' + params['error']);
			}
			if (params['error'] == null) {
				this.error = 'Unknown error !!!';
			} else {
				this.error = params['error'];
			}
		}));
	}

	/**
	 * Unsubscription implemented in the BaseComponent
	 */
	public ngOnDestroy() {
		super.ngOnDestroy();
	}

}
