import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { ProjectService } from 'src/app/service/project/project.service';
import { FilteredProject } from './filtered-project';
import { FilteredProjectsDataSource } from './filtered-projects-data-source';

@Component({
	selector: 'app-table-projects-filter',
	templateUrl: './table-projects-filter.component.html',
	styleUrls: ['./table-projects-filter.component.css']
})
export class TableProjectsFilterComponent extends BaseDirective implements OnInit, OnDestroy {

	displayedColumns: String[] = ['selected', 'name'];

	/**
	 * The undeclared contributors in the repository.
	 */
	public dataSource: FilteredProjectsDataSource;

	/**
	 * The table
	 */
	 @ViewChild('table') table: MatTable<FilteredProject>;

	constructor(private projectService: ProjectService) { 
		super();
	}

	ngOnInit(): void {
		this.subscriptions.add(
			this.projectService.allProjectsIsLoaded$.subscribe ({
				next: loadAndOk => {
					if (loadAndOk) {
						const projects = [];
						projects.push(new FilteredProject(true, -1, 'All projects'));
						this.projectService.allProjects
							.map(prj => new FilteredProject(true, prj.id, prj.name))
							.forEach(fp => projects.push(fp));
						this.dataSource = new FilteredProjectsDataSource(projects);
					}
				}
			})
		);
	}

	flipSelection(project: FilteredProject) {
//		console.log (project);
//		console.log (this.dataSource.data);
		this.table.renderRows();
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
