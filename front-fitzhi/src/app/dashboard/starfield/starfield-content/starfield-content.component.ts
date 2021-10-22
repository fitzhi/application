import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Star } from '../data/star';
import { StarfieldService } from '../service/starfield.service';

@Component({
	selector: 'app-starfield-content',
	templateUrl: './starfield-content.component.html',
	styleUrls: ['./starfield-content.component.css']
})
export class StarfieldContentComponent extends BaseDirective implements OnInit, OnDestroy {

	constructor(public starfieldService: StarfieldService) { 
		super();
	}

	ngOnInit(): void {
		this.subscriptions.add(
			this.starfieldService.constellations$.subscribe({
				next: constellations => this.starfieldService.assembleTheStars(constellations)
			})
		)
	}

	/**
	 * Create a counter used by the DIV containing the skills <span>&#x2605;</span>.
	 * @param count the number of stars to be drawn for a skill 
	 * @returns the returning array
	 */
	counter(count: number) {
		return new Array(count);
	}

	style(star: Star) {
		return `background-color: ${star.backgroundColor}; color: ${star.color}`;
	}
	
	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
