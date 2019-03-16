import { Component, OnInit, Input, OnDestroy, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TabsStaffListService } from '../service/tabs-staff-list.service';
import { BaseComponent } from '../../base/base.component';
import { MatTableDataSource, MatSort, MatSortable, Sort } from '@angular/material';
import { Constants } from '../../constants';
import { StaffListCriteria } from '../service/staffListCriteria';
import { Profile } from '../../data/profile';
import { ReferentialService } from '../../service/referential.service';
import { Experience } from '../../data/experience';

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

  @ViewChild(MatSort) sort: MatSort;

  public displayedColumns: string[] = ['firstName lastName', 'level', 'skills', 'isActive', 'external'];

  /**
   * Profiles array retrieved from the referential.
   */
  private profiles: Profile[];

  constructor(
    private tabsStaffListComponent: TabsStaffListService,
    private referentialService: ReferentialService,
    private router: Router) {
      super();
    }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.log ('Searching staff members for criteria:' + this.criteria + ', activeOnly:' + this.activeOnly);
    }

    this.profiles = this.referentialService.profiles;

    this.subscriptions.add(
      this.tabsStaffListComponent.search(this.criteria, this.activeOnly).subscribe(collaborators => {
          this.dataSource = new MatTableDataSource(collaborators);
          this.dataSource.sortingDataAccessor = (item, property) => {
            switch (property) {
              case 'firstName lastName':
                return item.firstName.toLocaleLowerCase() + ' ' + item.lastName.toLocaleLowerCase();
              case 'isActive':
                return item.isActive;
              case 'level':
                return this.title(item.level);
              case 'skills':
                return this.skills(item.experiences);
              case 'external':
                return item.external;
            }
          };
          this.dataSource.sort = this.sort;
      }));
      const key = this.tabsStaffListComponent.key(new StaffListCriteria(this.criteria, this.activeOnly));
      const context = this.tabsStaffListComponent.getContext(key);
      if (context.isSorted()) {
        this.sort.sort(context.getSortConfiguration());
      }
  }

  sortData (event: Sort) {
    const key = this.tabsStaffListComponent.key(new StaffListCriteria(this.criteria, this.activeOnly));
    const context = this.tabsStaffListComponent.getContext(key);
    context.storeSortingContext (event.active, event.direction);
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
   * @param levelCode code of level of a collaborator
   * @returns  the title corresponding to the code
   */
  public title(levelCode: string) {
    const found = this.profiles.find(profile => (profile.code === levelCode));
    if (typeof found === 'undefined') {
      return '';
    } else {
      return found.title;
    }
  }

  public skills(experiences: Experience[]): string {
    let mainSkills = '';
    let evaluatedSkill: Experience[];
    for (let level = Constants.LEVEL_Expert; (level >= Constants.LEVEL_Beginner); level--) {
      evaluatedSkill = experiences.filter(expe => (expe.level === level));
      evaluatedSkill.forEach(experience => {mainSkills += experience.title + ', '; });
    }
    return mainSkills;
  }

  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }

}
