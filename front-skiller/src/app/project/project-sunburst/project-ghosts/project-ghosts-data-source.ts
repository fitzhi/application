import { DataSource } from '@angular/cdk/table';
import { BehaviorSubject, Observable } from 'rxjs';
import { CollectionViewer } from '@angular/cdk/collections';
import { Project } from '../../../data/project';
import { Unknown } from '../../../data/unknown';
import { MatTableDataSource } from '@angular/material/table';

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
			g.fullName = unknown.fullName;
			g.technical = unknown.technical;
			g.action = unknown.action;
			if ((g.action === 'N') && (g.idStaff === -1) && !g.technical) {
				g.fullName = 'Unrecognized login';
			}
			g.firstname = '';
			g.lastname = '';
			g.active = false;
			g.external = false;
			ghosts.push(g);
		});
		this.data = ghosts;
	}

	/**
	 * @param technical value of the check-box "technical"
	 * @returns a string representation of the technical
	 */
	public checkValue(_technical: boolean): string {
		return;
	}

}
