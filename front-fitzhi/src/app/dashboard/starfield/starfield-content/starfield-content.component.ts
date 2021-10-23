import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Star } from '../data/star';
import { StarfieldService } from '../service/starfield.service';

@Component({
	selector: 'app-starfield-content',
	templateUrl: './starfield-content.component.html',
	styleUrls: ['./starfield-content.component.css']
})
export class StarfieldContentComponent extends BaseDirective implements OnInit, OnDestroy {

	public idSkillHighlighted = -1;

	constructor(
		public starfieldService: StarfieldService,
		private cdr: ChangeDetectorRef ) { 
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
	 * process the CSS style of the DIV node containing a star.
	 * @param star the star whose DIV style has to be defined.
	 * @returns the style of the DIV containing the star
	 */
	style(star: Star) {
		return  (star.idSkill === this.idSkillHighlighted) ?
			`background-color: lightGrey; color: ${star.color}` :
			`background-color: ${star.backgroundColor}; color: ${star.color}`;
	}
	
	/**
	 * This method is invoked when a star capture the **ENTRY** of the mouse.
	 * @param star the given star
	 */
	public mouseEnter(star: Star) {
		this.idSkillHighlighted = star.idSkill;
		// We force to refresh here, because the result of the function style(...) will change
		// due to the current skill highlighted. 
		this.cdr.detectChanges();
	}

	/**
	 * This method is invoked when a star capture the **LEAVE** of the mouse.
	 * @param star the given star
	 */
	public mouseLeave(star: Star) {
		this.idSkillHighlighted = -1;
		// We force to refresh here, because the result of the function style(...) will change
		// due to the current skill highlighted. 
		this.cdr.detectChanges();
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
