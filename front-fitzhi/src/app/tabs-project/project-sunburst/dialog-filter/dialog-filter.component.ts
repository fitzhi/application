import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { traceOn } from 'src/app/global';
import { Contributor } from '../../../data/contributor';
import { SettingsGeneration } from '../../../data/settingsGeneration';
import { ProjectStaffService } from '../../project-staff-service/project-staff.service';

@Component({
	selector: 'app-dialog-filter',
	templateUrl: './dialog-filter.component.html',
	styleUrls: ['./dialog-filter.component.css']
})
export class DialogFilterComponent implements OnInit {

	public contributors: Contributor[];

	public filters = new FormGroup({
		startingDate: new FormControl(''),
		idStaffSelected: new FormControl('')
	});

	constructor(
		private projectStaffService: ProjectStaffService,
		private dialogRef: MatDialogRef<DialogFilterComponent>) { }

	ngOnInit() {
		if (traceOn()) {
			if (this.projectStaffService.contributors) {
				console.groupCollapsed('Contributors list');
				this.projectStaffService.contributors.forEach(entry => {
					console.log(entry.fullname);
				});
				console.groupEnd();
			}
		}

		this.filters.get('startingDate').setValue('');
		this.filters.get('idStaffSelected').setValue('');

		this.contributors = this.projectStaffService.contributors;
	}

	get idStaffSelected(): any {
		return this.filters.get('idStaffSelected');
	}

	get startingDate(): any {
		return this.filters.get('startingDate');
	}

	submit() {
		const startingDate = (this.filters.get('startingDate').value === '')
			? new Date(0) : this.filters.get('startingDate').value;
		const idStaffSelected = this.filters.get('idStaffSelected').value;
		if (traceOn()) {
			console.log('idStaffSelected ' + idStaffSelected + ' / startingDate : ' + startingDate);
		}
		const settings = new SettingsGeneration(0, startingDate.getTime(), idStaffSelected);
		this.dialogRef.close(settings);
	}
}
