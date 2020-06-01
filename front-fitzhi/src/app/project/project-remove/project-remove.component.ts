import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-project-remove',
	templateUrl: './project-remove.component.html',
	styleUrls: ['./project-remove.component.css']
})
export class ProjectRemoveComponent implements OnInit {

	/**
	 * This component, hosted in a tab pane, is using this emitter to inform its parent to change the active pane.
	 */
	@Output() tabActivationEmitter = new EventEmitter<number>();

	constructor(private projectService: ProjectService) { }

	ngOnInit() {
	}

	/**
	 * The end-
	 */
	public removeProject() {

	}
}
