import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatSort } from '@angular/material';
import { Project } from '../../../data/project';
import { Constants } from '../../../constants';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { Ghost } from '../../../data/Ghost';
import { BehaviorSubject } from 'rxjs';
import { DataSource } from '@angular/cdk/table';

@Component({
  selector: 'app-dialog-project-ghosts',
  templateUrl: './dialog-project-ghosts.component.html',
  styleUrls: ['./dialog-project-ghosts.component.css']
})
export class DialogProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  public dataSource: ProjectGhostsDataSource;

  constructor(
    public dialogRef: MatDialogRef<DialogProjectGhostsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectGhostsDataSource) { }

  ngOnInit() {
    this.dataSource = this.data;
  }

  public submit() {
    this.dialogRef.close(this.dataSource.ghostsSubject.getValue());
  }
}
