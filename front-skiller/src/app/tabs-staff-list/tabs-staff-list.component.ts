import { Component, OnInit } from '@angular/core';
import { Constants } from '../constants';
import { CinematicService } from '../service/cinematic.service';
import { Router } from '@angular/router';
import { TabsStaffListService } from './service/tabs-staff-list.service';

@Component({
  selector: 'app-tabs-staff-list',
  templateUrl: './tabs-staff-list.component.html',
  styleUrls: ['./tabs-staff-list.component.css']
})
export class TabsStaffListComponent implements OnInit {

  tabs = [];

  /**
   * criteria passed to each new tab
   */
  criteria: string;

  /**
   * activeOnly activeObly filter passed to each new tab
   */
  activeOnly: boolean;

  selected: number;

  constructor(
    private tabsStaffListComponent: TabsStaffListService,
    private cinematicService: CinematicService,
    private router: Router) { }

  ngOnInit() {
    this.cinematicService.setForm(Constants.TABS_STAFF_LIST, this.router.url);
    this.tabsStaffListComponent.search$.subscribe(envelope => {
      this.criteria = envelope.criteria;
      this.activeOnly = this.activeOnly;
      this.add(envelope.criteria);
    });
  }

  public add(title: string) {
    this.tabs.push(title);
    this.selected = this.tabs.length - 1;
  }

  public select(event: any) {
  }

  public remove(index: number) {
    this.tabs.splice(index, 1);
  }
}
