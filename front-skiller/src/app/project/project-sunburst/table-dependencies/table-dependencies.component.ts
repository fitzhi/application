import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Library } from '../../../data/library';
import { DependenciesDataSource } from './DependenciesDataSource';
import { InLineEditDialogComponent } from './in-line-edit-dialog/in-line-edit-dialog.component';
import { ProjectService } from '../../../service/project.service';

@Component({
	selector: 'app-table-dependencies',
	templateUrl: './table-dependencies.component.html',
	styleUrls: ['./table-dependencies.component.css']
})
export class TableDependenciesComponent implements OnInit {

	public dataSource: DependenciesDataSource;

	public displayedColumns: string[] = ['path', 'type', 'actionsColumn'];

	private libraries: Library[] = [];

	constructor(private dialog: MatDialog, public projectService: ProjectService) { }

	ngOnInit() {
		this.dataSource = new DependenciesDataSource(this.libraries);
	}

	editDependency(library: Library) {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.disableClose = true;
		dialogConfig.data = { initialValue: library.exclusionDirectory }
		const dialogRef = this.dialog.open(InLineEditDialogComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(newPath => {
			this.dataSource.updatePath(library, newPath);
			this.projectService.libDirSave(5, this.libraries);
		});
	}

	removeDependency(library: Library) {
		this.dataSource.remove(library);
		this.projectService.libDirSave(5, this.libraries);
	}

}
