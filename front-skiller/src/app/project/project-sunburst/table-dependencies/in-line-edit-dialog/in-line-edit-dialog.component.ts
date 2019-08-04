import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { BehaviorSubject } from 'rxjs';
import { ProjectService } from '../../../../service/project.service';

@Component({
	selector: 'app-in-line-edit-dialog',
	templateUrl: './in-line-edit-dialog.component.html',
	styleUrls: ['./in-line-edit-dialog.component.css']
})
export class InLineEditDialogComponent implements OnInit {

	searchPath$ = new BehaviorSubject<string>('');

	pathSearched: string;

	public paths: string[] = [];

	constructor(
		private dialogRef: MatDialogRef<InLineEditDialogComponent>,
		private projectService: ProjectService,
		@Inject(MAT_DIALOG_DATA) private data) {
		this.searchPath$.next(data.initialValue);
	}

	ngOnInit() {
	}

	changePath($event: string) {
		this.paths = this.paths.filter(s => s.toLowerCase().startsWith($event));
		this.pathSearched = $event;
		this.searchPath$.next($event);
		return this.projectService.libDirLookup(5, $event)
			.subscribe(res => this.paths = res);
	}

	closeIfEnter(event: KeyboardEvent) {

		if ((event.code === 'Tab') && (!event.shiftKey)) {
			const currentList = this.paths
				.filter(s => s.toLowerCase()
					.startsWith(this.pathSearched.toLowerCase()));
			if (currentList.length === 1) {
				this.searchPath$.next(this.paths[0]);
			}
		}

		if (event.code === 'Enter') {
			this.searchPath$.subscribe(value => {
				if (value !== '') {
					this.dialogRef.close(value);
				}
			});
		}
	}

}
