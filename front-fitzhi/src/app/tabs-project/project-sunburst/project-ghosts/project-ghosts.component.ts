import { Component, OnInit, Inject, OnDestroy, Input } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { ProjectService } from '../../../service/project/project.service';
import { PseudoList } from '../../../data/PseudoList';
import { Constants } from '../../../constants';
import { MessageService } from '../../../interaction/message/message.service';
import { BaseDirective } from '../../../base/base-directive.directive';
import { Project } from 'src/app/data/project';

@Component({
	selector: 'app-project-ghosts',
	templateUrl: './project-ghosts.component.html',
	styleUrls: ['./project-ghosts.component.css']
})
export class ProjectGhostsComponent extends BaseDirective implements OnDestroy {

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

	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
