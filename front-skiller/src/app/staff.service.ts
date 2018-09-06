import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Collaborator} from './data/collaborator';
import {Project} from './data/project';
import {StaffDTO} from './data/external/staffDTO';

import {MOCK_COLLABORATORS} from './mock/mock-collaborators';

import {Constants} from './constants';
import {Experience} from './data/Experience';
import {Observable, of} from 'rxjs';

import {InternalService} from './internal-service';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class StaffService extends InternalService {

  private collaboratorUrl = 'http://localhost:8080/staff';  // URL to web api

  constructor(
    private http: HttpClient) {
    super();
  }

  /**
   * Return the global list of ALL collaborators, working for the company.
   */
  getAll(): Observable<Collaborator[]> {
    if (Constants.DEBUG) {
      this.log('Fetching the collaborators');
    }
    return this.http.get<Collaborator[]>(this.collaboratorUrl + '/all');
  }

  /**
   * GET staff member associated to this id. Will throw a 404 if id not found.
   */
  get(id: number): Observable<Collaborator> {
    const url = this.collaboratorUrl + '/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the collaborator ' + id + ' on the address ' + url);
    }
    return this.http.get<Collaborator>(url);
  }

  /**
   * POST: update or add a new collaborator to the server
   */
  save(collaborator: Collaborator): Observable<Collaborator> {
    if (Constants.DEBUG) {
      console.log('Saving the collaborator with id ' + collaborator.id);
    }
    return this.http.post<Collaborator>(this.collaboratorUrl + '/save', collaborator, httpOptions);
  }

  /**
   * DELETE delete a staff member from the server
   */
  delete(collaborater: Collaborator | number): Observable<Collaborator> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.id;
    const url = `${this.collaboratorUrl}/${id}`;

    return this.http.delete<Collaborator>(url, httpOptions).pipe(
      tap(_ => this.log(`deleted collaborator id=${id}`)),
      catchError(this.handleError<Collaborator>('deleteCollaborater'))
    );
  }

  /**
   * POST: Add the contribution of a staff member into a project defined by its name
   */
  addProject(idStaff: number, projectName: string): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Adding the collaborator with id : ' + idStaff + ' into the project ' + projectName);
    }
    const body = {idStaff: idStaff, newProjectName: projectName};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/project/save', body, httpOptions);
  }

  /**
   * POST: Change the contribution of a staff member into a project defined by its name.
   */
  changeProject(idStaff: number, formerProjectName: string, newProjectName: string): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Adding the collaborator with id : ' + idStaff + ' into the project ' + newProjectName);
    }
    const body = {idStaff: idStaff, newProjectName: newProjectName, formerProjectName: formerProjectName};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/project/save', body, httpOptions);
  }

  /**
   * POST: Unregister the contribution of a staff member into a project.
   */
  removeFromProject(idStaff: number, idProject: number): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Removing the collaborator with id : ' + idStaff + ' from project with id ' + idProject);
    }
    const body = {idStaff: idStaff, idProject: idProject};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/project/del', body, httpOptions);
  }

  /**
  * Load the projects associated with the staff member identified by this id. 
  */
  loadProjects(idStaff: number): Observable<Project[]> {
    return this.http.get<Project[]>(this.collaboratorUrl + '/projects/' + idStaff);
  }

  /**
  * Load the experience of the staff member identified by this id.
  */
  loadExperiences(idStaff: number): Observable<Experience[]> {
    return this.http.get<Experience[]>(this.collaboratorUrl + '/experiences/' + idStaff);
  }

  /**
   * POST: Add an asset to a staff member defined by its name
   */
  addExperience(idStaff: number, skillTitle: string, level: number): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Adding the skill  ' + skillTitle + ' for the staff member whom id is ' + idStaff);
    }
    const body = {idStaff: idStaff, newSkillTitle: skillTitle, level: level};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/skill/save', body, httpOptions);
  }
}
