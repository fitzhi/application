import { Constants } from './constants';
import {Injectable} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {Subject} from "rxjs/Subject";

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

  setForm(form: Number) {
    /**
    * Fire the event. Has to be at the end of the method.
    */
    this.actualFormOnStage.next(form);
  }

}

