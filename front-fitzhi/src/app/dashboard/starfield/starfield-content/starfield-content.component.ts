import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from 'src/app/base/base-directive.directive';
import { Skill } from 'src/app/data/skill';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Star } from '../data/star';
import { StarfieldService } from '../service/starfield.service';

@Component({
	selector: 'app-starfield-content',
	templateUrl: './starfield-content.component.html',
	styleUrls: ['./starfield-content.component.css']
})
export class StarfieldContentComponent extends BaseDirective implements OnInit, OnDestroy {

	/**
	 * Skill corresponding to the star highlighted by the mouse.
	 */
	public activeSkill: Skill = undefined;

	/**
	 * Star highlighted by the mouse.
	 */
	public activeStar: Star = undefined;

	/**
	 * Index in the series of the star highlighted by the mouse.
	 */
	public indexHighlighted = -1;

	/**
	 * This boolean hides, or shows the detail popup of an highlighted skill.
	 * It's used in a '*ngIf'.
	 */
	public skillPopupDisplay = false;

	constructor(
		public starfieldService: StarfieldService,
		public skillService: SkillService,
		private cdr: ChangeDetectorRef ) {
		super();
	}

	ngOnInit(): void {
		this.subscriptions.add(
			this.starfieldService.constellations$.subscribe({
				next: constellations => this.starfieldService.assembleTheStars(constellations)
			})
		);
	}

	/**
	 * process the CSS style of the DIV node containing a star.
	 * @param star the star whose DIV style has to be defined.
	 * @returns the style of the DIV containing the star
	 */
	style(star: Star) {
		return  ((this.activeSkill) && (star.idSkill === this.activeSkill.id)) ?
			`background-color: lightGrey; color: ${star.color}` :
			`background-color: ${star.backgroundColor}; color: ${star.color}`;
	}

	/**
	 * This method is invoked when a star capture the **ENTRY** of the mouse.
	 * @param star the given star
	 * @param index of the star in the starfield
	 */
	public mouseEnter(star: Star, index: number) {
		this.activeSkill = this.skillService.allSkills.find(sk => sk.id === star.idSkill);
		this.activeStar = star;
		this.skillPopupDisplay = true;
		this.indexHighlighted = index;
		// We force to refresh here, because the result of the function style(...) will change
		// due to the current skill highlighted.
		this.cdr.detectChanges();
	}

	/**
	 * This method is invoked when a star capture the **LEAVE** of the mouse.
	 * @param star the given star
	 */
	public mouseLeave() {
		this.activeSkill = undefined;
		this.activeStar = undefined;
		this.indexHighlighted = -1;
		this.skillPopupDisplay = false;
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
