import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-project-inactivate',
	templateUrl: './project-inactivate.component.html',
	styleUrls: ['./project-inactivate.component.css']
})
export class ProjectInactivateComponent implements OnInit {

	constructor(public projectService: ProjectService) { }

	ngOnInit() {
	}

}
