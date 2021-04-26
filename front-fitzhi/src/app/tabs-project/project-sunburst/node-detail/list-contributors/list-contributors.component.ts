import { Component, OnInit, Input, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { BaseComponent } from '../../../../base/base.component';
import { traceOn } from 'src/app/global';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Contributor } from 'src/app/data/contributor';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { UserSetting } from 'src/app/base/user-setting';

@Component({
	selector: 'app-list-contributors',
	templateUrl: './list-contributors.component.html',
	styleUrls: ['./list-contributors.component.css']
})
export class ListContributorsComponent extends BaseComponent implements OnInit, OnDestroy, AfterViewInit {

	@Input() contributors: MatTableDataSource<Contributor>;

	public tblColumns: string[] = ['fullname', 'active', 'external', 'lastCommit'];

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
	public pageSize = new UserSetting('list-contributors.pageSize', 5);

	constructor() { super(); }

	ngAfterViewInit(): void {
		this.contributors.paginator = this.paginator;
	}

	ngOnInit() {
		if (traceOn()) {
			console.groupCollapsed('Contributors');
			console.table(this.contributors.data);
			console.groupEnd();
		}
	}


	/**
	 * Return the CSS class corresponding to the active vs inactive status of a developer.
	 */
	public class_active_inactive(active: boolean) {
		return active ? 'contributor_active' : 'contributor_inactive';
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
