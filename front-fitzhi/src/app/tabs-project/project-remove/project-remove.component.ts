import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project/project.service';
import { ListProjectsService } from '../list-project/list-projects-service/list-projects.service';

@Component({
	selector: 'app-project-remove',
	templateUrl: './project-remove.component.html',
	styleUrls: ['./project-remove.component.css']
})
export class ProjectRemoveComponent implements OnInit {

	constructor(
		private projectService: ProjectService,
		private listProjectsService: ListProjectsService) { }

	ngOnInit() {
	}

	/**
	 * This function is executed when the user clicks on the button "Remove"
	 */
	public removeProject() {

		this.projectService.removeApiProject$().subscribe({
			next: b => {
				if (b) {
					if (traceOn()) {
						console.log ('The project has been sucessfully removed');
					}
					this.listProjectsService.reload();
				}
			}
		})
	}
}
