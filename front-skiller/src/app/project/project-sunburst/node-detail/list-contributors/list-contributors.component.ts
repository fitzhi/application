import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Constants } from '../../../../constants';
import { BaseComponent } from '../../../../base/base.component';

@Component({
  selector: 'app-list-contributors',
  templateUrl: './list-contributors.component.html',
  styleUrls: ['./list-contributors.component.css']
})
export class ListContributorsComponent extends BaseComponent implements OnInit, OnDestroy {

  public tblColumns: string[] = ['fullname', 'active', 'external', 'lastCommit'];

  @Input('contributors')
  public contributors;

  constructor() { super(); }

  ngOnInit() {
    if (Constants.DEBUG) {
      this.subscriptions.add(
        this.contributors.committersSubject.subscribe(elements => {
          if (elements !== null) {
            console.groupCollapsed('Contributors');
            elements.forEach(element => console.log  (element.fullname));
            console.groupEnd();
          }
        }));
    }
  }

  /**
   * Return the CSS class corresponding to the active vs inactive status of a developer.
   */
  public class_active_inactive(active: boolean) {
    return active ? 'contributor_active' : 'contributor_inactive';
  }


  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }
}
