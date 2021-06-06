import { Component, EventEmitter, HostBinding, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { BaseComponent } from 'src/app/base/base.component';
import { traceOn } from 'src/app/global';

@Component({
	selector: 'app-skyline-icon',
	templateUrl: './skyline-icon.component.html',
	styleUrls: ['./skyline-icon.component.css']
})
export class SkylineIconComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Width of the skyline icon
	 */
	@HostBinding('style.--skyline-icon-width')
	@Input() width = '100px';

	/**
	 * Height of the skyline icon
	 */
	@HostBinding('style.--skyline-icon-height')
	@Input() height = '100px';

	/**
	 * We inform the parent form that the Skykline Dashboard has been selected.
	 */
	@Output() dashboardSelected$ = new EventEmitter<number>();

	/**
	 * An observable which informs this component that the user has clicked on it
	 */
	@Input() selected$ = new BehaviorSubject<boolean>(false);

	/**
	 * This class name whil marks the fact that the icon has selected, not not.
	 */
	public classSelected = '';

	constructor() { super(); }

	ngOnInit(): void {
		this.subscriptions.add(
			this.selected$.subscribe({
				next: selected => {
					this.classSelected = (selected) ? 'selected' : '';
			}})
		);
	}

	/**
	 * The user has clicked on the component.
	 */
	public click() {
		if (traceOn()) {
			console.log ('Clicking on the Skyline icon');
		}
		this.dashboardSelected$.emit(1);
	}

	/**
	 * Removing useless subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
