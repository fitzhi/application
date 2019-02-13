import { OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Constants } from '../constants';

export class BaseComponent implements OnDestroy {

  /**
   * Array of subscriptions activated on the child component.
   */
  subscriptions: Subscription = new Subscription();

  dateConstruction: String;

  constructor() {
    const today = new Date();
    this.dateConstruction = today.getHours() + ':' + today.getMinutes() + ':' + today.getSeconds();
  }

  public ngOnDestroy() {
    if (Constants.DEBUG) {
      console.log('[BaseComponent] ngOnDestroy()');
    }
    this.subscriptions.unsubscribe();
  }
}
