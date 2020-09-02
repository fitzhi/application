import { Project } from '../../../data/project';
import { Unknown } from '../../../data/unknown';
import { MatTableDataSource } from '@angular/material/table';
import { Constants } from '../../../constants';
import { Collaborator } from 'src/app/data/collaborator';
import { traceOn } from 'src/app/global';
import { BehaviorSubject } from 'rxjs';

export class ProjectGhostsDataSource extends MatTableDataSource<Unknown> {


	public ghosts: Unknown[];

	public ghosts$ = new BehaviorSubject<Unknown[]>([]);

	public project: Project;

	/**
     * @param project current project
     * @param ghosts list of unregistered contributors.
     */
	constructor(project: Project, ghosts: Unknown[]) {
		super();
		this.project = project;
		if (traceOn()) {
			console.groupCollapsed	(ghosts.length + ' ghosts identified');
			ghosts.forEach(g => {
				console.log (g.pseudo, (g.technical ? 'technical' : g.idStaff));
			});
			console.groupEnd();
		}
		this.ghosts = ghosts;
		this.ghosts$.next(this.ghosts);
	}

	connect(): BehaviorSubject<Unknown[]> {
		return this.ghosts$;
	}

	/**
	 * Update the datasource with new data.
	 * @param ghosts the new ghosts to be displayed.
	 */
	update(ghosts: Unknown[]) {
		this.ghosts = ghosts;
		this.ghosts$.next(this.ghosts);
	}

	/**
	 * @param pseudo remove the ghost associated to the passed pseudo.
	 */
	removePseudo (pseudo: string) {
		const indexPseudo = this.ghosts.findIndex(ghost => pseudo === ghost.pseudo);
		if (indexPseudo === -1) {
			console.error ('Pseudo ' + pseudo + ' has disappeared from ths ghosts list');
		}
		this.ghosts.splice(indexPseudo, 1);
		this.ghosts$.next(this.ghosts);
	}
}
