import { Component, OnInit, ViewChild } from '@angular/core';
import { TreemapChartComponent } from '../treemap-chart/treemap-chart.component';
import { ProjectService } from 'src/app/service/project/project.service';
import { TreemapHeaderComponent } from '../treemap-header/treemap-header.component';

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
