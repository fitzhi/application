import { Component, OnInit } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';

@Component({
	selector: 'app-treemap',
	templateUrl: './treemap.component.html',
	styleUrls: ['./treemap.component.css']
})
export class TreemapComponent implements OnInit {

	constructor(public projectService: ProjectService) { }

	ngOnInit() {
	}

}
