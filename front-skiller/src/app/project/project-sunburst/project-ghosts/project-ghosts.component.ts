import { Component, OnInit, Inject, OnDestroy, Input } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { ProjectService } from '../../../service/project.service';
import { PseudoList } from '../../../data/PseudoList';
import { Constants } from '../../../constants';
import { MessageService } from '../../../message/message.service';
import { BaseComponent } from '../../../base/base.component';
import { Project } from 'target/classes/app/data/project';

@Component({
	selector: 'app-project-ghosts',
	templateUrl: './project-ghosts.component.html',
	styleUrls: ['./project-ghosts.component.css']
})
export class ProjectGhostsComponent extends BaseComponent implements OnDestroy {

	/**
	 * Observable to the active project.
	 */
	@Input() dataSourceGhosts$;

	private updatedData: ProjectGhostsDataSource;

	private dialogReference: MatDialogRef<any, any>;

	constructor(
		private projectService: ProjectService,
		private messageService: MessageService) {
		super();
	}

	public submit() {
		/*
		this.subscriptions.add(
			this.projectService.saveGhosts(new PseudoList(
				this.dataSource.project.id,
				this.dataSource.ghostsSubject.getValue()))
				.subscribe(pseudoList => {
					if (Constants.DEBUG) {
						console.group('[Response from /project/api-ghosts] peudoList');
						console.log('idProject ' + pseudoList.idProject);
						pseudoList.unknowns.forEach(function (value) { console.log(value); });
						console.groupEnd();
					}
					this.updatedData = new ProjectGhostsDataSource(this.dataSource.project);
					this.updatedData.sendUnknowns(pseudoList.unknowns);
				},
					responseInError => {
						if (Constants.DEBUG) {
							console.log('Error ' + responseInError.error.code + ' ' + responseInError.error.message);
						}
						this.messageService.error(responseInError.error.message);
					}));
					*/
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
