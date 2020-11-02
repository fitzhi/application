import { Component, OnInit } from '@angular/core';
import { ControlledRisingSkylineService } from 'controlled-rising-skyline';
import { Building } from 'rising-skyline';
import { BehaviorSubject } from 'rxjs';

@Component({
	selector: 'app-skyline',
	templateUrl: './skyline.component.html',
	styleUrls: ['./skyline.component.css']
})
export class SkylineComponent implements OnInit {

	public risingSkylineHistory$ = new BehaviorSubject<Building[]>([]);

	constructor(private controlledRisingSkylineService:  ControlledRisingSkylineService) { }

	ngOnInit(): void {
		this.controlledRisingSkylineService.randomSkylineHistory(this.risingSkylineHistory$);
	}

}
