import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';

@Component({
	selector: 'app-treemap-skills',
	templateUrl: './treemap-skills.component.html',
	styleUrls: ['./treemap-skills.component.css']
})
export class TreemapComponent implements OnInit {

	constructor(public projectService: ProjectService) { }

	ngOnInit() {
	}

}
