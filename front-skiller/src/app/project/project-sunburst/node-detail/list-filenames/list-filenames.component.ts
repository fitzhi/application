import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Constants } from '../../../../constants';
import { BaseComponent } from '../../../../base/base.component';

@Component({
  selector: 'app-list-filenames',
  templateUrl: './list-filenames.component.html',
  styleUrls: ['./list-filenames.component.css']
})
export class ListFilenamesComponent extends BaseComponent implements OnInit, OnDestroy {

  public tblColumns: string[] = ['filename', 'lastCommit'];

  @Input('filenames')
  public filenames;

  constructor() { super(); }

  ngOnInit() {
    if (Constants.DEBUG) {
      this.subscriptions.add(
        this.filenames.filenamesSubject.subscribe(elements => {
          if (elements !== null) {
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
