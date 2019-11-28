import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Library } from '../../../data/library';
import { DependenciesDataSource } from './DependenciesDataSource';
import { InLineEditDialogComponent } from './in-line-edit-dialog/in-line-edit-dialog.component';
import { ProjectService } from '../../../service/project.service';
import { take } from 'rxjs/operators';
import { Observable, BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { BaseComponent } from 'src/app/base/base.component';

@Component({
	selector: 'app-table-dependencies',
	templateUrl: './table-dependencies.component.html',
	styleUrls: ['./table-dependencies.component.css']
})
export class TableDependenciesComponent extends BaseComponent implements OnInit {

	@Input() project$: BehaviorSubject<Project>;

	private idProject: number;

	public dataSource: DependenciesDataSource;

	public displayedColumns: string[] = ['path', 'type', 'actionsColumn'];

	constructor(private dialog: MatDialog, public projectService: ProjectService) {
		super();
	}

	ngOnInit() {
		this.dataSource = new DependenciesDataSource([]);
		if (this.project$) {
			this.subscriptions.add(
				this.project$.subscribe(project => {

					// The behaviorSubject project$ is initialized with a null.
					if (!project) {
						return;
					}

					this.idProject = project.id;
					this.dataSource.update(project.libraries);
				}));
		}
	}

	editDependency(library: Library) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.data = { initialValue: library.exclusionDirectory, idProject: this.idProject };
		const dialogRef = this.dialog.open(InLineEditDialogComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(newPath => {
			this.dataSource.updatePath(library, newPath);
			this.projectService.libDirSave(this.idProject, this.dataSource.getLibraries());
		});
	}

	removeDependency(library: Library) {
		this.dataSource.remove(library);
		this.projectService.libDirSave(this.idProject, this.dataSource.getLibraries());
	}

	/**
	* Calling the base class to unsubscribe all subscriptions.
	*/
	ngOnDestroy() {
		super.ngOnDestroy();
	}

}
