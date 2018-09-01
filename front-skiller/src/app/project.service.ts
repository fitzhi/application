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
  save(project: Project): Observable<Project> {
    if (Constants.DEBUG) {
      console.log((typeof project.id !== 'undefined') ? 'Saving ' : 'Adding' + ' project ' + project.name);
    }
    return this.httpClient.post<Project>(this.projectUrl + '/save', project, httpOptions);
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
   * GET the project associated to the passed name, if any, from the back-end skiller. Will throw a 404 if this name is not retrieved.
   */
  lookup(projectName: string): Observable<Project> {
    const url = this.projectUrl + '/name/' + projectName;
    if (Constants.DEBUG) {
      console.log('Fetching the project name ' + projectName + ' on the address ' + url);
    }
    return this.httpClient.get<Project>(url);
  }
}
