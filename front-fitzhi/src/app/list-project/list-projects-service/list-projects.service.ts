import { Constants } from '../../constants';
import {Project} from '../../data/project';
import { ProjectService } from '../../service/project.service';
import {Injectable, OnInit} from '@angular/core';
import {Observable, of, BehaviorSubject, throwError, empty, EMPTY} from 'rxjs';
import {tap} from 'rxjs/operators';
import { MessageService } from '../../message/message.service';
import { traceOn } from 'src/app/global';

@Injectable({
	providedIn: 'root'
})
export class ListProjectsService  {

	public filteredProjects$ = new BehaviorSubject<Project[]>([]);

	constructor(
		private messageService: MessageService,
		private projectService: ProjectService) {}

	/**
	* Filter the projects for the passed criteria.
	* @param myCriteria criteria typed by the end-user
	*/
	reloadProjects(myCriteria: string) {

		// '*' is a wildcard for all projects.
		if (myCriteria === '*') {
			this.filteredProjects$.next(this.projectService.allProjects);
			return;
		}

		const projects: Project[] = [];

		function testCriteria(project, index, array) {
			return (myCriteria == null) ?
				true : (project.name.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
		}
		projects.push(...this.projectService.allProjects.filter(testCriteria));

		/**
		 * We throw the resulting collection.
		 */
		this.filteredProjects$.next(projects);
	}

	/**
	 * Return the project associated with this id in cache first, anf if not found, direct on the server
	 * @param id the project identifier
	 */
	getProject$(id: number): Observable<Project> {

		if (!this.projectService.allProjects) {
			this.messageService.info('Please wait until all projects have been loaded!');
			return EMPTY;
		}
		let foundProject: Project = null;
		foundProject = this.projectService.allProjects.find(project => project.id === id);

		if (foundProject) {
			return of(foundProject);
		} else {
			// The project's id is not, or no more, available in the cache.
			// We try a direct access
			if (traceOn()) {
				console.log('Direct access for : ' + id);
			}
			return this.projectService.get(id).pipe(tap(
				(project: Project) => {
					if (traceOn()) {
						console.log('Direct access for : ' + id);
						if (project) {
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
