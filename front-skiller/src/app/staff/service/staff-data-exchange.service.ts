import {Collaborator} from '../../data/collaborator';
import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StaffDataExchangeService {

  private collaboratorSource = new BehaviorSubject(new Collaborator());

  collaboratorObserver = this.collaboratorSource.asObservable();

  constructor() {}

  /**
   * A new collaborator has been loaded and data has to be shared.
   */
  changeCollaborator(collaborator: Collaborator) {
    this.collaboratorSource.next(collaborator);
  }

}
