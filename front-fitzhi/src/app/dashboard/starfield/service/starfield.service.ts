import { Injectable } from '@angular/core';
import { trace } from 'console';
import { BehaviorSubject } from 'rxjs';
import { traceOn } from 'src/app/global';
import { Constellation } from '../data/constellation';
import { Star } from '../data/star';

@Injectable({
	providedIn: 'root'
})
export class StarfieldService {

	private constellationsSubject$ = new BehaviorSubject<Constellation[]>([]);

	/**
	 * The constellations of skills to be drawn in the starfield component.
	 */
	public constellations$ = this.constellationsSubject$.asObservable();


	private starsSubject$ = new BehaviorSubject<Star[]>([]);

	/**
	 * The serie of stars (<span>&#x2605;</span>) to be assembled in the catterpilar .
	 */
	public stars$ = this.starsSubject$.asObservable();

	constructor() { }

	/**
	 * Emit the skills constellation to be drawn in the starfield component.
	 * @param constellations array of constellations
	 */
	public broadcastConstellations(constellations: Constellation[]) {
		this.constellationsSubject$.next(constellations);
	}

	/**
	 * Emit the skills constellation to be drawn in the starfield component.
	 * @param stars array of stars
	 */
	 public broadcastStars(stars: Star[]) {
		this.starsSubject$.next(stars);
	}

	/**
	 * Assemble the array of constellations into one **unique** serie of <span>&#x2605;</span>.
	 * *We aggregate the constallations into one single serie of <span>&#x2605;</span> 
	 * in order to be able to wrap a serie in the middle of a skill.* 
	 * @param stars the array of skills data
	 */
	public assembleTheStars(constellations: Constellation[]) {

		if (traceOn()) {
			console.log ('%d constellations to be assembled.', constellations.length);
		}
		const stars = [];
		constellations.forEach(constellation => {
			for (let i = 0; i < constellation.count; i++) {
				stars.push(new Star(constellation.idSkill, constellation.color, constellation.backgroundColor));
			}
		});
		if (traceOn()) {
			console.log ('%d stars have been assembled.', stars.length);
		}
		this.broadcastStars(stars);
	}
}
