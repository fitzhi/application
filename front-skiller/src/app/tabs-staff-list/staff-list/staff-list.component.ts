import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { TabsStaffListService } from '../service/tabs-staff-list.service';
import { BaseComponent } from '../../base/base.component';
import { MatTableDataSource } from '@angular/material';
import { Constants } from '../../constants';

@Component({
  selector: 'app-staff-list',
  templateUrl: './staff-list.component.html',
  styleUrls: ['./staff-list.component.css']
})
export class StaffListComponent extends BaseComponent implements OnInit, OnDestroy {

  @Input('criteria')
  public criteria: string;

  @Input('activeOnly')
  public activeOnly: boolean;

  public dataSource;

  public displayedColumns: string[] = ['fullname', 'active', 'external'];

  constructor(
    private tabsStaffListComponent: TabsStaffListService,
    private router: Router) { super(); }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.log ('Searching staff members for criteria:' + this.criteria + ', activeOnly:' + this.activeOnly);
    }
    this.subscriptions.add(
      this.tabsStaffListComponent.search(this.criteria, this.activeOnly).subscribe(collaborators => {
          this.dataSource = new MatTableDataSource(collaborators);
      }));
  }

  /**
   * Return the CSS class corresponding to the active vs inactive status of a developer.
   */
  public class_active_inactive(active: boolean) {
    return active ? 'contributor_active' : 'contributor_inactive';
  }

  public routeStaff(idStaff: number) {
      this.tabsStaffListComponent.inMasterDetail = true;
      this.router.navigate(['/user/' + idStaff], {});
  }

  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }

}
