import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { Contributor } from 'src/app/data/contributor';
import { Filename } from 'src/app/data/filename';
import { BaseDirective } from '../../../base/base-directive.directive';

@Component({
	selector: 'app-node-detail',
	templateUrl: './node-detail.component.html',
	styleUrls: ['./node-detail.component.css']
})
export class NodeDetailComponent extends BaseDirective implements OnInit, OnDestroy {

	@Input() filenames: MatTableDataSource<Filename>;

	@Input() contributors: MatTableDataSource<Contributor>;

	@Input() location: BehaviorSubject<Filename[]>;

	public node: Filename[];

	constructor() {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.location.subscribe({
				next: element => this.node = element
			}));
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
