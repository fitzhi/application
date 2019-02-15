import { Constants } from '../constants';
import {Project} from '../data/project';
import { ProjectService } from '../service/project.service';
import {Injectable} from '@angular/core';
import {Subject, Observable, of} from 'rxjs';
import {catchError, map, tap, filter} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ListProjectsService {

  /**
   * List of projects corresponding to the search criteria.
   */
  private static theProjects: Project[] = [];

  constructor(private projectService: ProjectService) {}

  /**
  * Reload the projects for the passed criteria.
  */
  reloadProjects(myCriteria: string) {

    function testCriteria(project, index, array) {
      return (myCriteria == null) ?
        true : (project.name.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
    }

    this.cleanUpProjects();
    this.projectService.getAll().subscribe(
      (projects: Project[]) =>
        ListProjectsService.theProjects.push(...projects.filter(testCriteria)),
        error => console.log(error),
      () => {
        if (Constants.DEBUG) {
          console.log('the projects collection is containing now ' + ListProjectsService.theProjects.length + ' records');
        }
      });
  }

  /**
   * Cleanup the list of projects formerly loaded on the browser.
   */
  cleanUpProjects() {
    if (Constants.DEBUG) {
      if (ListProjectsService.theProjects == null) {
        console.log('INTERNAL ERROR : collection theProjects SHOULD NOT BE NULL, dude !');
      } else {
        console.log('Cleaning up the projects collection containing ' + ListProjectsService.theProjects.length + ' records');
      }
    }
    ListProjectsService.theProjects.length = 0;
  }

  /**
  * Return the list of projects.
  */
  getProjects(): Project[] {
    return ListProjectsService.theProjects;
  }

  /**
   * Return the project associated with this id in cache first, anf if not found, direct on the server
   */
  getProject(id: number): Observable<Project> {

    let foundProject: Project = null;
    foundProject = ListProjectsService.theProjects.find(project => project.id === id);

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
