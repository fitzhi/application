import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Skill} from './data/skill';
import {Constants} from './constants';
import {MOCK_COLLABORATORS} from './mock/mock-collaborators';
import { Subject, Observable, of } from 'rxjs';
import { catchError, map, tap, filter } from 'rxjs/operators';

import {CollaboratorService} from './collaborator.service';
import {SkillService} from './skill.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * List of collaborators corresponding to the search criteria.
   */
  private static theStaff: Collaborator[] = [];

  /**
   * List of skills corresponding to the search criteria.
   */
  private static theSkill: Skill[] = [];


  /**
   * Current collaborator's identifier previewed on the fom.
   */
  private emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  /**
   * Construction.
   */
  constructor(
    private collaboratorService: CollaboratorService,
    private skillService: SkillService) {
  }

  /**
	* Reload the collaborators for the passed criteria.
	*/
  reloadCollaborators(myCriteria: string) {

    function testCriteria (collab, index, array) {
      return      (collab.firstName.toLowerCase().indexOf(myCriteria) > -1)
            ||    (collab.lastName.toLowerCase().indexOf(myCriteria) > -1);
    }

    this.cleanUpCollaborators();
    this.collaboratorService.getAll().
      subscribe ( (staff: Collaborator[]) =>
          DataService.theStaff.push(...staff.filter( testCriteria)),
          error => console.log (error),
          () => { if (Constants.DEBUG) {
                      console.log('the staff collection is containing now ' + DataService.theStaff.length + ' records');
                  }
                }
          );
  }

  /**
   * Cleanup the list of collaborators involved in our service center.
   */
  cleanUpCollaborators() {
    if (Constants.DEBUG) {
      if (DataService.theStaff == null) {
        console.log('INTERNAL ERROR : collection theStaff SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the staff collection containing ' + DataService.theStaff.length + ' records');
      }
    }
    DataService.theStaff.length = 0;
  }

  /**
   * Return the collaborator associated with this id.
   */
  getCollaborator(id: number): Observable<Collaborator> {

    let foundCollab: Collaborator = null;
    foundCollab = DataService.theStaff.find(collab => collab.id === id);

    if (typeof foundCollab !== 'undefined') {
      this.emitActualCollaboratorDisplay.next(id);
      // We create an observable for an element of the cache in order to be consistent with the direct reading.
      return of(foundCollab);
    } else {
      // The collaborator's id is not, or no more, available in the cache
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.collaboratorService.get(id).pipe(tap(
        (collab: Collaborator) => {
           if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            console.log('Collaborator found : ' + collab.firstName + ' ' + collab.lastName);
          }
        }));
   }
  }
      /*
          if (typeof foundCollab !== 'undefined') {
            this.emitActualCollaboratorDisplay.next(id);
          } else {
            this.emitActualCollaboratorDisplay.next(undefined);
          }

        },
        error => console.log (error),
        () => {

                console.log('complete');
                if (typeof foundCollab !== 'undefined') {
                  console.log ('No collaborator found for the id ' + id);
                }
              }
      );
       */


  /**
   * Return the NEXT collaborator's id associated with this id in the staff list.
   */
  nextCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (Constants.DEBUG) {
      console.log ('Current index : ' + index);
      console.log ('Staff size : ' + DataService.theStaff.length);
    }
    if (index < DataService.theStaff.length - 1) {
      return DataService.theStaff[index + 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the PREVIOUS collaborator's id associated with this id in the staff list.
   */
  previousCollaboratorId(id: number): number {
    const index = DataService.theStaff.findIndex(collab => collab.id === id);
    if (index > 0) {
      return DataService.theStaff[index - 1].id;
    } else {
      return undefined;
    }
  }

  /**
   * Return the list of collaborators.
   */
  getStaff(): Collaborator[] {
    return DataService.theStaff;
  }

  /**
   * Saving a new or an updated collaborator
   */
  saveCollaborator (collaborator: Collaborator): Observable<Collaborator> {
    return this.collaboratorService.save (collaborator);
  }

  /**
   * Saving a new or an updated skill
   */
  saveSkill (skill: Skill) {
    this.skillService.save (skill);
  }
}
