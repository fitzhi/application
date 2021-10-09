import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { FilteredProject } from './filtered-project';

export class FilteredProjectsDataSource extends MatTableDataSource<FilteredProject> {

	constructor(projects: FilteredProject[]) {
		super(projects);
	}
/*
	public updateProject(project: FilteredProject) {
		const fp = this.data.find(item => item.id === project.id)
		if (!fp) {
			throw new Error('Application is in an incomplete state');
		}
		fp.name = project.name;
		fp.selected = project.selected;
	}
*/
}
