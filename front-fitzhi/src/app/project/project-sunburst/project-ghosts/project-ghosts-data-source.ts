import { Project } from '../../../data/project';
import { Unknown } from '../../../data/unknown';
import { MatTableDataSource } from '@angular/material/table';
import { Constants } from '../../../constants';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { BehaviorSubject } from 'rxjs';

export class ProjectGhostsDataSource extends MatTableDataSource<Unknown> {


	/**
     * @param project current project
     * @param ghosts list of unregistered contributors.
     */
	constructor(ghosts: Unknown[]) {
		super(ghosts);
		if (traceOn()) {
			console.groupCollapsed	(ghosts.length + ' ghosts identified');
			ghosts.forEach(g => {
				console.log (g.pseudo, (g.technical ? 'technical' : g.idStaff));
			});
			console.groupEnd();
		}
	}
	/**
	 * Update the datasource with new data.
	 * @param ghosts the new ghosts to be displayed.
	 */
	update(ghosts: Unknown[]) {
		this.data = ghosts;
	}

	/**
	 * @param pseudo remove the ghost associated to the passed pseudo.
	 */
	removePseudo (pseudo: string) {
		const indexPseudo = this.data.findIndex(ghost => pseudo === ghost.pseudo);
		if (indexPseudo === -1) {
			console.error ('Pseudo ' + pseudo + ' has disappeared from the ghosts list');
		} else {
			if (traceOn()) {
				console.log('the pseudo %s has been removed @ %d.', pseudo, indexPseudo);
			}
			this.data.splice(indexPseudo, 1);
		}
	}

}
