import {Injectable} from '@angular/core';
import {Skill} from '../data/skill';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {InternalService} from '../internal-service';

import {Constants} from '../constants';
import { ListCriteria } from '../data/listCriteria';
import { BackendSetupService } from './backend-setup/backend-setup.service';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class SkillService extends InternalService {

  /*
   * skills
   */
  public allSkills: Skill[] = [];

  /**
   * skills
   */
  public filteredSkills: Skill[] = [];

  /**
   * Context of search
   */
  criteria: ListCriteria;

  constructor(private httpClient: HttpClient, private backendSetupService: BackendSetupService) {
    super();
    if (Constants.DEBUG && !this.backendSetupService.hasSavedAnUrl()) {
        console.log ('Skills loading is postponed due to the lack of backend URL.');
    }
    if (this.backendSetupService.hasSavedAnUrl()) {
        this.loadSkills();
    }
   }

 /**
  * load the list of ALL collaborators skills, working for the company.
  */
  loadSkills() {

    if (Constants.DEBUG) {
      this.log('Fetching all skills on URL ' + this.backendSetupService.url() + '/skill' + '/all');
    }
    const subSkills = this.httpClient.get<Skill[]>(this.backendSetupService.url() + '/skill' + '/all').subscribe(
      skills => {
        if (Constants.DEBUG) {
          console.groupCollapsed('Skills registered : ');
          skills.forEach(function (skill) {
            console.log (skill.id + ' ' + skill.title);
          });
          console.groupEnd();
        }
        skills.forEach(skill => this.allSkills.push(skill));
      },
      error => console.log (error),
      () => setTimeout(() => {subSkills.unsubscribe(); }, 1000));
  }

  /**
  * Save the skill
  */
  save(skill: Skill): Observable<Skill> {
    if (Constants.DEBUG) {
      console.log( (typeof skill.id !== 'undefined') ? 'Saving '  : 'Adding' + ' skill ' + skill.title);
    }
    return this.httpClient.post<Skill>(this.backendSetupService.url() + '/skill' + '/save', skill, httpOptions);
  }

  /**
   * @returns the title associated to the passed skill identifier
   */
  title(idSkill: number) {
    const found = this.allSkills.find(skill => skill.id === idSkill);
    if (typeof found === 'undefined') {
        return 'ERR : no title for id ' + idSkill;
    } else {
      return found.title;
    }
  }

  /**
   * GET the skill associated to this id from the backend skiller. Will throw a 404 if this id is not found.
   */
  get(id: number): Observable<Skill> {
    const url = this.backendSetupService.url() + '/skill' + '/' + id;
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
    const url = this.backendSetupService.url() + '/name/' + skillTitle;
    if (Constants.DEBUG) {
      console.log('Fetching the skill title ' + skillTitle + ' on the address ' + url);
    }
    return this.httpClient.get<Skill>(url);
  }

  /**
   * @param searchContext Filter the global list on the passed criteria
   */
  filter (criteria: ListCriteria) {
    this.criteria = criteria;
  }

  /**
   * Filter and return the skills corresponding to the current criterias.
   */
  getFilteredSkills(): Skill[] {
    this.filteredSkills = Object.assign([], this.allSkills);
    return this.filteredSkills;
  }
}
