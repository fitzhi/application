import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';

@Component({
	selector: 'app-project-remove',
	templateUrl: './project-remove.component.html',
	styleUrls: ['./project-remove.component.css']
})
export class ProjectRemoveComponent implements OnInit {

	constructor(public projectService: ProjectService) { }

	ngOnInit() {
	}

}
