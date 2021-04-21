import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Constants } from '../../../../constants';
import { BaseComponent } from '../../../../base/base.component';
import { Filename } from 'src/app/data/filename';
import { FilenamesDataSource } from '../filenames-data-source';
import { traceOn } from 'src/app/global';

/**
 * This component hosts a table with the source filenames of a repository directory
 * 
 * It can be for example, the package in a Java project 
 */
@Component({
	selector: 'app-list-filenames',
	templateUrl: './list-filenames.component.html',
	styleUrls: ['./list-filenames.component.css']
})
export class ListFilenamesComponent extends BaseComponent implements OnInit, OnDestroy {

	public tblColumns: string[] = ['filename', 'lastCommit'];

	@Input() filenames: FilenamesDataSource;

	constructor() { super(); }

	ngOnInit() {
		if (traceOn()) {
			this.subscriptions.add(
				this.filenames.filenamesSubject$.subscribe((elements: Filename[]) => {
					if ((elements) && (elements.length > 0)) {
						console.groupCollapsed('Filenames');
						elements.forEach(element => console.log  (element.filename));
						console.groupEnd();
					}
			}));
		}
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
