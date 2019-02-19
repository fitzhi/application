import { Component, OnInit } from '@angular/core';
import { ProjectStaffService } from '../../project-staff-service/project-staff.service';
import { Constants } from '../../../constants';
import { Contributor } from '../../../data/contributor';
import { MatDialogRef } from '@angular/material';
import { SettingsGeneration } from '../../../data/settingsGeneration';
import { FormGroup, FormControl } from '@angular/forms';

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
    if (Constants.DEBUG) {
      console.group('Contributors list');
      this.projectStaffService.contributors.forEach(entry => {
        console.log(entry.fullname);
      });
      console.groupEnd();
      this.filters.get('startingDate').setValue('');
      this.filters.get('idStaffSelected').setValue('');
    }

    this.contributors = this.projectStaffService.contributors;
  }

  get idStaffSelected(): any {
    return this.filters.get('idStaffSelected');
  }

  get startingDate(): any {
    return this.filters.get('startingDate');
  }

  submit() {
    const startingDate = this.filters.get('startingDate').value;
    const idStaffSelected = this.filters.get('idStaffSelected').value;
    if (Constants.DEBUG) {
      console.log('idStaffSelected ' + idStaffSelected + ' / startingDate : ' + startingDate);
    }
    const settings = new SettingsGeneration(0, startingDate.getTime(), idStaffSelected);
    this.dialogRef.close (settings);
  }
}
