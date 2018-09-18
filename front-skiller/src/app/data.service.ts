import {Injectable} from '@angular/core';
import {Collaborator} from './data/collaborator';
import {Skill} from './data/skill';
import {Project} from './data/project';
import {Constants} from './constants';
import {MOCK_COLLABORATORS} from './mock/mock-collaborators';
import {Subject, Observable, of} from 'rxjs';
import {catchError, map, tap, filter} from 'rxjs/operators';

import {StaffService} from './staff.service';
import {SkillService} from './skill.service';
import {ProjectService} from './project.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  /**
   * List of projects corresponding to the search criteria.
   */
  private static theProjects: Project[] = [];

  /**
   * Current collaborator's identifier previewed on the form.
   */
  public emitActualCollaboratorDisplay = new Subject<number>();

  /**
   * Observable associated with the current collaborator.
   */
  newCollaboratorDisplayEmitted$ = this.emitActualCollaboratorDisplay.asObservable();

  /**
   * Construction.
   */
  constructor(
    private collaboratorService: StaffService,
    private skillService: SkillService,
    private projectService: ProjectService) {
  }



  /**
  * Reload the projects for the passed criteria.
  */
  reloadProjects(myCriteria: string) {

    function testCriteria(project, index, array) {
      return (myCriteria == null) ?
        true : (project.name.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
    }

    this.cleanUpProjects();
    this.projectService.getAll().
      subscribe((projects: Project[]) =>
        DataService.theProjects.push(...projects.filter(testCriteria)),
        error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the projects collection is containing now ' + DataService.theProjects.length + ' records');
        }
      }
      );
  }

  /**
   * Cleanup the list of projects formerly loaded on the browser.
   */
  cleanUpProjects() {
    if (Constants.DEBUG) {
      if (DataService.theProjects == null) {
        console.log('INTERNAL ERROR : collection theProjects SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the projects collection containing ' + DataService.theProjects.length + ' records');
      }
    }
    DataService.theProjects.length = 0;
  }


  /**
  * Return the list of projects.
  */
  getProjects(): Project[] {
    return DataService.theProjects;
  }

  /**
   * Saving a new or an updated project.
   */
  saveProject(project: Project): Observable<Project> {
    return this.projectService.save (project);
  }

  /**
   * Return the project associated with this id in cache first, anf if not found, direct on the server
   */
  getProject(id: number): Observable<Project> {

    let foundProject: Project = null;
    foundProject = DataService.theProjects.find(project => project.id === id);

    if (typeof foundProject !== 'undefined') {
      return of(foundProject);
    } else {
      // The collaborator's id is not, or no more, available in the cache.
      // We try a direct access
      if (Constants.DEBUG) {
        console.log('Direct access for : ' + id);
      }
      return this.projectService.get(id).pipe(tap(
        (project: Project) => {
          if (Constants.DEBUG) {
            console.log('Direct access for : ' + id);
            if (typeof project !== 'undefined') {
              console.log('Project found : ' + project.name);
            } else {
              console.log('No project found for id ' + id);
            }
          }
        }));
    }
  }

  /**
   * Return a project, if any, for this name
   */
  	lookupProject(projectName: string): Observable<Project> {
  		return this.projectService.lookup(projectName);
	}
}

