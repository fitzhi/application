import { Constants } from '../../../../constants';
import { DeclaredExperience } from '../../../../data/declared-experience';
import { ReturnCodeMessage } from '../../../../data/return_code_message';
import { StaffService } from '../../../../service/staff.service';
import { SelectionModel } from '@angular/cdk/collections';
import {Component, Inject, ViewChild, AfterViewInit} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { INTERNAL_SERVER_ERROR} from 'http-status-codes';

@Component({
	selector: 'app-uploaded-skills-pickup',
	templateUrl: './uploaded-skills-pickup.component.html',
	styleUrls: ['./uploaded-skills-pickup.component.css']
})
export class UploadedSkillsPickupComponent implements AfterViewInit {

	displayedColumns: string[] = ['select', 'title', 'occurrences'];

	/**
	 * Data source associated to the table.
	 */
	dataSource: MatTableDataSource<DeclaredExperience>;

	private selection: SelectionModel<DeclaredExperience>;

	@ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

	constructor(
		private staffService: StaffService,
		private dialogRef: MatDialogRef<UploadedSkillsPickupComponent>,
		@Inject(MAT_DIALOG_DATA) public data: any) {
		this.dataSource = new MatTableDataSource<DeclaredExperience>(this.data.experience);
		this.selection = new SelectionModel<DeclaredExperience>(true, []);
	}

	ngAfterViewInit() {
		this.dataSource.paginator = this.paginator;
	}

	/**
	 * Whether the number of selected elements matches the total number of rows.
	 */
	isAllSelected() {
		const numSelected = this.selection.selected.length;
		const numRows = this.dataSource.data.length;
		return numSelected === numRows;
	}

	/**
	 * Selects all rows if they are not all selected; otherwise clear selection.
	 */
	masterToggle() {
		this.isAllSelected() ?
				this.selection.clear() :
				this.dataSource.data.forEach(row => this.selection.select(row));
	}

	submit() {
		if (Constants.DEBUG) {
			console.log ('Submiting the selected skills.');
			let ind = 0;
			do {
				console.log ('Adding skill ' + this.selection.selected[ind].idSkill + ' ' + this.selection.selected[ind].title);
				ind++;
			} while (ind < this.selection.selected.length);
		}

		this.staffService.addDeclaredExperience (this.data.idStaff, this.selection.selected)
			.subscribe(
				staffDTO => {
					const rcm = new ReturnCodeMessage(staffDTO.code, staffDTO.message);
					this.dialogRef.close(rcm);
				},
				response => {
						if (response.status === INTERNAL_SERVER_ERROR) {
							if (Constants.DEBUG) {
								console.log('500 : Error returned ' + response.error.message);
							}
							const rcm = new ReturnCodeMessage(response.error.code, response.error.message);
							this.dialogRef.close(rcm);
						} else {
							console.error(response.error);
							const rcm = new ReturnCodeMessage(-1, 'Enormous');
							this.dialogRef.close(rcm);
						}
				});
	}
}
