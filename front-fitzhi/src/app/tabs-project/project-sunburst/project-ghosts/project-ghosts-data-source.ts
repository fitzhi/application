import { MatTableDataSource } from '@angular/material/table';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { Unknown } from '../../../data/unknown';

export class ProjectGhostsDataSource extends MatTableDataSource<Unknown> {

	/**
	 * An array containing all ghosts
	 *
	 * **For some Angular curious reasons, we need to preserve a local array containing the ghosts,
	 * instead of using directly the internal data property of MatTableDataSource,
	 * IN ORDER TO have a workable PAGINATOR behavior with a DYNAMIC data.**
	 */
	public ghosts: Unknown[];

	/**
	 * @param ghosts list of unregistered contributors.
	 * @param allStaff _(optional)_ the complete list of staff members, if necessary
	 */
	constructor(ghosts: Unknown[], allStaff?: Collaborator[]) {
		super();
		this.ghosts = ghosts;

		// If the staff list is not given, it's because we do need to fill the related staff at this level
		if (allStaff) {
			this.updateRelatedStaff(ghosts, allStaff);
		}

		this.update(ghosts);
		if (traceOn()) {
			console.groupCollapsed	(this.ghosts.length + ' ghosts identified');
			this.ghosts.forEach(g => {
				console.log (g.pseudo, (g.technical ? 'technical' : g.idStaff));
			});
			console.groupEnd();
		}
	}

	private updateRelatedStaff(ghosts: Unknown[], staff?: Collaborator[]) {
		this.ghosts
			.filter(g => g.idStaff > 0)
			.forEach(g => {
				const related = staff.find(s => s.idStaff === g.idStaff);
				if (!related) {
					console.log ('WTF : Cannot retrieve the staff %d connected to the ghost %s', g.idStaff, g.pseudo);
					g.staffRelated = new Collaborator();
					g.staffRelated.idStaff = g.idStaff;
					g.staffRelated.login = '?';
					g.staffRelated.firstName = 'unknown';
					g.staffRelated.lastName = 'unknown';
				} else {
					g.staffRelated = related;
				}
			});
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
		const indexPseudo = this.ghosts.findIndex(ghost => pseudo === ghost.pseudo);
		if (indexPseudo === -1) {
			console.error ('Pseudo ' + pseudo + ' has disappeared from the ghosts list');
		} else {
			if (traceOn()) {
				console.log('the pseudo %s has been removed @ %d.', pseudo, indexPseudo);
			}
			this.ghosts.splice(indexPseudo, 1);
		}
		this.update(this.ghosts);
	}

	/**
	 * Update a ghost with the given staff identifier
	 * @param pseudo the ghost pseudo who has been linked to a current developer.
	 * @param idStaff staff identifier
	 */
	updateStaffMember (pseudo: string, idStaff: number) {
		const indexPseudo = this.ghosts.findIndex(ghost => pseudo === ghost.pseudo);
		if (indexPseudo === -1) {
			console.error ('Pseudo ' + pseudo + ' has disappeared from the ghosts list');
		} else {
			if (traceOn()) {
				console.log('the pseudo %s has been removed @ %d.', pseudo, indexPseudo);
			}
			this.ghosts[indexPseudo].idStaff = idStaff;
		}
		this.update(this.ghosts);
	}

}
