import {Injectable} from '@angular/core';
import {Subject} from "rxjs/Subject";

@Injectable({
  providedIn: 'root'
})
export class CinematicService {

  /**
   * Identifier of the select form on stage on the SPA.
   */
  private emitActualComponentSource = new Subject<Number>();

  /**
   * Observable listening the select form on stage.
   */
  newFormDisplayEmitted$ = this.emitActualComponentSource.asObservable();

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
    this.emitActualComponentSource.next(form);
  }

}

