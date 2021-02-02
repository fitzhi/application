import { Component, Input, OnInit } from '@angular/core';
import { ControlledRisingSkylineService } from 'controlled-rising-skyline';
import { Building } from 'rising-skyline';
import { BehaviorSubject } from 'rxjs';
import { SkylineService } from '../service/skyline.service';

@Component({
	selector: 'app-skyline',
	templateUrl: './skyline.component.html',
	styleUrls: ['./skyline.component.css']
})
export class SkylineComponent implements OnInit {

	/**
	 * The **width** of the skyline component.
	 */
	@Input() width;
	
	/**
	 * The **height** of the skyline component.
	 */
	@Input() height;

	/**
	 * The **margin** of the skyline component.
	 */
	@Input() margin;

	/**
	 * The **speed** of the animation.
	 */
	@Input() speed;

	public risingSkylineHistory$ = new BehaviorSubject<Building[]>([]);

	constructor(
		public skylineService: SkylineService) { }

	ngOnInit(): void {
	}

}
