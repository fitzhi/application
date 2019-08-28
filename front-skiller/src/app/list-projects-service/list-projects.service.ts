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
	private theProjects: Project[] = [];

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
		this.theProjects.push(...this.projectService.allProjects.filter(testCriteria));
	}

	/**
	 * Cleanup the list of projects formerly loaded on the browser.
	 */
	cleanUpProjects() {
		if (Constants.DEBUG) {
			if (this.theProjects == null) {
				console.log('INTERNAL ERROR : collection theProjects SHOULD NOT BE NULL, dude !');
			} else {
				console.log('Cleaning up the projects collection containing ' + this.theProjects.length + ' records');
			}
		}
		this.theProjects.length = 0;
	}

	/**
	* Return the list of projects.
	*/
	getProjects(): Project[] {
		return this.theProjects;
	}

	/**
	 * Return the project associated with this id in cache first, anf if not found, direct on the server
	 */
	getProject(id: number): Observable<Project> {

		let foundProject: Project = null;
		foundProject = this.theProjects.find(project => project.id === id);

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
