import { Component, ViewEncapsulation, OnInit, OnDestroy } from '@angular/core';
import { Constants } from '../constants';
import { CinematicService } from '../service/cinematic.service';
import { Router } from '@angular/router';
import { TabsStaffListService } from './service/tabs-staff-list.service';
import { BaseComponent } from '../base/base.component';

@Component({
	selector: 'app-tabs-staff-list',
	templateUrl: './tabs-staff-list.component.html',
	styleUrls: ['./tabs-staff-list.component.css'],
	encapsulation: ViewEncapsulation.None
})
export class TabsStaffListComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Tab keys. Each tab has a key.
	 */
	tabKeys: string[] = [];

	/**
	 * Tabs created on the main panel.
	 */
	tabs = [];

	selected: number;

	constructor(
		private tabsStaffListService: TabsStaffListService,
		private cinematicService: CinematicService,
		private router: Router) { super(); }

	ngOnInit() {
		this.cinematicService.setForm(Constants.TABS_STAFF_LIST, this.router.url);

		this.subscriptions.add(
			this.tabsStaffListService.search$.subscribe(envelope => {
				console.log ('envelope', envelope);
				setTimeout(() => {
					this.tabKeys.push(this.tabsStaffListService.key(envelope));
					this.add(envelope.criteria);
				}, 0);
			}));

		this.reloadHistory();
	}

	/**
	 * Reload the search history.
	 * This method will be called during the ngOnInit process to retrieve the search context.
	 */
	public reloadHistory() {
		this.tabsStaffListService.staffListContexts.forEach(criterias => {
			this.tabKeys.push(this.tabsStaffListService.key(criterias));
			this.add(criterias.criteria);
		});
		this.selected = this.tabsStaffListService.activeTab;
	}

	/**
	 * Criteria passed to each new tab.
	 */
	criteria(index: number) {
		return this.tabsStaffListService.staffListContexts.get(this.tabKeys[index]).criteria;
	}

	/**
	 * activeOnly activeOnly filter passed to each new tab.
	 */
	activeOnly(index: number) {
		return this.tabsStaffListService.staffListContexts.get(this.tabKeys[index]).activeOnly;
	}

	public add(title: string) {
		this.tabs.push(title);
		this.selected = this.tabs.length - 1;
	}

	/**
	 * Method fired whe the end user has clicked a new tab.
	 * @param activeTab the index of the new activated tab
	 */
	public select(activeTab: number) {
		this.tabsStaffListService.activeTab = activeTab;
		this.tabsStaffListService.activeKey = this.tabKeys[activeTab];
	}

	/**
	 * Remove the tab from the result views container.
	 * @param index of the tab.
	 */
	public remove(index: number) {
		const key = this.tabKeys[index];
		this.tabs.splice(index, 1);
		this.tabKeys.splice(index, 1);
		this.tabsStaffListService.removeHistory(key);
	}

	/**
	 * Truncate the label of the the lab.
	 * @param label label theoric.
	 * @param length maximum length for the label.
	 */
	truncate(label: string, length: number) {
		if (label.length > length) {
			return label.substr(0, length) + '...';
		}
		return label;
	}

	/**
	 * Calling the base class to unsubscribe all subscriptions.
	 */
	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
