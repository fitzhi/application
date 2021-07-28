import { Component, OnInit, Input, OnDestroy, AfterViewInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Library } from '../../../data/library';
import { DependenciesDataSource } from './DependenciesDataSource';
import { InLineEditDialogComponent } from './in-line-edit-dialog/in-line-edit-dialog.component';
import { ProjectService } from '../../../service/project/project.service';
import { take } from 'rxjs/operators';
import { Observable, BehaviorSubject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { BaseDirective } from 'src/app/base/base-directive.directive';

@Component({
	selector: 'app-table-dependencies',
	templateUrl: './table-dependencies.component.html',
	styleUrls: ['./table-dependencies.component.css']
})
export class TableDependenciesComponent extends BaseDirective implements OnInit, AfterViewInit, OnDestroy {

	private idProject: number;

	public dataSource: DependenciesDataSource;

	public displayedColumns: string[] = ['path', 'type', 'actionsColumn'];

	constructor(private dialog: MatDialog, public projectService: ProjectService) {
		super();
	}

	ngOnInit() {
		this.dataSource = new DependenciesDataSource([]);
	}

	ngAfterViewInit() {
		this.projectService.projectLoaded$.subscribe({
			next: doneAndOk => {
				if (doneAndOk) {
					this.idProject = this.projectService.project.id;
					//
					// We postpone the datasource updates to avoid the warning
					// ExpressionChangedAfterItHasBeenCheckedError: Expression has changed after it was checked.
					//
					setTimeout(() => {
						this.dataSource.update(this.projectService.project.libraries);
					}, 0);
				}
			}
		});
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
