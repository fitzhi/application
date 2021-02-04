import { MatTableDataSource } from '@angular/material/table';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';

export class ProjectsDataSource extends MatTableDataSource<Project> {

	/**
	 * An array containing the filtered projects
	 *
	 * **For some Angular curious reasons, we need to preserve a local array containing the ghosts,
	 * instead of using directly the internal data property of MatTableDataSource,
	 * IN ORDER TO have a workable PAGINATOR behavior with a DYNAMIC data.**
	 */
	public projects: Project[];

	/**
     * @param project current project
     * @param projects list of unregistered contributors.
     */
	constructor(projects: Project[]) {
		super();
		this.projects = projects;
		this.update(projects);
		if (traceOn()) {
			console.groupCollapsed	(this.projects.length + ' projects to populate the table');
			this.projects.forEach(p => {
				console.log (p.id + ' ' + p.name);
			});
			console.groupEnd();
		}
	}
	/**
	 * Update the datasource with new data.
	 * @param projects the new projects to be displayed.
	 */
	update(projects: Project[]) {
		this.data = projects;
	}

}
