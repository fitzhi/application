import {Injectable} from '@angular/core';
import {Project} from '../data/project';
import {ProjectDTO} from '../data/external/projectDTO';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs';
import {InternalService} from '../internal-service';

import {Constants} from '../constants';
import { Skill } from '../data/skill';
import { ContributorsDTO } from '../data/external/contributorsDTO';
import { PseudoList } from '../data/PseudoList';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class ProjectService extends InternalService {

  private projectUrl = 'http://localhost:8080/project';  // URL to web api

  constructor(private httpClient: HttpClient) { super(); }

  /**
 * Return the global list of ALL collaborators, working for the company.
 */
  getAll(): Observable<Project[]> {
    if (Constants.DEBUG) {
      this.log('Fetching the projects on URL ' + this.projectUrl + '/all');
    }
    return this.httpClient.get<Project[]>(this.projectUrl + '/all');
  }

  /**
  * Save the project.
  */
  save(project: Project): Observable<Project> {
    if (Constants.DEBUG) {
      console.log((typeof project.id !== 'undefined') ? 'Saving ' : 'Adding' + ' project ' + project.name);
    }
    return this.httpClient.post<Project>(this.projectUrl + '/save', project, httpOptions);
  }

  /**
  * Add a skill to a project.
  */
  addSkill(idProject: number, skillTitle: string): Observable<ProjectDTO> {
    if (Constants.DEBUG) {
      console.log('Adding the skill  ' + skillTitle + ' for the project whom id is ' + idProject);
    }
    const body = {idProject: idProject, newSkillTitle: skillTitle};
    return this.httpClient.post<ProjectDTO>(this.projectUrl + '/skills/save', body, httpOptions);
  }

  /**
  * Change the skill inside a project.
  */
  changeSkill(idProject: number, formerSkillTitle: string, newSkillTitle: string): Observable<ProjectDTO> {
    if (Constants.DEBUG) {
      console.log('Changing the skill  ' + formerSkillTitle + ' to ' + newSkillTitle + ' for the project whom id is ' + idProject);
    }
    const body = {idProject: idProject, formerSkillTitle: formerSkillTitle, newSkillTitle: newSkillTitle};
    return this.httpClient.post<ProjectDTO>(this.projectUrl + '/skills/save', body, httpOptions);
  }

  /**
   * POST: Remove a skill from project skills list.
   */
  removeSkill(idProject: number, idSkill: number): Observable<ProjectDTO> {
    if (Constants.DEBUG) {
      console.log('Remove a the skill with ID ' +  idSkill + ' from the project with ID ' + idProject);
    }
    const body = {idProject: idProject, idSkill: idSkill};
    return this.httpClient.post<ProjectDTO>(this.projectUrl + '/skills/del', body, httpOptions);
  }

 /**
  * Load the projects associated with the staff member identified by this id.
  */
  loadSkills(idProject: number): Observable<Skill[]> {
    return this.httpClient.get<Skill[]>(this.projectUrl + '/skills/' + idProject);
  }

  /**
   * GET the project associated to this id from the back-end od skiller. Will throw a 404 if this id is not found.
   */
  get(id: number): Observable<Project> {
    const url = this.projectUrl + '/id/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the project ' + id + ' on the address ' + url);
    }
    return this.httpClient.get<Project>(url);
  }

  /**
   * GET the project associated to the passed name, if any, from the back-end skiller.
   * Will throw a 404 if this name is not retrieved.
   */
  lookup(projectName: string): Observable<Project> {
    const url = this.projectUrl + '/name/' + projectName;
    if (Constants.DEBUG) {
      console.log('Fetching the project name ' + projectName + ' on the address ' + url);
    }
    return this.httpClient.get<Project>(url);
  }

  /**
   * Retrieve the Sunburst data, figuring the activity on the source code regarding the passed project.
   */
  retrieveSuburstData(idProject: number): Observable<any> {
    if (Constants.DEBUG) {
      console.log('Retrieving the Sunburst data for the project ' + idProject);
    }
    const body = {idProject: idProject};
    return this.httpClient.post<any>(this.projectUrl + '/sunburst', body, httpOptions);
  }

  /**
   * Retrieve the contributors for a given project
   * @param idProject project identifier
   */
  contributors(idProject: number): Observable<ContributorsDTO> {
    const url = this.projectUrl + '/contributors/' + idProject;
    if (Constants.DEBUG) {
      console.log('Retrieve the contributors for the project identifier ' + idProject);
    }
    return this.httpClient.get<ContributorsDTO>(url);
  }

  /**
   * Save the ghosts list with their connected data;
   */
  saveGhosts(pseudoList: PseudoList): Observable<PseudoList> {
    if (Constants.DEBUG) {
      console.log('Saving data with for the project ' + pseudoList.idProject);
    }
    return this.httpClient.post<any>(this.projectUrl + '/api-ghosts', pseudoList, httpOptions);
  }

  /**
   * Save the ghosts list with their connected data;
   */
  resetDashboard(id: number): Observable<string> {
    const url = this.projectUrl + '/resetDashboard/' + id;
    if (Constants.DEBUG) {
      console.log('Reset of the dashboard data on URL ' + url);
    }
    return this.httpClient.get<string>(url, httpOptions);
  }
}
