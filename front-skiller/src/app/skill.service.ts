import {Injectable} from '@angular/core';
import {Skill} from './data/skill';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Observable, of} from 'rxjs';
import {InternalService} from './internal-service';

import {Constants} from './constants';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class SkillService extends InternalService {

  private skillUrl = 'http://localhost:8080/skill';  // URL to web api

  constructor(private httpClient: HttpClient) {super(); }

  /**
 * Return the global list of ALL collaborators, working for the company.
 */
  getAll(): Observable<Skill[]> {
    if (Constants.DEBUG) {
      this.log('Fetching the skills on URL ' + this.skillUrl + '/all');
    }
    return this.httpClient.get<Skill[]>(this.skillUrl + '/all');
  }

  /**
  * Save the skill
  */
  save(skill: Skill): Observable<Skill> {
    if (Constants.DEBUG) {
      console.log( (typeof skill.id !== 'undefined') ? 'Saving '  : 'Adding' + ' skill ' + skill.title);
    }
    return this.httpClient.post<Skill>(this.skillUrl + '/save', skill, httpOptions);
  }

  /**
   * GET the skill associated to this id from the backend skiller. Will throw a 404 if this id is not found.
   */
  get(id: number): Observable<Skill> {
    const url = this.skillUrl + '/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the skill ' + id + ' on the address ' + url);
    }
    return this.httpClient.get<Skill>(url);
  }

  /**
   * GET the skill associated to the passed name, if any, from the back-end skiller.
   * Will throw a 404 if this name is not retrieved.
   */
  lookup(skillTitle: string): Observable<Skill> {
    const url = this.skillUrl + '/name/' + skillTitle;
    if (Constants.DEBUG) {
      console.log('Fetching the skill title ' + skillTitle + ' on the address ' + url);
    }
    return this.httpClient.get<Skill>(url);
  }


}
