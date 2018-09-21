import {Constants} from '../constants';
import {Skill} from '../data/skill';
import {SkillService} from '../skill.service';
import {Injectable} from '@angular/core';
import {Subject, Observable, of} from 'rxjs';
import {catchError, map, tap, filter} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ListSkillService {

  /**
   * List of skills corresponding to the search criteria.
   */
  private static theSkills: Skill[] = [];

  constructor(private skillService: SkillService) {}
  /**
   * Return the list of staff membersÒ.
   */
  getSkills(): Skill[] {
    return ListSkillService.theSkills;
  }

  /**
  * Reload the collaborators for the passed criteria.
  */
  reloadSkills(myCriteria: string) {

    function testCriteria(skill, index, array) {
      return (myCriteria == null) ? true : (skill.title.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
    }

    this.cleanUpSkills();
    this.skillService.getAll().
      subscribe((skills: Skill[]) =>
        ListSkillService.theSkills.push(...skills.filter(testCriteria)),
      error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the skills collection is containing now ' + ListSkillService.theSkills.length + ' records');
        }
      });
  }

  /**
   * Cleanup the list of skills involved in our service center.
   */
  cleanUpSkills() {
    if (Constants.DEBUG) {
      if (ListSkillService.theSkills == null) {
        console.log('INTERNAL ERROR : collection theSkill SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the skill collection containing ' + ListSkillService.theSkills.length + ' records');
      }
    }
    ListSkillService.theSkills.length = 0;
  }

  /**
   * Return the skill associated with this id.
   */
  getSkill(id: number): Observable<Skill> {

    let foundSkill: Skill = null;
    foundSkill = ListSkillService.theSkills.find(skill => skill.id === id);

    if (typeof foundSkill !== 'undefined') {
      //TODO this.emitActualCollaboratorDisplay.next(id);
      // We create an observable for an element of the cache in order to be consistent with the direct reading.
      return of(foundSkill);
    } else {
      // The collaborator's id is not, or no more, available in the cache
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.skillService.get(id).pipe(tap(
        (skill: Skill) => {
          if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            if (typeof skill !== 'undefined') {
              console.log('Skill found : ' + skill.title);
            } else {
              console.log('No skill found for id ' + id);
            }
          }
        }));
    }
  }
}
