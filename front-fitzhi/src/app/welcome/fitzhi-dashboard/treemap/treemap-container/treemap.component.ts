import { Component, OnInit, ViewChild } from '@angular/core';
import { TreemapChartComponent } from '../treemap-chart/treemap-chart.component';
import { ProjectService } from 'src/app/service/project.service';
import { TreemapHeaderComponent } from '../treemap-header/treemap-header.component';

@Component({
	selector: 'app-treemap',
	templateUrl: './treemap.component.html',
	styleUrls: ['./treemap.component.css']
})
export class TreemapComponent implements OnInit {

	@ViewChild (TreemapChartComponent, {static: false}) treemapChartComponent: TreemapChartComponent;

	@ViewChild (TreemapHeaderComponent, {static: false}) treemapHeaderComponent: TreemapHeaderComponent;

	constructor(private projectService: ProjectService) { }

	ngOnInit() {
	}

}
