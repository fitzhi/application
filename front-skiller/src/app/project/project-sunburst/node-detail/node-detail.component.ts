import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { FilenamesDataSource } from './filenames-data-source';
import { ContributorsDataSource } from './contributors-data-source';
import { BaseComponent } from '../../../base/base.component';

@Component({
  selector: 'app-node-detail',
  templateUrl: './node-detail.component.html',
  styleUrls: ['./node-detail.component.css']
})
export class NodeDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  @Input('filenames')
  public filenames: FilenamesDataSource;

  @Input('contributors')
  public contributors: ContributorsDataSource;

  @Input('location')
  public location;

  public node;

  constructor() {
    super();
  }

  ngOnInit() {
    this.subscriptions.add(
      this.location.subscribe(element => this.node = element));
  }

  /**
   * Calling the base class to unsubscribe all subscriptions.
   */
  ngOnDestroy() {
    super.ngOnDestroy();
  }
}
