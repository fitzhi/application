import { Constants } from './constants';
import {Injectable} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {Subject} from 'rxjs/Subject';

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

  /**
   * Identifier of the select form on stage on the SPA.
   */
  public actualFormOnStage = new BehaviorSubject<Number>(Constants.WELCOME);

  /**
    * Current collaborator's identifier previewed on the form.
    */
  public emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator previewed.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  /**
   * This subject containts the tab selected in the projects Tab Group container
   */
  public tabProjectActivated = new BehaviorSubject<Number>(Constants.PROJECT_TAB_FORM);

  setForm(form: Number) {
    /**
    * Fire the event. Has to be at the end of the method.
    */
    this.actualFormOnStage.next(form);
  }

  /**
   * Fire the event that the tab index has changed.
   */
  setProjectTab(tab: number) {
    this.tabProjectActivated.next(tab);
  }

}

