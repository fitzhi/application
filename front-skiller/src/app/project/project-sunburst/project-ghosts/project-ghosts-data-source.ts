import { Project } from '../../../data/project';
import { Unknown } from '../../../data/unknown';
import { MatTableDataSource } from '@angular/material/table';
import { Constants } from '../../../constants';
import { Collaborator } from 'src/app/data/collaborator';

export class ProjectGhostsDataSource extends MatTableDataSource<Unknown> {


	public project: Project;

	/**
     * @param project current project
     */
	constructor(project: Project) {
		super();
		this.project = project;
	}

	/**
     * Send the loaded data from the backend.
     * @param unknowns list of unregistered contributors.
     */
	sendUnknowns(unknowns: Unknown[]): void {
		const ghosts = [];
		unknowns.forEach(function (unknown) {
			const g = new Unknown();
			g.pseudo = unknown.pseudo;
			g.idStaff = unknown.idStaff;
			g.login = unknown.login;
			g.technical = unknown.technical;
			g.action = unknown.action;
			g.firstname = '';
			g.lastname = '';
			g.active = false;
			g.external = false;
			g.staffRelated = new Collaborator();
			g.staffRecorded = false;
			ghosts.push(g);
		});
		this.data = ghosts;
		if (Constants.DEBUG) {
			console.groupCollapsed	(ghosts.length + ' ghosts identified');
			ghosts.forEach(g => {
				console.log (g.pseudo, (g.technical ? 'technical' : g.idStaff));
			});
			console.groupEnd();
		}
	}

	/**
	 * @param pseudo remove the ghost associated to the passed pseudo.
	 */
	removePseudo (pseudo: string) {
		const indexPseudo = this.data.findIndex(ghost => pseudo === ghost.pseudo);
		if (indexPseudo === -1) {
			console.error ('Pseudo ' + pseudo + ' has disappeared from ths ghosts list');
		}
		this.data.splice(indexPseudo, 1);
	}
}
