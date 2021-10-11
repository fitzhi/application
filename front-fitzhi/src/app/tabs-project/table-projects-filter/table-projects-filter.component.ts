import { Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { TreemapProjectsService } from 'src/app/dashboard/treemap-projects/treemap-projects-service/treemap-projects.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { FilteredProject } from './filtered-project';
import { FilteredProjectsDataSource } from './filtered-projects-data-source';

export enum EventOrigin {
	SELECTED = 0,
	NAME = 1,
}

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

	/**
	 * Origin of the mouse-event passed to the function flipSelection(...).
	 */
	public EventOrigin = EventOrigin;

	/**
	 * ID representing all projects in the list of projects.
	 */
	private ALL_PROJECTS = -1;

	constructor(
			private projectService: ProjectService,
			private treemapProjectsService: TreemapProjectsService) {
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
							.filter(prj => prj.active)
							.filter(prj => this.projectService.hasBeenEvaluated(prj))
							.map(prj => new FilteredProject(true, prj.id, prj.name))
							.forEach(fp => projects.push(fp));
						this.dataSource = new FilteredProjectsDataSource(projects);
					}
				}
			})
		);
	}

	/**
	 * Flip the selection of the given project
	 * @param project the current project
	 * @param origin the widget which emits this event.
	 */
	flipSelection(project: FilteredProject, origin: EventOrigin) {
		project.selected = !project.selected;
		if (project.id === this.ALL_PROJECTS) {
			this.dataSource.data.forEach( p => {
				if (p.id > this.ALL_PROJECTS) {
					p.selected = project.selected;
				}
			});
		} else {
			if (!project.selected) {
				this.dataSource.data[0].selected = false;
			}
		}
		this.treemapProjectsService.informSelectedProjects(
			this.dataSource.data.filter(p => p.selected).map(p => p.id));
		this.table.renderRows();
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
