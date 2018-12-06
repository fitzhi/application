import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, tap} from 'rxjs/operators';

import {Collaborator} from './data/collaborator';
import {Project} from './data/project';
import {StaffDTO} from './data/external/staffDTO';

import {Constants} from './constants';
import { DeclaredExperience } from './data/declared-experience';
import {Experience} from './data/experience';
import {Observable} from 'rxjs';

import {InternalService} from './internal-service';

import { saveAs } from 'file-saver';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class StaffService extends InternalService {

  private static peopleCountExperience: Map<string, number> = new Map<string, number>();

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
      console.log('Saving the collaborator with id ' + collaborator.idStaff);
    }
    return this.http.post<Collaborator>(this.collaboratorUrl + '/save', collaborator, httpOptions);
  }

  /**
   * DELETE delete a staff member from the server
   */
  delete(collaborater: Collaborator | number): Observable<Collaborator> {
    const id = typeof collaborater === 'number' ? collaborater : collaborater.idStaff;
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
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/experiences/save', body, httpOptions);
  }

  /**
   * POST: Add the relevant declared experiences (certainly retrieved from the resume)
   */
  addDeclaredExperience(idStaff: number, skills: DeclaredExperience[]): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Adding ' + skills.length + ' experiences to the staff Id  ' + idStaff);
    }
    const body = {idStaff: idStaff, skills: skills};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/api/experiences/resume/save',
      body, httpOptions);
  }
  /**
   * POST: Revoke an experience to a a staff member.
   */
  revokeExperience(idStaff: number, idSkill: number): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Revoking the experence ' +  idSkill + ' from the collaborator application');
    }
    const body = {idStaff: idStaff, idSkill: idSkill};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/experiences/del', body, httpOptions);
  }

  /**
   * POST: Change the experience defined by its title, or its level for a developer.
   */
  changeExperience(idStaff: number, formerSkillTitle: string, newSkillTitle: string, level: number): Observable<StaffDTO> {
    if (Constants.DEBUG) {
      console.log('Change the skill for the collaborator with id : '
        + idStaff + ' from ' + formerSkillTitle + ' to ' + newSkillTitle);
    }
    const body = {idStaff: idStaff, formerSkillTitle: formerSkillTitle, newSkillTitle: newSkillTitle, level: level};
    return this.http.post<StaffDTO>(this.collaboratorUrl + '/experiences/save', body, httpOptions);
  }

  /**
   * GET : Download the application file for the passed staff member.
   */
  downloadApplication(staff: Collaborator) {
    if ((staff.application === null) || (staff.application.length === 0)) {
      return;
    }
    if (Constants.DEBUG) {
      console.log('Download the application file : '
        + staff.application + ' for ' + staff.firstName + ' ' + staff.lastName);
    }

    const headers = new HttpHeaders();
    headers.set ('Accept', 'application/msword');

    this.http.get(this.collaboratorUrl + '/' + staff.idStaff + '/application',
    { headers: headers, responseType: 'blob' })
    .subscribe( data =>  {
      this.saveToFileSystem (data, staff.application, 'application/octet-stream');
    });

  }

  /**
   * Save the application file on the the file system.
   */
  private saveToFileSystem(data, filename, typeOfFile) {
    const blob = new Blob([data], { type: typeOfFile });
    saveAs(blob, filename);
  }

  /**
   * Get the count of staff members aggregated by skill & level (i.e. experience)
   * @param activeOnly : Only active employees count into the aggregation.
   */
  getPeopleCountExperience(): Map<string, number> {
    return StaffService.peopleCountExperience;
  }

  /**
   * Retrieving the sum of staff members aggregated by skill & level (i.e. experience)
   * @param activeOnly : Only active employees count into the aggregation.
   */
  countAll_groupBy_experience(activeOnly: boolean) {
    if (Constants.DEBUG) {
      console.log('countAll_groupBy_experience loading aggegations count from the server');
    }
    this.http.get<any>(this.collaboratorUrl + '/countGroupByExperiences/'
                        + (activeOnly ? '/active' : '/all') )
      .subscribe(
        response  => {
          StaffService.peopleCountExperience.clear();
          Object.entries(response)
            .forEach( entry => {
              let key, value: string;
              key = entry[0] as string;
              value = entry[1] as string;
              StaffService.peopleCountExperience.set(key, parseInt(value, 0));
            }); },
        error => console.log (error),
        () => {
          if (Constants.DEBUG) {
            console.log ('peopleCountExperience is completly loaded');
          }
        }
        );
  }

}
