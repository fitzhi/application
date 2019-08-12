import { Component, OnInit, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Library } from '../../../data/library';
import { DependenciesDataSource } from './DependenciesDataSource';
import { InLineEditDialogComponent } from './in-line-edit-dialog/in-line-edit-dialog.component';
import { ProjectService } from '../../../service/project.service';
import { take } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { Project } from 'src/app/data/project';

@Component({
	selector: 'app-table-dependencies',
	templateUrl: './table-dependencies.component.html',
	styleUrls: ['./table-dependencies.component.css']
})
export class TableDependenciesComponent implements OnInit {

	@Input ('project$') project$: Observable<Project>;

	private idProject: number;

	public dataSource: DependenciesDataSource;

	public displayedColumns: string[] = ['path', 'type', 'actionsColumn'];

	constructor(private dialog: MatDialog, public projectService: ProjectService) { }

	ngOnInit() {
		this.dataSource = new DependenciesDataSource([]);
		this.project$.pipe(take(1)).subscribe(prj => {
			this.idProject = prj.id;
			this.dataSource.update(prj.libraries);
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

}
