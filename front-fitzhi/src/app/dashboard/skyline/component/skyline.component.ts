import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Building, BuildingSelected } from 'rising-skyline';
import { BehaviorSubject } from 'rxjs';
import { traceOn } from 'src/app/global';
import { SkylineService } from '../service/skyline.service';

@Component({
	selector: 'app-skyline',
	templateUrl: './skyline.component.html',
	styleUrls: ['./skyline.component.css']
})
export class SkylineComponent {

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
		public skylineService: SkylineService,
		private router: Router) { }

	/**
	 * This method is invoked when a building is clicked.
	 * @param $event the selected building
	 */
	public onClickBuilding($event: BuildingSelected) {
		if (traceOn()) {
			console.log('Building %s %s clicked', $event.building.id, $event.building.title);
		}
		document.body.style.cursor = 'default';
		this.router.navigate(['/project/' + $event.building.id], {});
	}

	/**
	 * This method is invoked when the mouse is entering a building.
	 * @param $event the selected building
	 */
	public onEnterBuilding($event: BuildingSelected) {
		if (traceOn()) {
			console.log('Entering into the building %s %.', $event.building.id, $event.building.title);
		}
		document.body.style.cursor = 'pointer';
	}

	/**
	 * This method is invoked when the mouse is leaving a building.
	 * @param $event the selected building
	 */
	public onLeaveBuilding($event: BuildingSelected) {
		if (traceOn()) {
			console.log('Leaving the building %s %s.', $event.building.id, $event.building.title);
		}
		document.body.style.cursor = 'default';
	}

}
