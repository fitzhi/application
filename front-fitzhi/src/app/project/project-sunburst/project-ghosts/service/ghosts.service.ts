import { Injectable } from '@angular/core';
import { Collaborator } from 'src/app/data/collaborator';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { traceOn } from 'src/app/global';
import { StaffFormComponent } from 'src/app/tabs-staff/staff-form/staff-form.component';
import { Unknown } from 'src/app/data/unknown';

@Injectable({
	providedIn: 'root'
})
export class GhostsService {

	public ghosts: Unknown[] = [];

	constructor() { }

	/**
	 * Update the array of actual ghosts.
	 * @param ghosts the new list of ghosts
	 */
	updateGhosts(ghosts: Unknown[]) {
		this.ghosts = ghosts;
	}

	/**
	 * This method is reproducing _in the Angular environment_
	 * the method **BasicCommitRepository.extractMatchingUnknownContributors**
	 * from the Fitzhì Java plattform.
	 *
	 * The use case for this  method :
	 *
	 * After the creation of a staff member for a given login, it is possible that some other logins
	 * might be linked to this new staff member. For example, you have created a collaborator linked to the login **frvidal**.
	 * This new staff has **Frédéric** as firtname, and **Vidal** as lastname.
	 * If you have a ghost with **Frederic VIDAL** as login, this ghost should be associated with this collaborator.
	 *
	 *
	 * @param pseudos the array of eligible pseudos which can be linked with the given staff
	 * @param staff the new staff to be evaluated with the remaining collection of logins
	 */
	extractMatchingUnknownContributors(pseudos: string[], staff: Collaborator) {

		const firstName = this.reduceCharacters(staff.firstName);
		const lastName = this.reduceCharacters(staff.lastName);
		const login = this.reduceCharacters(staff.login);

		const matchedLogins = [];

		pseudos.forEach (candidate => {
							const eligible = this.reduceCharacters(candidate);
							if (   (eligible === login)
								||   (eligible === (firstName + ' ' + lastName))
								||   (eligible === (lastName + ' ' + firstName))) {
									matchedLogins.push(candidate);
							}
		});

		return matchedLogins;
	}

	/**
	 * Remove the alternate ghosts who are linked to the given staff.
	 * @param staff the given collaborator
	 */
	removeAlternateGhosts(dataSource: ProjectGhostsDataSource, staff: Collaborator) {
		const pseudos = dataSource.data.map(ghost => ghost.pseudo);
		const matchingPseudos = this.extractMatchingUnknownContributors(pseudos, staff);
		return matchingPseudos;
	}


	/**
	 * Replace the character with accent with its corresponding character without character.
	 * @param str the string to updated.
	 */
	reduceCharacters (str: string): string {

		if (!str) {
			return str;
		}

		const map = {
				' ' : '-|_',
				'a' : 'á|à|ã|â|À|Á|Ã|Â',
				'e' : 'é|è|ê|É|È|Ê',
				'i' : 'í|ì|î|Í|Ì|Î',
				'o' : 'ó|ò|ô|õ|Ó|Ò|Ô|Õ',
				'u' : 'ú|ù|û|ü|Ú|Ù|Û|Ü',
				'c' : 'ç|Ç',
				'n' : 'ñ|Ñ'
		};

		for (const pattern in map) {
				if (map.hasOwnProperty(pattern)) {
					str = str.replace(new RegExp(map[pattern], 'g'), pattern).trim().replace('  ', ' ').toLowerCase();
				}
		}

		return str;
	}

}
