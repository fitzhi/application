import { AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { UserSetting } from 'src/app/base/user-setting';
import { Filename } from 'src/app/data/filename';
import { traceOn } from 'src/app/global';
import { BaseDirective } from '../../../../base/base-directive.directive';

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
export class ListFilenamesComponent extends BaseDirective implements OnInit, OnDestroy, AfterViewInit {

	@Input() filenames: MatTableDataSource<Filename>;

	public tblColumns: string[] = ['filename', 'lastCommit'];

	/**
	 * The table in the component
	 */
	@ViewChild(MatTable) table: MatTable<any>;

	/**
	 * The paginator of the displayed datasource.
	 */
	@ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

	/**
	 * Key used to save the page size in the local storage.
	 */
	public pageSize = new UserSetting('list-filenames.pageSize', 5);

	constructor() { super(); }

	ngOnInit() {
		if (traceOn()) {
			console.groupCollapsed('Filenames');
			console.table  (this.filenames.data);
			console.groupEnd();
		}
	}

	ngAfterViewInit(): void {
		this.filenames.paginator = this.paginator;
	}

	/**
	 * This method is invoked if the user change the page size.
	 * @param $pageEvent event
	 */
	public page($pageEvent: PageEvent) {
		this.pageSize.saveSetting($pageEvent.pageSize);
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
