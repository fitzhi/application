import {Injectable} from '@angular/core';
import {Project} from './data/project';
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
export class ProjectService extends InternalService {

  private projectUrl = 'http://localhost:8080/project';  // URL to web api

  constructor(private httpClient: HttpClient) {super();}

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
  save(project: Project) {
    if (Constants.DEBUG) {
      console.log('Saving project for id ' + project.id);
    }
    if (project.id == null) {
      this.add(project);
    }
  }

  /** POST: add a new project to the server */
  add(newProject: Project): Observable<Project> {
    return this.httpClient.post<Project>(this.projectUrl, newProject, httpOptions).pipe(
      tap((project: Project) =>
        this.log(`added project w/ id=${project.id}`)),
      catchError(this.handleError<Project>('addProject'))
    );
  }

  /**
   * GET the project associated to this id from the backend projecter. Will throw a 404 if this id is not found.
   */
  get(id: number): Observable<Project> {
    const url = this.projectUrl + '/' + id;
    if (Constants.DEBUG) {
      console.log('Fetching the project ' + id + ' on the address ' + url);
    }
    return this.httpClient.get<Project>(url);
  }

}
