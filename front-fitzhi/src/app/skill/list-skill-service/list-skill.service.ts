import {Constants} from '../../constants';
import {Skill} from '../../data/skill';
import {SkillService} from '../service/skill.service';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {tap} from 'rxjs/operators';
import { traceOn } from '../../global';

@Injectable({
	providedIn: 'root'
})
export class ListSkillService {

	/**
	 * List of skills corresponding to the search criteria.
	 */
	private theSkills: Skill[] = [];

	constructor(private skillService: SkillService) {}

	/**
	 * Return the list of staff members.
	 */
	getSkills(): Skill[] {
		return this.theSkills;
	}

	/**
	* Reload the collaborators for the passed criteria.
	*/
	reloadSkills(myCriteria: string) {

		function testCriteria(skill, index, array) {
			return (myCriteria == null) ? true : (skill.title.toLowerCase().indexOf(myCriteria.toLowerCase()) > -1);
		}

		this.cleanUpSkills();
		this.theSkills.push(...this.skillService.allSkills.filter(testCriteria));
	}

	/**
	 * Cleanup the list of skills involved in our service center.
	 */
	cleanUpSkills() {
		if (traceOn()) {
			if (this.theSkills == null) {
				console.log('INTERNAL ERROR : collection theSkill SHOULD NOT BE NULL, dude !');
			} else {
				console.log('Cleaning up the skill collection containing ' + this.theSkills.length + ' records');
			}
		}
		this.theSkills.length = 0;
	}

	/**
	 * Return the skill associated with this id.
	 */
	getSkill$(id: number): Observable<Skill> {

		let foundSkill: Skill = null;
		foundSkill = this.theSkills.find(skill => skill.id === id);

		if (foundSkill) {
			// We create an observable for an element of the cache in order to be consistent with the HTTP Get load.
			return of(foundSkill);
		} 

		// The skill's id is not, or no more, available in the cache
		// We try a direct access
		if (traceOn()) {
			console.log('Direct access for : ' + id);
		}
		return this.skillService.get(id).pipe(tap(
			(skill: Skill) => {
				if (traceOn()) {
					console.log('Direct access for : ' + id);
					if (skill) {
						console.log('Skill found : ' + skill.title);
					} else {
						console.log('No skill found for id ' + id);
					}
				}
			}));
	}
}
