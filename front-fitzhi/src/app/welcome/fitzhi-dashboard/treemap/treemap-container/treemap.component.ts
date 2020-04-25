import { Component, OnInit, ViewChild } from '@angular/core';
import { TreemapChartComponent } from '../treemap-chart/treemap-chart.component';
import { ProjectService } from 'src/app/service/project.service';

@Component({
	selector: 'app-treemap',
	templateUrl: './treemap.component.html',
	styleUrls: ['./treemap.component.css']
})
export class TreemapComponent implements OnInit {

	@ViewChild (TreemapChartComponent, {static: false}) treemapChartComponent: TreemapChartComponent;

	constructor(private projectService: ProjectService) { }

	ngOnInit() {
	}

}
