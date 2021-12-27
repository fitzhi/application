import { MatTableDataSource } from '@angular/material/table';
import { FilteredProject } from './filtered-project';

export class FilteredProjectsDataSource extends MatTableDataSource<FilteredProject> {

	constructor(projects: FilteredProject[]) {
		super(projects);
	}

	public selectedProjects() {
		return this.data.filter(p => p.selected);
	}
}
